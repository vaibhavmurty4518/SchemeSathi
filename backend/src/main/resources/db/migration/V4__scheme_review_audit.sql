CREATE TABLE scheme_review_audit (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  scheme_id UUID NOT NULL REFERENCES government_schemes(id) ON DELETE CASCADE,
  reviewer_id VARCHAR(100) NOT NULL,
  decision VARCHAR(20) NOT NULL CHECK (decision IN ('APPROVED','REJECTED')),
  comment TEXT,
  decided_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_scheme_review_audit_scheme ON scheme_review_audit(scheme_id, decided_at DESC);
