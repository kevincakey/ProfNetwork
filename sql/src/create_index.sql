--Kevin Gao 862138776
--Aditi Behera 862138359
CREATE INDEX userId ON USR (userId);
CREATE INDEX usr_pwd ON USR (userId, password);
CREATE INDEX name ON USR (name);
CREATE INDEX msg ON MESSAGE (msgId, senderId, receiverId, contents, deleteStatus);
CREATE INDEX connection ON CONNECTION_USR (userId, status);