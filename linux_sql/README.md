# Introduction
This project aims to record the hardware specification of each node and monitor node resource usage (e.g. CPU, memory usage)
in real-time. The collected data would then later be stored in a RDBMS (Postgres), where the data
can be used in a later state to generate reports for future resource planning purposes. 

The primary users of this project are the Jarvis Linux Cluster Administration (LCA) team where they are 
responsible for managing Linux clusters of 10 nodes/servers running a Rocky Linux OS. 

To build this project, I utilized several key technologies
- Bash (For scripting and automating)
- Docker (To containerize the application, ensuring consistency across development)
- Git (For version control and collaboration)
- Postgres (For data management)
- Crontab (A job scheduler)


# Quick Start
Quick-start commands
- Start a psql instance using psql_docker.sh
```bash
# start a container
docker container start jrvs-psql
```
- Create tables using ddl.sql
```bash
# Execute ddl.sql script on the host_agent database againse the psql instance
psql -h localhost -U postgres -d host_agent -f sql/ddl.sql
```
- Insert hardware specs data into the DB using host_info.sh
```bash
./scripts/host_info.sh "localhost" 5432 "host_agent" "postgres" "mypassword"
```
- Insert hardware usage data into the DB using host_usage.sh
```bash
bash scripts/host_usage.sh localhost 5432 host_agent postgres password
```
- Crontab setup
```bash
* * * * * bash /home/rocky/dev/jarvis_data_eng_ChrisWan/linux_sql/scripts/host_usage.sh 
localhost 5432 host_agent postgres password > /tmp/host_usage.log
```

# Implemenation

To implement this project, I used the software development life cycle model to design, build, test, and deploy this software.

During the planning and requirement analysis phase I defined the scope and objectives of the project by gathering
the necessary requirements from the clients and target users. 

After having a clear outline on the requirements I converted each of these requirements into a work item that can be placed 
into the product backlog which will be used for each sprint (2-week sprints).

The first development process began with the environment setup. This involved setting up the version control with Git. 
Creating a docker container, setting up the Postgres database on Docker, creating the environmental variables that would be used, 
and creating the folder structure/directories that would be used through out the application (scripts and sql folders).

After setting up the project, I first began working with the database and created the tables that would be used to store 
necessary information such as hardware specifications (e.g. hostname, cpu_number, cpu_model, etc) and resource usage data
(e.g. memory_free, cpu_idle, disk_available, etc). I then created a ddl.sql script to generate each of these tables automatically.

Once the database tables were finalized, I then began working on the monitoring agents (host_info.sh and host_usage.sh). 
I started with host_info.sh because it allows me to collect the hardware specification of the host computer and because
I assume that the hardware specification are static, this script should only be executed once.

Following the completion of the host_info.sh script I started to work on the host_usage.sh script where it would be collecting 
the server usage data and inserting them into the psql database. This script is intended to execute every minute using Linux's
crontab program.

Having finished both the host_info.sh and host_usage.sh scripts I tested out the scripts to ensure it is working properly. After ensuring that
the scripts work properly, I set up Crontab to schedule the host_usage.sh to execute every 5 minutes.

Finally, after having a working, tested and deployable application, I deployed the product into production.

## Architecture
![Linux Cluster Monitor Image](https://github.com/jarviscanada/jarvis_data_eng_ChrisWan/assets/71847150/8b2ada9c-ed44-4728-a9ba-60ab74269f7f)

## Scripts
Shell script description and usage (use markdown code block for script usage)
- psql_docker.sh
```markdown
Captures the CLI arguments and switches cases to handle create|stop|start docker operations.
Can create a docker container, start the docker container or stop the docker container.
```
- host_info.sh
```markdown
Collects hardware specification data and then inserts the data into the psql instance.
It is executed only once.
```
- host_usage.sh
```markdown
Collects server usage and then inserts the data into the psql database.
Executed every minute using the Linux's crontab program.
```
- crontab
```markdown
Used to schedule a job for every minute. Executes the host_info.sh script every minute and extracts then inserts this data
into the psql database.
```

## Database Modeling
PSQL Database Table Schema
- `host_info`

| id | hostname | cpu_number | cpu_architecture | cpu_model | cpu_mhz | l2_cache | "timestamp" | total_mem |
|----|----------|------------|-----------------|-------|-------|----------|------------|-----------|
| 1  | 'jrvs-remote-desktop-centos7-6.us-central1-a.c.spry-framework-236416.internal'    | 2          |  'x86_64', 'Intel(R) Xeon(R) CPU @ 2.30GHz' | 2300  | 256   | 256      | '2024-06-29 17:49:53.000'    | 601324   |

- `host_usage`

| host_id | memory_free | cpu_idle | cpu_kernel | disk_io | disk_available |"timestamp" |
|---------|----------|----------|------------|---------|----------------|----------------|
| 1       | 256     | 95          | 0          | 0       | 31220            |'2024-06-29 17:49:53.000'           |

# Test
```markdown
ddl.sql prevented the user from generating a database table if it already exists 

Both host_info.sh and host_usage.sh prevents the user from executing the command if the number of arguments is not equal to 5.

psql_docker.sh prevents the user from executing the command if the number of arguments is not equal to 3. It does not run
if the container is already created when passing in the create command. And does not allow the user to start or stop the docker
container if the container isn't already created. 

All of these tests have successfully passed when executing and running the bash scripts.
```


# Deployment
Code was managed using version control systems like Git and the final production code is pushed onto Githun.
The package was containerized with their dependencies into containers with docker. And the application 
is deployed onto Google Cloud Platform (GCP) where it would there would exists and running instance of this application on a 
Virtual Machine (VM). 

# Improvements
- Currently only one psql database is being used to handle the insertion of data usage across linux servers. Therefore,
Scalability and performance optimizations can be made to ensure that if the team decides to horizontally scale the application 
to a large amount of Linux clusters, the database would be able to handle multiple concurrent commands from multiple systems at a scheduled rate. 
Some potential solutions can be enhancing the database schema and queries in PostgreSQL to handle increasing volumes of data efficiently and
a queue data structure than can store the order of the nodes that are passing in their data so the work of inserting the data into the psql database 
can be achieved asynchronously, allowing other operations and processes to be completed. 
- The system's security and monitoring can be improved by implementing encryption for data transmission between nodes and the database.
And a monitoring script can be created to track the application performance, health, and status in real-time.
- A user interface (GUI) can be created to allow the target users (Linux Cluster Administration) to insert and remove nodes
with ease and have a dashboard that showcases the reports of resource usage from each node cluster. 