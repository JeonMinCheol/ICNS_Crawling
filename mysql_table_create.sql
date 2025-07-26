create database justice_ben;
use justice_ben;

-- delete from justice_ben.user;
-- delete from justice_ben.case_decision;
-- delete from justice_ben.case_info;
-- delete from justice_ben.lawyer_info;
-- delete from justice_ben.defendant_lawyer;
-- delete from justice_ben.plaintiff_lawyer;

-- DROP TABLE user;
-- DROP TABLE defendant_lawyer;
-- DROP TABLE plaintiff_lawyer;
-- DROP TABLE case_decision;
-- DROP TABLE case_info;
-- DROP TABLE lawyer_info;

-- select * from case_info;

CREATE TABLE case_decision (
_id int DEFAULT NULL,
CASE_ID int DEFAULT NULL,
SENTENCE text,
PARAGRAPH text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE case_info (
CASE_ID int NOT NULL,
CASE_NAME varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
CASE_KIND varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
INCIDENT_REASON text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
COURT_NAME varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
INDEX_NO varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
SLIPOP_NO varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
JUDGE_NAME varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
PLAINTIFF_NAME varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
DEFENDANT_NAME varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
DECISION_DATE date NOT NULL,
PLAINTIFF_LAWYER_NUM int NOT NULL,
DEFENDANT_LAWYER_NUM int NOT NULL,
SUMMARY text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
RESULT varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
PRIMARY KEY (CASE_ID),
KEY idx_decision_date (DECISION_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `lawyer_info` (
  `_id` int NOT NULL,
  `LAWYER_NAME` varchar(256) NOT NULL,
  `LAWFIRM` varchar(1024) DEFAULT NULL,
  `COUNT` int DEFAULT NULL,
  `WIN` int DEFAULT NULL,
  `LOSE` int DEFAULT NULL,
  PRIMARY KEY (`_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `defendant_lawyer` (
  `_id` int NOT NULL,
  `CASE_ID` int NOT NULL,
  `LAWYER_NO` int NOT NULL,
  `LAWYER_NAME` varchar(1024) NOT NULL,
  `CLIENT_NAME` varchar(1024) NOT NULL,
  `RESULT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `CASE_ID` (`CASE_ID`),
  KEY `LAWYER_NO` (`LAWYER_NO`),
  CONSTRAINT `defendant_lawyer_ibfk_1` FOREIGN KEY (`CASE_ID`) REFERENCES `case_info` (`CASE_ID`) ON UPDATE CASCADE,
  CONSTRAINT `defendant_lawyer_ibfk_2` FOREIGN KEY (`LAWYER_NO`) REFERENCES `lawyer_info` (`_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `plaintiff_lawyer` (
  `_id` int NOT NULL,
  `CASE_ID` int NOT NULL,
  `LAWYER_NO` int NOT NULL,
  `LAWYER_NAME` varchar(1024) NOT NULL,
  `CLIENT_NAME` varchar(1024) NOT NULL,
  `RESULT` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`_id`),
  KEY `CASE_ID` (`CASE_ID`),
  KEY `LAWYER_NO` (`LAWYER_NO`),
  CONSTRAINT `plaintiff_lawyer_ibfk_1` FOREIGN KEY (`CASE_ID`) REFERENCES `case_info` (`CASE_ID`) ON UPDATE CASCADE,
  CONSTRAINT `plaintiff_lawyer_ibfk_2` FOREIGN KEY (`LAWYER_NO`) REFERENCES `lawyer_info` (`_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `role` varchar(256) NOT NULL,
  PRIMARY KEY (`_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 데이터 임포트 완료 후 실행할 것
-- ALTER TABLE defendant_lawyer DROP FOREIGN KEY defendant_lawyer_ibfk_2;
-- ALTER TABLE plaintiff_lawyer DROP FOREIGN KEY plaintiff_lawyer_ibfk_2;
-- ALTER TABLE lawyer_info MODIFY COLUMN _id INT NOT NULL AUTO_INCREMENT;
-- ALTER TABLE defendant_lawyer
-- ADD CONSTRAINT defendant_lawyer_ibfk_2
-- FOREIGN KEY (LAWYER_NO) REFERENCES lawyer_info(_id) ON UPDATE CASCADE;
-- ALTER TABLE plaintiff_lawyer
-- ADD CONSTRAINT plaintiff_lawyer_ibfk_2
-- FOREIGN KEY (LAWYER_NO) REFERENCES lawyer_info(_id) ON UPDATE CASCADE;
-- ALTER TABLE defendant_lawyer MODIFY COLUMN _id INT NOT NULL AUTO_INCREMENT;
-- ALTER TABLE plaintiff_lawyer MODIFY COLUMN _id INT NOT NULL AUTO_INCREMENT;
-- ALTER TABLE case_decision MODIFY COLUMN _id INT NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY (_id);
