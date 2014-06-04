DROP SCHEMA IF EXISTS COMINFO;
CREATE SCHEMA COMINFO;
SET SCHEMA COMINFO;

CREATE TABLE DATAROW (
  ID            INT AUTO_INCREMENT PRIMARY KEY,
  BOOKING_DATE  DATE           NOT NULL,
  VALUE_DATE    DATE           NOT NULL,
  BOOKING_TEXT  VARCHAR(2000)  NOT NULL,
  BOOKING_VALUE DECIMAL(20, 2) NOT NULL,
  UNIQUE (BOOKING_DATE, VALUE_DATE, BOOKING_TEXT, BOOKING_VALUE)
);

CREATE TABLE CATEGORY (
  ID    INT AUTO_INCREMENT PRIMARY KEY,
  REGEX VARCHAR(1000) NOT NULL
);

CREATE TABLE CATEGORY_MAP (
  ROW_ID INT,
  FOREIGN KEY (ROW_ID) REFERENCES DATAROW (ID) ON DELETE CASCADE,
  CAT_ID INT,
  FOREIGN KEY (CAT_ID) REFERENCES CATEGORY (ID) ON DELETE CASCADE
);
