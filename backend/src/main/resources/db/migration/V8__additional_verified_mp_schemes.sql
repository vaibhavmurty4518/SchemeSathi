-- V8: Additional verified Madhya Pradesh state government schemes
-- Sources: Official Government of Madhya Pradesh portals (saara.mp.gov.in, scholarshipportal.mp.nic.in, sambal.mp.gov.in, msme.mponline.gov.in, mpurban.gov.in)
-- Verified against official published guidelines and criteria.

INSERT INTO government_schemes (title, summary, scope, state_code, category, benefit_description,
  eligibility_rules, required_documents, application_url, source_url, source_name,
  verified_at, review_status, published) VALUES

-- 1. Mukhya Mantri Kisan Kalyan Yojana (MP)
('Mukhya Mantri Kisan Kalyan Yojana',
 'State-level supplementary income support scheme by Government of Madhya Pradesh providing Rs 6,000 annually in addition to Central PM-Kisan (total Rs 12,000 per year for MP farmers).',
 'STATE', 'MP', 'Agriculture',
 'Additional financial assistance of Rs 6,000 per year credited directly to Aadhaar-linked bank accounts of verified MP farmer families.',
 '{"all":[{"field":"stateCode","operator":"EQ","value":"MP","label":"State of residence","description":"Must be a bona fide resident of Madhya Pradesh"},{"field":"employmentStatus","operator":"IN","value":["FARMER","SELF_EMPLOYED_AGRICULTURE"],"label":"Farmer status","description":"Must be registered as a farmer with landholding in MP"},{"field":"age","operator":"GTE","value":18,"label":"Minimum age","description":"Applicant must be at least 18 years old"}],"any":[]}',
 '["AADHAAR","BANK_PASSBOOK","LAND_RECORD","DOMICILE","FARMER_ID"]',
 'https://saara.mp.gov.in/',
 'https://saara.mp.gov.in/',
 'MP SAARA Revenue Portal (Revenue Department, GoMP)',
 now(), 'APPROVED', true),

-- 2. Mukhya Mantri Medhavi Vidyarthi Yojana (MMVY) (MP)
('Mukhya Mantri Medhavi Vidyarthi Yojana',
 'Financial support scheme for meritorious students of Madhya Pradesh who scored 70%+ in MP Board or 85%+ in CBSE/ICSE 12th exams to pursue higher technical and professional education.',
 'STATE', 'MP', 'Education',
 'Full course tuition fees paid directly to academic institutions (engineering, medical, polytechnic, law, government degree courses) by GoMP.',
 '{"all":[{"field":"stateCode","operator":"EQ","value":"MP","label":"State of residence","description":"Must be a resident of Madhya Pradesh"},{"field":"annualIncome","operator":"LTE","value":600000,"label":"Family annual income","description":"Father/Guardian annual income must not exceed Rs 6 lakh"},{"field":"educationLevel","operator":"IN","value":["HIGHER_SECONDARY","GRADUATE"],"label":"Education qualification","description":"Must have passed Class 12 with prescribed merit percentage"}],"any":[]}',
 '["AADHAAR","DOMICILE","INCOME_CERT","EDUCATION_MARKSHEET","BANK_PASSBOOK"]',
 'http://scholarshipportal.mp.nic.in/MedhaviChhatra/',
 'http://scholarshipportal.mp.nic.in/MedhaviChhatra/',
 'MP State Scholarship Portal 2.0 (GoMP)',
 now(), 'APPROVED', true),

-- 3. Mukhya Mantri Jan Kalyan (Sambal 2.0) Yojana (MP)
('Mukhya Mantri Jan Kalyan (Sambal 2.0) Yojana',
 'Comprehensive social security cover for unorganized sector workers in Madhya Pradesh providing maternity relief, accidental death benefits, funeral assistance, and educational support.',
 'STATE', 'MP', 'Social Security',
 'Assistance of Rs 4 lakh on accidental death, Rs 2 lakh on normal death, Rs 2 lakh on permanent disability, Rs 16,000 maternity assistance, and waiver of electricity bill arrears.',
 '{"all":[{"field":"stateCode","operator":"EQ","value":"MP","label":"State of residence","description":"Must be a resident of Madhya Pradesh"},{"field":"age","operator":"GTE","value":18,"label":"Minimum age","description":"Must be at least 18 years old"},{"field":"age","operator":"LTE","value":60,"label":"Maximum age","description":"Must be 60 years or younger"},{"field":"employmentStatus","operator":"IN","value":["INFORMAL_WORKER","SELF_EMPLOYED","UNEMPLOYED"],"label":"Unorganized worker","description":"Must be registered as an unorganized sector labourer in MP"}],"any":[]}',
 '["AADHAAR","DOMICILE","BANK_PASSBOOK","PHOTO"]',
 'https://sambal.mp.gov.in/',
 'https://sambal.mp.gov.in/',
 'Labour Department Portal (Sambal 2.0, GoMP)',
 now(), 'APPROVED', true),

-- 4. MP MSME Mukhyamantri Udyam Kranti Yojana (MP)
('Mukhyamantri Udyam Kranti Yojana',
 'Self-employment scheme providing collateral-free bank loans with interest subsidy and credit guarantee for youth of Madhya Pradesh to establish manufacturing, service, or retail businesses.',
 'STATE', 'MP', 'Skill & Employment',
 'Project loan from Rs 1 lakh up to Rs 50 lakh for manufacturing and up to Rs 25 lakh for services, with 3% annual interest subsidy for 7 years.',
 '{"all":[{"field":"stateCode","operator":"EQ","value":"MP","label":"State of residence","description":"Must be a resident of Madhya Pradesh"},{"field":"age","operator":"GTE","value":18,"label":"Minimum age","description":"Must be at least 18 years old"},{"field":"age","operator":"LTE","value":45,"label":"Maximum age","description":"Must be 45 years or younger"},{"field":"annualIncome","operator":"LTE","value":1200000,"label":"Family annual income","description":"Family income should not exceed Rs 12 lakh"}],"any":[]}',
 '["AADHAAR","DOMICILE","EDUCATION_MARKSHEET","BANK_PASSBOOK","PAN"]',
 'https://msme.mponline.gov.in/',
 'https://msme.mponline.gov.in/',
 'MP MSME Portal (Department of MSME, GoMP)',
 now(), 'APPROVED', true),

-- 5. Deendayal Antyodaya Rasoi Yojana (MP)
('Deendayal Antyodaya Rasoi Yojana',
 'Urban food security program providing hygienic, balanced, and nutritious meals to poor labourers, daily wage workers, and citizens in Madhya Pradesh cities for Rs 5 per thali.',
 'STATE', 'MP', 'Social Welfare',
 'Nutritious lunch meal thali at heavily subsidized rate of Rs 5 per person at municipal kitchen centres across all district headquarters of MP.',
 '{"all":[{"field":"stateCode","operator":"EQ","value":"MP","label":"State of residence","description":"Must be in Madhya Pradesh"},{"field":"residenceType","operator":"IN","value":["URBAN","SEMI_URBAN"],"label":"Urban area","description":"Operates in municipal and urban local body areas of MP"}],"any":[]}',
 '["AADHAAR"]',
 'https://mpurban.gov.in/',
 'https://mpurban.gov.in/',
 'Urban Development & Housing Department, GoMP',
 now(), 'APPROVED', true);
