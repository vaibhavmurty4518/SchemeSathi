CREATE TABLE app_users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email VARCHAR(320) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(80) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'CITIZEN',
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT app_users_role_check CHECK (role IN ('CITIZEN','REVIEWER','ADMIN'))
);

CREATE TABLE citizen_profiles (
  user_id UUID PRIMARY KEY REFERENCES app_users(id) ON DELETE CASCADE,
  date_of_birth DATE,
  state_code CHAR(2),
  district VARCHAR(100),
  gender VARCHAR(30),
  marital_status VARCHAR(30),
  annual_income BIGINT CHECK (annual_income IS NULL OR annual_income >= 0),
  employment_status VARCHAR(40),
  education_level VARCHAR(60),
  social_category VARCHAR(30),
  residence_type VARCHAR(20),
  disability BOOLEAN,
  interests JSONB NOT NULL DEFAULT '[]'::jsonb,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE government_schemes (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title VARCHAR(200) NOT NULL,
  summary TEXT NOT NULL,
  scope VARCHAR(20) NOT NULL CHECK (scope IN ('CENTRAL','STATE')),
  state_code CHAR(2),
  category VARCHAR(80) NOT NULL,
  benefit_description TEXT,
  eligibility_rules JSONB NOT NULL DEFAULT '{"all":[],"any":[]}'::jsonb,
  required_documents JSONB NOT NULL DEFAULT '[]'::jsonb,
  application_url VARCHAR(1000),
  source_url VARCHAR(1000) NOT NULL,
  source_name VARCHAR(200) NOT NULL,
  verified_at TIMESTAMPTZ,
  review_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (review_status IN ('DRAFT','IN_REVIEW','APPROVED','REJECTED','EXPIRED')),
  published BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CHECK ((scope = 'CENTRAL' AND state_code IS NULL) OR (scope = 'STATE' AND state_code IS NOT NULL))
);
CREATE INDEX idx_scheme_public_scope_state ON government_schemes(published,review_status,scope,state_code);
CREATE INDEX idx_scheme_category ON government_schemes(category);
