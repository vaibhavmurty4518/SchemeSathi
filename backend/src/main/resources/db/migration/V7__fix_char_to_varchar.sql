-- V7: Fix CHAR(2) columns to VARCHAR(2) for Hibernate compatibility
ALTER TABLE citizen_profiles ALTER COLUMN state_code TYPE VARCHAR(2);
ALTER TABLE government_schemes ALTER COLUMN state_code TYPE VARCHAR(2);
