-- V6: Seed verified government schemes (Central + Madhya Pradesh)
-- Sources: Official .gov.in / .nic.in portals. Verified November 2026.
-- Eligibility rules are simplified representations of published criteria.

INSERT INTO government_schemes (title, summary, scope, state_code, category, benefit_description,
  eligibility_rules, required_documents, application_url, source_url, source_name,
  verified_at, review_status, published) VALUES

-- 1. PM Kisan Samman Nidhi
('PM Kisan Samman Nidhi',
 'Direct income support of Rs 6,000 per year to small and marginal farmer families, paid in three equal instalments of Rs 2,000 each.',
 'CENTRAL', NULL, 'Agriculture',
 'Rs 6,000 per year transferred directly to bank account in 3 instalments of Rs 2,000.',
 '{"all":[{"field":"age","operator":"GTE","value":18,"label":"Minimum age","description":"Applicant must be at least 18 years old"},{"field":"employmentStatus","operator":"IN","value":["FARMER","SELF_EMPLOYED_AGRICULTURE"],"label":"Farmer status","description":"Must be a small or marginal farmer family"}],"any":[]}',
 '["AADHAAR","BANK_PASSBOOK","LAND_RECORD","FARMER_ID"]',
 'https://pmkisan.gov.in/',
 'https://pmkisan.gov.in/',
 'PM Kisan Official Portal',
 now(), 'APPROVED', true),

-- 2. Ayushman Bharat PM-JAY
('Ayushman Bharat - Pradhan Mantri Jan Arogya Yojana (PM-JAY)',
 'Health insurance cover of Rs 5 lakh per family per year for secondary and tertiary hospitalisation to economically vulnerable families.',
 'CENTRAL', NULL, 'Health',
 'Cashless and paperless hospital treatment up to Rs 5 lakh per family per year at empanelled hospitals.',
 '{"all":[{"field":"annualIncome","operator":"LTE","value":500000,"label":"Family annual income","description":"Family annual income must be up to Rs 5 lakh"}],"any":[]}',
 '["AADHAAR","RATION_CARD","INCOME_CERT"]',
 'https://pmjay.gov.in/',
 'https://pmjay.gov.in/',
 'PM-JAY Official Portal',
 now(), 'APPROVED', true),

-- 3. PM Awas Yojana - Gramin
('Pradhan Mantri Awas Yojana - Gramin (PMAY-G)',
 'Financial assistance for construction of pucca houses with basic amenities to eligible rural households who are houseless or living in kutcha/dilapidated houses.',
 'CENTRAL', NULL, 'Housing',
 'Rs 1.20 lakh in plain areas and Rs 1.30 lakh in hilly/difficult areas for house construction.',
 '{"all":[{"field":"annualIncome","operator":"LTE","value":300000,"label":"Household income limit","description":"Household annual income should be within BPL or eligible category limits"},{"field":"residenceType","operator":"EQ","value":"RURAL","label":"Rural residence","description":"Must reside in a rural area"}],"any":[]}',
 '["AADHAAR","INCOME_CERT","BANK_PASSBOOK","RATION_CARD"]',
 'https://pmayg.nic.in/',
 'https://pmayg.nic.in/',
 'PMAY-G Official Portal',
 now(), 'APPROVED', true),

-- 4. Ladli Behna Yojana (MP)
('Mukhya Mantri Ladli Behna Yojana',
 'Monthly financial assistance of Rs 1,250 to eligible women of Madhya Pradesh for their empowerment and financial independence.',
 'STATE', 'MP', 'Women & Child Development',
 'Rs 1,250 per month (Rs 15,000 per year) transferred directly to bank account.',
 '{"all":[{"field":"gender","operator":"EQ","value":"FEMALE","label":"Gender","description":"Applicant must be female"},{"field":"stateCode","operator":"EQ","value":"MP","label":"State of residence","description":"Must be a resident of Madhya Pradesh"},{"field":"age","operator":"GTE","value":21,"label":"Minimum age","description":"Must be at least 21 years old"},{"field":"age","operator":"LTE","value":60,"label":"Maximum age","description":"Must be 60 years or younger"},{"field":"annualIncome","operator":"LTE","value":250000,"label":"Family income","description":"Family annual income should not exceed Rs 2.5 lakh"}],"any":[]}',
 '["AADHAAR","BANK_PASSBOOK","DOMICILE","INCOME_CERT"]',
 'https://ladlibahna.mp.gov.in/',
 'https://ladlibahna.mp.gov.in/',
 'Ladli Behna Official Portal (MP)',
 now(), 'APPROVED', true),

-- 5. Mukhya Mantri Kanya Vivah Yojana (MP)
('Mukhya Mantri Kanya Vivah / Nikah Yojana',
 'Financial assistance of Rs 55,000 for marriage of daughters from economically weaker families in Madhya Pradesh.',
 'STATE', 'MP', 'Women & Child Development',
 'Rs 55,000 financial assistance including Rs 11,000 for household items, Rs 38,000 fixed deposit, and Rs 6,000 for event expenses.',
 '{"all":[{"field":"stateCode","operator":"EQ","value":"MP","label":"State","description":"Must be a resident of Madhya Pradesh"},{"field":"annualIncome","operator":"LTE","value":200000,"label":"Family income","description":"Family annual income should not exceed Rs 2 lakh"},{"field":"age","operator":"GTE","value":18,"label":"Bride minimum age","description":"Bride must be at least 18 years old"}],"any":[{"field":"socialCategory","operator":"IN","value":["SC","ST","OBC","GENERAL_BPL"],"label":"Social category","description":"Family should belong to BPL, SC, ST or OBC category"}]}',
 '["AADHAAR","DOMICILE","INCOME_CERT","CASTE_CERT","BIRTH_CERT","BANK_PASSBOOK"]',
 'https://mpedistrict.gov.in/',
 'https://mpedistrict.gov.in/',
 'MP e-District Portal',
 now(), 'APPROVED', true),

-- 6. PM Ujjwala Yojana
('Pradhan Mantri Ujjwala Yojana (PMUY)',
 'Free LPG connections to women from Below Poverty Line (BPL) households to reduce health hazards from cooking with fossil fuels.',
 'CENTRAL', NULL, 'Energy',
 'Free LPG connection with a deposit-free cylinder and regulator. First refill and stove provided free under PMUY 2.0.',
 '{"all":[{"field":"age","operator":"GTE","value":18,"label":"Minimum age","description":"Applicant must be at least 18 years old"},{"field":"gender","operator":"EQ","value":"FEMALE","label":"Gender","description":"LPG connection is issued in the name of an adult woman of the household"},{"field":"annualIncome","operator":"LTE","value":200000,"label":"BPL household","description":"Must belong to a Below Poverty Line household"}],"any":[]}',
 '["AADHAAR","RATION_CARD","BANK_PASSBOOK","PHOTO"]',
 'https://pmuy.gov.in/',
 'https://pmuy.gov.in/',
 'PMUY Official Portal',
 now(), 'APPROVED', true),

-- 7. Sukanya Samriddhi Yojana
('Sukanya Samriddhi Yojana',
 'Small savings scheme for the girl child offering attractive interest rates and tax benefits under Section 80C, with a maturity period of 21 years.',
 'CENTRAL', NULL, 'Financial Inclusion',
 'High interest rate savings account (currently ~8% p.a.) with tax benefits. Minimum deposit Rs 250/year, maximum Rs 1.5 lakh/year.',
 '{"all":[{"field":"gender","operator":"EQ","value":"FEMALE","label":"Girl child","description":"Account is opened for a girl child below 10 years of age"},{"field":"age","operator":"LTE","value":10,"label":"Maximum age","description":"Girl child must be below 10 years of age at account opening"}],"any":[]}',
 '["BIRTH_CERT","AADHAAR","PHOTO","PAN"]',
 'https://nsiindia.gov.in/',
 'https://nsiindia.gov.in/',
 'National Savings Institute',
 now(), 'APPROVED', true),

-- 8. PM Vishwakarma Yojana
('PM Vishwakarma Yojana',
 'End-to-end support to artisans and craftspeople through recognition, skill upgradation, toolkit incentives, credit support and market linkage.',
 'CENTRAL', NULL, 'Skill Development',
 'PM Vishwakarma Certificate and ID card; up to Rs 15,000 toolkit incentive; collateral-free credit up to Rs 3 lakh; skill training with stipend of Rs 500/day.',
 '{"all":[{"field":"age","operator":"GTE","value":18,"label":"Minimum age","description":"Must be at least 18 years old"},{"field":"employmentStatus","operator":"IN","value":["SELF_EMPLOYED","SELF_EMPLOYED_ARTISAN","INFORMAL_WORKER"],"label":"Artisan/craftsperson","description":"Must be working with hands and tools in one of the 18 identified trades"}],"any":[]}',
 '["AADHAAR","BANK_PASSBOOK","PHOTO","SELF_DECLARATION"]',
 'https://pmvishwakarma.gov.in/',
 'https://pmvishwakarma.gov.in/',
 'PM Vishwakarma Official Portal',
 now(), 'APPROVED', true),

-- 9. NSAP - Indira Gandhi National Old Age Pension Scheme
('Indira Gandhi National Old Age Pension Scheme (IGNOAPS)',
 'Monthly pension to BPL citizens above 60 years of age under the National Social Assistance Programme.',
 'CENTRAL', NULL, 'Social Security',
 'Rs 200 per month for persons aged 60-79 years, Rs 500 per month for persons aged 80+ years. States may add top-up amounts.',
 '{"all":[{"field":"age","operator":"GTE","value":60,"label":"Minimum age","description":"Must be 60 years or older"},{"field":"annualIncome","operator":"LTE","value":200000,"label":"BPL status","description":"Must belong to a household living below the poverty line"}],"any":[]}',
 '["AADHAAR","BIRTH_CERT","INCOME_CERT","BANK_PASSBOOK","PHOTO"]',
 'https://nsap.nic.in/',
 'https://nsap.nic.in/',
 'NSAP Official Portal',
 now(), 'APPROVED', true),

-- 10. Mukhya Mantri Seekho Kamao Yojana (MP)
('Mukhya Mantri Seekho Kamao Yojana',
 'Skill development programme for youth in Madhya Pradesh providing on-the-job training at registered establishments with monthly stipend.',
 'STATE', 'MP', 'Skill Development',
 'Monthly stipend during training: Rs 8,000 (for 12th pass), Rs 8,500 (for ITI), Rs 9,000 (for diploma), Rs 10,000 (for graduate and above).',
 '{"all":[{"field":"stateCode","operator":"EQ","value":"MP","label":"State","description":"Must be a resident of Madhya Pradesh"},{"field":"age","operator":"GTE","value":18,"label":"Minimum age","description":"Must be at least 18 years old"},{"field":"age","operator":"LTE","value":29,"label":"Maximum age","description":"Must be 29 years or younger"}],"any":[]}',
 '["AADHAAR","DOMICILE","EDUCATION_MARKSHEET","BANK_PASSBOOK","PHOTO"]',
 'https://mmsky.mp.gov.in/',
 'https://mmsky.mp.gov.in/',
 'MMSKY Official Portal (MP)',
 now(), 'APPROVED', true);
