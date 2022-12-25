--Kevin Gao 862138776
--Aditi Behera 862138359
COPY USR(userId, password, email, name, dateOfBirth)
FROM '/tmp/CS166_Project/data/USR.csv'
DELIMITER ','
CSV HEADER;

COPY MESSAGE(msgId, senderId, receiverId, contents, sendTime, deleteStatus, status)
FROM '/tmp/CS166_Project/data/message.csv'
DELIMITER ','
CSV HEADER;

COPY CONNECTION_USR(userId, connectionId, status)
FROM '/tmp/CS166_Project/data/connection.csv'
DELIMITER ','
CSV HEADER;

COPY WORK_EXPR(userId, company, role, location, startDate, endDate)
FROM '/tmp/CS166_Project/data/work_ex.csv'
DELIMITER ','
CSV HEADER;

COPY EDUCATIONAL_DETAILS(userId, instituitionName, major, degree, startdate, enddate)
FROM '/tmp/CS166_Project/data/edu_det.csv'
DELIMITER ','
CSV HEADER;