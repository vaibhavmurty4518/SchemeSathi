CREATE TABLE citizen_scheme_applications (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
  scheme_id UUID NOT NULL REFERENCES government_schemes(id) ON DELETE RESTRICT,
  status VARCHAR(24) NOT NULL DEFAULT 'INTERESTED',
  notes VARCHAR(2000),
  applied_on DATE,
  last_updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT citizen_scheme_applications_status_check CHECK (status IN ('INTERESTED','PREPARING','SUBMITTED','UNDER_REVIEW','APPROVED','REJECTED','ON_HOLD')),
  CONSTRAINT citizen_scheme_applications_user_scheme_unique UNIQUE (user_id, scheme_id)
);
CREATE INDEX idx_citizen_applications_user_status ON citizen_scheme_applications(user_id, status, last_updated_at DESC);
