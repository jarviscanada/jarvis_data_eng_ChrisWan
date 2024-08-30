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