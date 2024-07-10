-- Modifying Data

-- Q1
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES (9, 'Spa', 20, 30, 100000, 800);

-- Q2
INSERT INTO cd.facilities (facid, name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES ((SELECT max(facid) from cd.facilities) + 1, 'Spa', 20, 30, 100000, 800);

-- Q3
UPDATE cd.facilities SET initialoutlay=10000 WHERE name LIKE '%Tennis Court 2%';

-- Q4
UPDATE cd.facilities
SET
	membercost=(SELECT membercost FROM cd.facilities WHERE name LIKE '%Tennis Court 1%') * 1.1,
	guestcost=(SELECT guestcost FROM cd.facilities WHERE name LIKE '%Tennis Court 1%') * 1.1

WHERE name LIKE '%Tennis Court 2%';

-- Q5
DELETE FROM cd.bookings;

-- Q6
DELETE FROM cd.members WHERE memid=37;

-- Basics

-- Q1
SELECT facid, name, membercost, monthlymaintenance FROM cd.facilities WHERE membercost<(monthlymaintenance * 0.02) AND membercost >0;

-- Q2
SELECT * FROM cd.facilities WHERE name LIKE '%Tennis%';

-- Q3
SELECT * FROM cd.facilities WHERE facid IN (1, 5);

-- Q4
SELECT memid, surname, firstname, joindate FROM cd.members WHERE joindate>='2012-09-01'::date;

-- Q5
SELECT surname FROM cd.members UNION SELECT name FROM cd.facilities;

-- Join

-- Q1
SELECT starttime FROM cd.bookings INNER JOIN cd.members ON cd.bookings.memid=cd.members.memid WHERE firstname='David' AND surname='Farrell';

-- Q2
SELECT starttime, name FROM cd.bookings INNER JOIN cd.facilities
ON cd.bookings.facid=cd.facilities.facid
WHERE cd.bookings.starttime >= '2012-09-21' AND cd.bookings.starttime < '2012-09-22'
AND cd.facilities.name LIKE 'Tennis%' ORDER BY starttime;

-- Q3
SELECT cd.members.firstname as memfname, cd.members.surname as memsname, recs.firstname as recfname, recs.surname as recsname
FROM cd.members LEFT OUTER JOIN cd.members recs ON recs.memid = cd.members.recommendedby
ORDER BY memsname, memfname;

-- Q4
SELECT DISTINCT recs.firstname, recs.surname FROM cd.members INNER JOIN cd.members recs
ON recs.memid=cd.members.recommendedby ORDER BY surname, firstname;

-- Q5
SELECT DISTINCT CONCAT(firstname, ' ', surname) member,
(SELECT CONCAT(firstname, ' ', surname) recommender FROM cd.members recs WHERE cd.members.recommendedby=recs.memid)
FROM cd.members
ORDER BY member;

-- Aggregation

-- Q1
SELECT recommendedby, COUNT(*) FROM cd.members WHERE recommendedby IS NOT NULL GROUP BY recommendedby
ORDER BY recommendedby;

-- Q2
SELECT facid, SUM(slots) as "Total Slots" FROM cd.bookings  GROUP BY facid
ORDER BY facid;

-- Q3

SELECT facid, SUM(slots) as "Total Slots" FROM cd.bookings WHERE starttime >= '2012-09-01' AND starttime < '2012-10-01'
GROUP BY facid
ORDER BY SUM(slots);

-- Q4
SELECT facid, extract(month from starttime) as month, sum(slots) as "Total Slots"
FROM cd.bookings WHERE extract(year from starttime) = 2012
GROUP BY facid, month
ORDER BY facid, month;

-- Q5
SELECT COUNT(distinct memid) from cd.bookings;

-- Q6

SELECT surname, firstname, member.memid, min(books.starttime) AS starttime FROM cd.members member
INNER JOIN cd.bookings books ON books.memid=member.memid
WHERE books.starttime >= '2012-09-01'
GROUP BY member.memid
ORDER BY memid;

-- Q7

SELECT (SELECT COUNT(*) FROM cd.members) as count, firstname, surname FROM cd.members
ORDER BY joindate;

-- Q8

SELECT COUNT(*) OVER(ORDER BY joindate), firstname, surname FROM cd.members
ORDER BY joindate;

-- Q9
SELECT facid, total FROM
(SELECT facid, SUM(slots) total, rank() OVER (ORDER BY SUM(slots) DESC) rank
FROM cd.bookings GROUP BY facid) as ranked WHERE rank = 1;

-- String

-- Q1

SELECT CONCAT(surname, ', ', firstname) as name FROM cd.members;

-- Q2

SELECT memid, telephone FROM cd.members WHERE telephone SIMILAR TO '%[()]%';

-- Q3

SELECT SUBSTR(surname, 1, 1) as letter, count(*) as count FROM cd.members
GROUP BY letter ORDER BY letter;