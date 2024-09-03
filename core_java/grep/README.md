# Introduction

Grep is a Java application that mimics Linux grep command which allows users to search matching strings from files. 
This application provides two effective methods for searching patterns within text files. 
The Stream-Based Approach leverages Java Streams and functional programming, enabling the processing of large datasets 
with minimal memory consumption, making it suitable for very large files. 
The Traditional Approach relies on standard file I/O and looping, which is more appropriate for moderately-sized files. 
Comprehensive unit testing with JUnit ensures the application's accuracy. 
Dockerizing the application simplifies distribution and deployment.

# Quick Start
To use this application pull the given repository and then install the package from the 
grep-1.0-SNAPSHOT.jar file. 

# Implemenation

## Pseudocode
`process` method pseudocode.
```
matchedLines = []
for file in listFilesRecursively(rootDir)
  for line in readLines(file)
      if containsPattern(line)
        matchedLines.add(line)
writeToFile(matchedLines)
```

## Performance Issue
When trying to run the java application by loading large amount of data into memory by using this command would produce 
a `OutOfMemoryError`. The reason this error occurred is that when trying to process a large amount of data
a heap limit has been exceeded. 
```markdown
java -Xms5m -Xmx5m \
-cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.grep.JavaGrepImp \
.*Romeo.*Juliet.* ./data ./out/grep.txt
```
To fix this error you can allocate more memory when running this application by increasing 
the maximum heap size this application can have by running this command. 
```markdown
java -Xms5m -Xmx25m \
-cp target/grep-1.0-SNAPSHOT.jar ca.jrvs.apps.grep.JavaGrepImp \
.*Romeo.*Juliet.* ./data ./out/grep.txt
```

# Test
How did you test your application manually? (e.g. prepare sample data, run some test cases manually, compare result)
I tested this application by using JUnit. First I created a bash script `script.sh` to generate custom
folders and text files which would contain test data. These folders would not be created if the folders already
exists. This is done during the @BeforeClass decorator, which will ensure the necessary test folders and files
would be generated before running each individual unit tests. 

Once all the test folders and files have been initialized. Unit tests would be created which would test each functionality of 
the application. The first tests that is written is testing the setters/getters of the main Java Grep application.
This will ensure that these functionalities will behave properly and can continue with the main methods that are being used
in the process method (running the entire application).  

# Deployment
The application is Dockerized for easier distribution. A Dockerfile is used to create a Docker image containing the Java application. 
The image can be built and run using Docker commands, enabling consistent execution across different environments.

# Improvement
1. Improving performance to handle larger files (This can be done by doing techniques like parallel processing to handle larger datasets more efficiently)
2. Adding a UI component to the application
3. Handling different types of file formats (currently it only handle text files)