CREATE TABLE measures (
  id INTEGER PRIMARY KEY AUTO_INCREMENT,

  value        INT NOT NULL,
  description  VARCHAR(255) NOT NULL,
  j_desc       VARCHAR(255));

-- no insertion?
-- when (migrate), following error happened.
-- ; Execution error (SQLSyntaxErrorException) at com.mysql.cj.jdbc.exceptions.SQLError/createSQLException (SQLError.java:120).
-- ; You have an error in your SQL syntax; check the manual that corresponds to your MariaDB server version for the right syntax to use near 'INSERT INTO measures
-- ; (value, description)
-- ; VALUES
-- ; (1, 'Weight (kg)'),
-- ; (4, 'Hei...' at line 9

INSERT INTO measures
(value, description)
VALUES
(1, 'Weight (kg)'),
(4, 'Height (meter)'),
(5, 'Fat Free Mass (kg)'),
(6, 'Fat Ratio (%)'),
(8, 'Fat Mass Weight (kg)'),
(9, 'Diastolic Blood Pressure (mmHg)'),
(10, 'Systolic Blood Pressure (mmHg)'),
(11, 'Heart Pulse (bpm) - only for BPM and scale devices'),
(12, 'Temperature (celsius)'),
(54, 'SP02 (%)'),
(71, 'Body Temperature (celsius)'),
(73, 'Skin Temperature (celsius)'),
(76, 'Muscle Mass (kg)'),
(77, 'Hydration (kg)'),
(88, 'Bone Mass (kg)'),
(91, 'Pulse Wave Velocity (m/s)'),
(123, 'VO2 max is a numerical measurement of your bodys ability to consume oxygen (ml/min/kg).'),
(135, 'QRS interval duration based on ECG signal'),
(136, 'PR interval duration based on ECG signal'),
(137, 'QT interval duration based on ECG signal'),
(138, 'Corrected QT interval duration based on ECG signal'),
(139, 'Atrial fibrillation result from PPG');

UPDATE measures set j_desc='体重 (kg)' where value=1;