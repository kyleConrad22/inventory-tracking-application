CREATE DATABASE inventory;
CREATE USER 'springuser'@'%' IDENTIFIED BY 'ThePassword';
GRANT ALL ON inventory.* TO 'springuser'@'%';

USE inventory;

CREATE TABLE current_inventory (
	heat_num VARCHAR(20) NOT NULL,
    gross_weight_kg VARCHAR(5) NOT NULL,
    net_weight_kg VARCHAR(5) NOT NULL,
    quantity VARCHAR(3) NOT NULL,
    dimension VARCHAR(20) NOT NULL,
    grade VARCHAR(30) NOT NULL,
    certificate_num VARCHAR(15) NOT NULL,
    bl_num VARCHAR(30) NOT NULL,
    barcode VARCHAR(30) NOT NULL,
	package_num VARCHAR(3) NOT NULL,
    PRIMARY KEY (barcode)
);

SHOW VARIABLES LIKE "secure_file_priv";

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/export.csv'
INTO TABLE current_inventory
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\r';

SELECT * FROM current_inventory;

DELETE FROM current_inventory WHERE heat_num = NULL;

UPDATE current_inventory
SET heat_num = REPLACE(heat_num, ' ', '');

UPDATE current_inventory
SET heat_num = REPLACE(heat_num, '"','');

UPDATE current_inventory
SET dimension = REPLACE(dimension, '?', 'X');

ALTER TABLE current_inventory
ADD work_order VARCHAR(11) NOT NULL,
ADD load_num VARCHAR(3) NOT NULL,
ADD loader VARCHAR(30) NOT NULL,
ADD load_time VARCHAR(20) NOT NULL;

ALTER TABLE current_inventory
	ADD barge VARCHAR(50) NOT NULL,
    ADD reception_date VARCHAR(20) NOT NULL,
    ADD checker VARCHAR(30) NOT NULL,
    ADD mark VARCHAR(30) NOT NULL;