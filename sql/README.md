
# Introduction 
The purpose of this project is to learn SQL and RDBMS so I can solve real world problems with data. This was done
through the use of Docker and Postgres. Docker ws used to setup a PSQL instance then utilizing PSQL DDL statements
to model and generate a database table. Then sample data was loaded into the database through the usage of bash
and psql command. This dataset includes newly created country clubs, with a set of members  facilities such as 
tennis courts, and booking history for those facilities. Base off this dataset custom queries were written 
to anaylize and modify the existing data to provide information that will be useful for the user. These queries included
CRUD data (INSERT, UPDATE, DELETE), basic sql statmenets (e.g. SELECT, WHERE, FROM, etc.), Joins, Aggreation (e.g. GROUP BY,
window functions and aggregation functions), and string functions. 

# SQL Quries

###### Table Setup (DDL)

```sql
CREATE TABLE IF NOT EXISTS cd.members (
    memid INTEGER PRIMARY KEY NOT NULL,
    surname VARCHAR(200) NOT NULL,
    firstname VARCHAR(200) NOT NULL,
    address VARCHAR(300) NOT NULL,
    zipcode INTEGER NOT NULL,
    telephone VARCHAR(20) NOT NULL,
    recommendedby INTEGER NOT NULL,
    joindate TIMESTAMP NOT NULL,
    CONSTRAINT fk_members_recommendedby FOREIGN KEY (recommendedby)
    REFERENCES cd.members(memid) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS cd.bookings (
    bookid INTEGER PRIMARY KEY NOT NULL,
    facid INTEGER NOT NULL REFERENCES cd.facilities(facid),
    memid INTEGER NOT NULL REFERENCES cd.members(memid),
    starttime TIMESTAMP NOT NULL,
    slots INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS cd.facilities (
    facid INTEGER PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    membercost NUMERIC NOT NULL,
    guestcost NUMERIC NOT NULL,
    initaloutlay NUMERIC NOT NULL,
    monthlymaintenance NUMERIC NOT NULL
);
```

###### Question 1: Insert some data into a table
```markdown
The club is adding a new facility - a spa. We need to add it into the facilities table. Use the following values:
facid: 9, Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800
```

```sql 
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);
```

###### Question 2: Insert calculated data into a table
```markdown
We want to automatically generate the value for the next facid, rather than specifying it as a constant.
Use the following values for everything else:
Name: 'Spa', membercost: 20, guestcost: 30, initialoutlay: 100000, monthlymaintenance: 800.
```

```sql 
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES ((SELECT max(facid) from cd.facilities) + 1, 'Spa', 20, 30, 100000, 800);
```

###### Question 3: Update some existing data
```markdown
We made a mistake when entering the data for the second tennis court. The initial outlay was 10000 rather than 8000: 
you need to alter the data to fix the error.
```

```sql 
UPDATE cd.facilities SET initialoutlay=10000 WHERE name LIKE '%Tennis Court 2%';
```

###### Question 4: Update a row based on the contents of another row
```markdown
We want to alter the price of the second tennis court so that it costs 10% more than the first one.
```

```sql 
UPDATE cd.facilities
SET
	membercost=(SELECT membercost FROM cd.facilities WHERE name LIKE '%Tennis Court 1%') * 1.1,
	guestcost=(SELECT guestcost FROM cd.facilities WHERE name LIKE '%Tennis Court 1%') * 1.1

WHERE name LIKE '%Tennis Court 2%';
```

###### Question 5: Delete all bookings
```markdown
As part of a clearout of our database, we want to delete all bookings from the cd.bookings table
```

```sql 
DELETE FROM cd.bookings;
```

###### Question 6: Delete a member from the cd.members table
```markdown
We want to remove member 37, who has never made a booking, from our database. 
```

```sql 
DELETE FROM cd.members WHERE memid=37;
```

###### Question 7: Control which rows are retrieved
```markdown
How can you produce a list of facilities that charge a fee to members, and that fee is less than 1/50th of the monthly maintenance cost? Return the facid, 
facility name, member cost, and monthly maintenance of the facilities in question.
```

```sql 
SELECT facid, name, membercost, monthlymaintenance FROM cd.facilities WHERE membercost<(monthlymaintenance * 0.02) AND membercost >0;
```

###### Question 8: Basic string searches
```markdown
How can you produce a list of all facilities with the word 'Tennis' in their name?
```

```sql 
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';
```

###### Question 9: Matching against multiple possible values
```markdown
How can you retrieve the details of facilities with ID 1 and 5?
```

```sql 
SELECT * FROM cd.facilities WHERE facid IN (1, 5);
```



###### Question 10: Working with dates
```markdown
How can you produce a list of members who joined after the start of September 2012? Return the memid, surname, 
firstname, and joindate of the members in question.
```

```sql 
SELECT memid, surname, firstname, joindate FROM cd.members WHERE joindate>='2012-09-01'::date;
```

###### Question 11: Combining results from multiple queries
```markdown
You, for some reason, want a combined list of all surnames and all facility names.
```

```sql 
SELECT surname FROM cd.members UNION SELECT name FROM cd.facilities;
```

###### Question 12: Retrieve the start times of members' bookings
```markdown
How can you produce a list of the start times for bookings by members named 'David Farrell'?
```

```sql 
SELECT starttime FROM cd.bookings INNER JOIN cd.members ON cd.bookings.memid=cd.members.memid WHERE firstname='David' AND surname='Farrell';
```



###### Question 13: Work out the start times of bookings for tennis courts
```markdown
How can you produce a list of the start times for bookings for tennis courts, for the date '2012-09-21'? Return a list 
of start time and facility name pairings, ordered by the time.
```

```sql 
SELECT starttime, name FROM cd.bookings INNER JOIN cd.facilities
ON cd.bookings.facid=cd.facilities.facid 
WHERE cd.bookings.starttime >= '2012-09-21' AND cd.bookings.starttime < '2012-09-22' 
AND cd.facilities.name LIKE 'Tennis%' ORDER BY starttime;
```


###### Question 14: Produce a list of all members, along with their recommender
```markdown
How can you output a list of all members, including the individual who recommended them (if any)? Ensure 
that results are ordered by (surname, firstname).
```

```sql 
SELECT cd.members.firstname as memfname, cd.members.surname as memsname, recs.firstname as recfname, recs.surname as recsname
FROM cd.members LEFT OUTER JOIN cd.members recs ON recs.memid = cd.members.recommendedby
ORDER BY memsname, memfname;
```

###### Question 15: Produce a list of all members who have recommended another member
```markdown
How can you output a list of all members who have recommended another member? Ensure that there are no duplicates in the list, 
and that results are ordered by (surname, firstname).
```

```sql 
SELECT DISTINCT recs.firstname, recs.surname FROM cd.members INNER JOIN cd.members recs
ON recs.memid=cd.members.recommendedby ORDER BY surname, firstname; 
```

###### Question 16: Produce a list of all members, along with their recommender, using no joins.
```markdown
How can you output a list of all members, including the individual who recommended them (if any), without using any joins? Ensure that there are no duplicates in the list, 
and that each firstname + surname pairing is formatted as a column and ordered.
```

```sql 
SELECT (SELECT CONCAT(firstname, ' ', surname) recommender FROM cd.members WHERE cd.members.recommendedby=cd.members.memid)
FROM cd.members
```


###### Question 17: Count the number of recommendations each member makes.
```markdown
Produce a count of the number of recommendations each member has made. Order by member ID.
```

```sql 
SELECT recommendedby, COUNT(*) FROM cd.members WHERE recommendedby IS NOT NULL GROUP BY recommendedby 
ORDER BY recommendedby;
```

###### Question 18: List the total slots booked per facility
```markdown
Produce a list of the total number of slots booked per facility. For now, just produce an output table consisting of facility id and slots, sorted by facility id.
```

```sql 
SELECT facid, SUM(slots) as "Total Slots" FROM cd.bookings  GROUP BY facid
ORDER BY facid;
```

###### Question 19: List the total slots booked per facility in a given month
```markdown
Produce a list of the total number of slots booked per facility in the month of September 2012. 
Produce an output table consisting of facility id and slots, sorted by the number of slots.
```

```sql 
SELECT facid, SUM(slots) as "Total Slots" FROM cd.bookings WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid 
ORDER BY SUM(slots);
```

###### Question 20: List the total slots booked per facility per month
```markdown
Produce a list of the total number of slots booked per facility per month in the year of 2012. 
Produce an output table consisting of facility id and slots, sorted by the id and month.
```

```sql 
SELECT facid, extract(month from starttime) as month, sum(slots) as "Total Slots"
FROM cd.bookings WHERE extract(year from starttime) = 2012 
GROUP BY facid, month 
ORDER BY facid, month;
```





###### Question 21: Find the count of members who have made at least one booking
```markdown
Find the total number of members (including guests) who have made at least one booking.
```

```sql 
SELECT COUNT(distinct memid) from cd.bookings; 
```



###### Question 22: List each member's first booking after September 1st 2012
```markdown
Produce a list of each member name, id, and their first booking after September 1st 2012. Order by member ID.
```

```sql 
SELECT surname, firstname, member.memid, min(books.starttime) AS starttime FROM cd.members member 
INNER JOIN cd.bookings books ON books.memid=member.memid
WHERE books.starttime >= '2012-09-01' 
GROUP BY member.memid
ORDER BY memid;
```

###### Question 23: Produce a list of member names, with each row containing the total member count
```markdown
Produce a list of member names, with each row containing the total member count. Order by join date, and include guest members.
```

```sql 
SELECT (SELECT COUNT(*) FROM cd.members) as count, firstname, surname FROM cd.members
ORDER BY joindate;
```

###### Question 24: Produce a numbered list of members
```markdown
Produce a monotonically increasing numbered list of members (including guests), ordered by their date of joining. Remember that member IDs are not guaranteed to be sequential.
```

```sql 
SELECT COUNT(*) OVER(ORDER BY joindate), firstname, surname FROM cd.members
ORDER BY joindate;
```

###### Question 25: Output the facility id that has the highest number of slots booked, again
```markdown
Output the facility id that has the highest number of slots booked. Ensure that in the event of a tie, all tieing results get output.
```

```sql 
SELECT facid, total FROM 
(SELECT facid, SUM(slots) total, rank() OVER (ORDER BY SUM(slots) DESC) rank
FROM cd.bookings GROUP BY facid) as ranked WHERE rank = 1; 
```

###### Question 26: Format the names of members
```markdown
Output the names of all members, formatted as 'Surname, Firstname'
```

```sql 
SELECT CONCAT(surname, ', ', firstname) as name FROM cd.members;
```

###### Question 27: Find telephone numbers with parentheses
```markdown
You've noticed that the club's member table has telephone numbers with very inconsistent formatting. 
You'd like to find all the telephone numbers that contain parentheses, returning the member ID and telephone number sorted by member ID.
```

```sql 
SELECT memid, telephone FROM cd.members WHERE telephone SIMILAR TO '%[()]%';
```



###### Question 28: Count the number of members whose surname starts with each letter of the alphabet
```markdown
You'd like to produce a count of how many members you have whose surname starts with each letter of the alphabet. 
Sort by the letter, and don't worry about printing out a letter if the count is 0.
```

```sql 
SELECT SUBSTR(surname, 1, 1) as letter, count(*) as count FROM cd.members
GROUP BY letter ORDER BY letter;
```