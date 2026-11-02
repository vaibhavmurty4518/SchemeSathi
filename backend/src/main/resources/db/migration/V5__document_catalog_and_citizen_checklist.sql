CREATE TABLE document_catalog (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  code VARCHAR(60) NOT NULL UNIQUE,
  title VARCHAR(180) NOT NULL,
  description TEXT NOT NULL,
  category VARCHAR(60) NOT NULL,
  issuing_authority VARCHAR(180),
  official_url VARCHAR(1000),
  active BOOLEAN NOT NULL DEFAULT true,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE citizen_document_checklist (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
  document_id UUID NOT NULL REFERENCES document_catalog(id),
  status VARCHAR(20) NOT NULL DEFAULT 'NEEDED' CHECK (status IN ('NEEDED','READY','EXPIRED','NOT_APPLICABLE')),
  notes VARCHAR(500),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  UNIQUE(user_id, document_id)
);
CREATE INDEX idx_citizen_document_user ON citizen_document_checklist(user_id, status);

INSERT INTO document_catalog(code,title,description,category,issuing_authority) VALUES
('AADHAAR','Aadhaar Card','Identity and address proof; share only where legally required.','IDENTITY','UIDAI'),
('PAN','PAN Card','Permanent Account Number proof, often used for financial schemes.','IDENTITY','Income Tax Department'),
('VOTER_ID','Voter ID (EPIC)','Electoral identity proof.','IDENTITY','Election Commission of India'),
('PASSPORT','Passport','Government-issued identity and citizenship-related proof.','IDENTITY','Passport Seva'),
('DRIVING_LICENSE','Driving Licence','Photo identity proof where accepted.','IDENTITY','State Transport Department'),
('DOMICILE','Domicile / Residence Certificate','Proof of residence in a state or region.','RESIDENCE','State/UT authority'),
('INCOME_CERT','Income Certificate','Official proof of family or individual income for eligibility.','INCOME','State/UT revenue authority'),
('CASTE_CERT','Caste Certificate','Proof of caste/category when a scheme requires it.','SOCIAL_CATEGORY','Competent state authority'),
('EWS_CERT','EWS Certificate','Economically Weaker Section certificate, if applicable.','SOCIAL_CATEGORY','Competent authority'),
('BIRTH_CERT','Birth Certificate','Proof of date and place of birth.','PERSONAL','Local registrar'),
('MARRIAGE_CERT','Marriage Certificate','Proof of marital status for applicable schemes.','PERSONAL','Marriage registrar'),
('RATION_CARD','Ration Card','Family and household information, where accepted.','FAMILY','State food/civil supplies department'),
('BANK_PASSBOOK','Bank Passbook / Account Proof','Account details for direct benefit transfer; mask unnecessary data.','FINANCIAL','Bank'),
('EDUCATION_MARKSHEET','Education Marksheet / Certificate','Proof of education level or academic result.','EDUCATION','Recognized board/university'),
('EMPLOYMENT_PROOF','Employment / Job-Seeker Proof','Employment status proof such as registration or certificate.','EMPLOYMENT','Relevant department/employer'),
('DISABILITY_CERT','Disability Certificate / UDID','Disability proof where scheme eligibility requires it.','SPECIAL_CATEGORY','UDID / competent medical authority'),
('LAND_RECORD','Land Record / Khasra-Khatauni','Agricultural land ownership or tenancy records, where required.','AGRICULTURE','State land records department'),
('FARMER_ID','Farmer Registration / ID','Farmer identity/registration proof where applicable.','AGRICULTURE','State agriculture department'),
('PHOTO','Recent Passport-size Photograph','Recent photo as specified by the application.','GENERAL','Applicant'),
('SIGNATURE','Signature / Thumb Impression','Signature or thumb impression as specified by the form.','GENERAL','Applicant'),
('SELF_DECLARATION','Self-declaration / Affidavit','Declaration in the format prescribed by the scheme.','GENERAL','Applicant / notary as prescribed');
