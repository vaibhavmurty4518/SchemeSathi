const crypto = require('crypto');
const url = require('url');

const JWT_SECRET = process.env.JWT_SECRET || 'schemesathi-secret-key-32chars-min-len!!';

// In-memory persistent data store (persists across warm serverless invocations)
const users = new Map();
const profiles = new Map();
const userChecklists = new Map(); // userId -> Map(docId -> item)
const userApplications = new Map(); // userId -> Map(appId -> item)
const reviewAudits = [];

// Seed default user
function hashPassword(password) {
  const salt = 'ss_salt_2026';
  return crypto.pbkdf2Sync(password, salt, 1000, 64, 'sha512').toString('hex');
}

const defaultUserId = 'c9b87940-abc1-4a6b-8a96-85a93c1cb0e5';
users.set('vaibhavmurty039@gmail.com', {
  id: defaultUserId,
  email: 'vaibhavmurty039@gmail.com',
  fullName: 'Vaibhav Murty',
  passwordHash: hashPassword('testpassword123'),
  role: 'CITIZEN',
  createdAt: new Date().toISOString()
});

// Admin demo user
const adminUserId = 'a1b2c3d4-e5f6-4a5b-8c7d-9e0f1a2b3c4d';
users.set('admin@schemesathi.in', {
  id: adminUserId,
  email: 'admin@schemesathi.in',
  fullName: 'SchemeSathi Admin',
  passwordHash: hashPassword('adminpassword123'),
  role: 'ADMIN',
  createdAt: new Date().toISOString()
});

// JWT Helpers (Pure Node crypto HMAC-SHA256)
function base64url(buf) {
  return buf.toString('base64').replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
}

function signJwt(payload) {
  const header = { alg: 'HS256', typ: 'JWT' };
  const exp = Math.floor(Date.now() / 1000) + (120 * 60);
  const fullPayload = { ...payload, exp, iat: Math.floor(Date.now() / 1000) };
  const encHeader = base64url(Buffer.from(JSON.stringify(header)));
  const encPayload = base64url(Buffer.from(JSON.stringify(fullPayload)));
  const hmac = crypto.createHmac('sha256', JWT_SECRET);
  hmac.update(encHeader + '.' + encPayload);
  const signature = base64url(hmac.digest());
  return `${encHeader}.${encPayload}.${signature}`;
}

function verifyJwt(token) {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    const hmac = crypto.createHmac('sha256', JWT_SECRET);
    hmac.update(parts[0] + '.' + parts[1]);
    const sig = base64url(hmac.digest());
    if (sig !== parts[2]) return null;
    const payload = JSON.parse(Buffer.from(parts[1], 'base64').toString('utf8'));
    if (payload.exp && payload.exp < Math.floor(Date.now() / 1000)) return null;
    return payload;
  } catch (e) {
    return null;
  }
}

// 21 Document Catalog Items
const documentCatalog = [
  { id: 'd001', code: 'AADHAAR', title: 'Aadhaar Card', description: 'Identity and address proof; share only where legally required.', category: 'IDENTITY', issuingAuthority: 'UIDAI', active: true },
  { id: 'd002', code: 'PAN', title: 'PAN Card', description: 'Permanent Account Number proof, often used for financial schemes.', category: 'IDENTITY', issuingAuthority: 'Income Tax Department', active: true },
  { id: 'd003', code: 'VOTER_ID', title: 'Voter ID (EPIC)', description: 'Electoral identity proof.', category: 'IDENTITY', issuingAuthority: 'Election Commission of India', active: true },
  { id: 'd004', code: 'PASSPORT', title: 'Passport', description: 'Government-issued identity and citizenship-related proof.', category: 'IDENTITY', issuingAuthority: 'Passport Seva', active: true },
  { id: 'd005', code: 'DRIVING_LICENSE', title: 'Driving Licence', description: 'Photo identity proof where accepted.', category: 'IDENTITY', issuingAuthority: 'State Transport Department', active: true },
  { id: 'd006', code: 'DOMICILE', title: 'Domicile / Residence Certificate', description: 'Proof of residence in a state or region.', category: 'RESIDENCE', issuingAuthority: 'State/UT authority', active: true },
  { id: 'd007', code: 'INCOME_CERT', title: 'Income Certificate', description: 'Official proof of family or individual income for eligibility.', category: 'INCOME', issuingAuthority: 'State/UT revenue authority', active: true },
  { id: 'd008', code: 'CASTE_CERT', title: 'Caste Certificate', description: 'Proof of caste/category when a scheme requires it.', category: 'SOCIAL_CATEGORY', issuingAuthority: 'Competent state authority', active: true },
  { id: 'd009', code: 'EWS_CERT', title: 'EWS Certificate', description: 'Economically Weaker Section certificate, if applicable.', category: 'SOCIAL_CATEGORY', issuingAuthority: 'Competent authority', active: true },
  { id: 'd010', code: 'BIRTH_CERT', title: 'Birth Certificate', description: 'Proof of date and place of birth.', category: 'PERSONAL', issuingAuthority: 'Local registrar', active: true },
  { id: 'd011', code: 'MARRIAGE_CERT', title: 'Marriage Certificate', description: 'Proof of marital status for applicable schemes.', category: 'PERSONAL', issuingAuthority: 'Marriage registrar', active: true },
  { id: 'd012', code: 'RATION_CARD', title: 'Ration Card', description: 'Family and household information, where accepted.', category: 'FAMILY', issuingAuthority: 'State food/civil supplies department', active: true },
  { id: 'd013', code: 'BANK_PASSBOOK', title: 'Bank Passbook / Account Proof', description: 'Account details for direct benefit transfer; mask unnecessary data.', category: 'FINANCIAL', issuingAuthority: 'Bank', active: true },
  { id: 'd014', code: 'EDUCATION_MARKSHEET', title: 'Education Marksheet / Certificate', description: 'Proof of education level or academic result.', category: 'EDUCATION', issuingAuthority: 'Recognized board/university', active: true },
  { id: 'd015', code: 'EMPLOYMENT_PROOF', title: 'Employment / Job-Seeker Proof', description: 'Employment status proof such as registration or certificate.', category: 'EMPLOYMENT', issuingAuthority: 'Relevant department/employer', active: true },
  { id: 'd016', code: 'DISABILITY_CERT', title: 'Disability Certificate / UDID', description: 'Disability proof where scheme eligibility requires it.', category: 'SPECIAL_CATEGORY', issuingAuthority: 'UDID / competent medical authority', active: true },
  { id: 'd017', code: 'LAND_RECORD', title: 'Land Record / Khasra-Khatauni', description: 'Agricultural land ownership or tenancy records, where required.', category: 'AGRICULTURE', issuingAuthority: 'State land records department', active: true },
  { id: 'd018', code: 'FARMER_ID', title: 'Farmer Registration / ID', description: 'Farmer identity/registration proof where applicable.', category: 'AGRICULTURE', issuingAuthority: 'State agriculture department', active: true },
  { id: 'd019', code: 'PHOTO', title: 'Recent Passport-size Photograph', description: 'Recent photo as specified by the application.', category: 'GENERAL', issuingAuthority: 'Applicant', active: true },
  { id: 'd020', code: 'SIGNATURE', title: 'Signature / Thumb Impression', description: 'Signature or thumb impression as specified by the form.', category: 'GENERAL', issuingAuthority: 'Applicant', active: true },
  { id: 'd021', code: 'SELF_DECLARATION', title: 'Self-declaration / Affidavit', description: 'Declaration in the format prescribed by the scheme.', category: 'GENERAL', issuingAuthority: 'Applicant / notary as prescribed', active: true }
];

// 15 Verified Real Government Schemes (7 Central + 8 Madhya Pradesh)
const schemes = [
  // 1. Central
  {
    id: 's001',
    title: 'PM Kisan Samman Nidhi',
    summary: 'Direct income support of Rs 6,000 per year to small and marginal farmer families, paid in three equal instalments of Rs 2,000 each.',
    scope: 'CENTRAL',
    stateCode: null,
    category: 'Agriculture',
    benefitDescription: 'Rs 6,000 per year transferred directly to bank account in 3 instalments of Rs 2,000.',
    eligibilityRules: {
      all: [
        { field: 'age', operator: 'GTE', value: 18, label: 'Minimum age', description: 'Applicant must be at least 18 years old' },
        { field: 'employmentStatus', operator: 'IN', value: ['FARMER', 'SELF_EMPLOYED_AGRICULTURE'], label: 'Farmer status', description: 'Must be a small or marginal farmer family' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'BANK_PASSBOOK', 'LAND_RECORD', 'FARMER_ID'],
    applicationUrl: 'https://pmkisan.gov.in/',
    sourceUrl: 'https://pmkisan.gov.in/',
    sourceName: 'PM Kisan Official Portal',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 2. Central
  {
    id: 's002',
    title: 'Ayushman Bharat - Pradhan Mantri Jan Arogya Yojana (PM-JAY)',
    summary: 'Health insurance cover of Rs 5 lakh per family per year for secondary and tertiary hospitalisation to economically vulnerable families.',
    scope: 'CENTRAL',
    stateCode: null,
    category: 'Health',
    benefitDescription: 'Cashless and paperless hospital treatment up to Rs 5 lakh per family per year at empanelled hospitals.',
    eligibilityRules: {
      all: [
        { field: 'annualIncome', operator: 'LTE', value: 500000, label: 'Family annual income', description: 'Family annual income must be up to Rs 5 lakh' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'RATION_CARD', 'INCOME_CERT'],
    applicationUrl: 'https://pmjay.gov.in/',
    sourceUrl: 'https://pmjay.gov.in/',
    sourceName: 'PM-JAY Official Portal',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 3. Central
  {
    id: 's003',
    title: 'Pradhan Mantri Awas Yojana - Gramin (PMAY-G)',
    summary: 'Financial assistance for construction of pucca houses with basic amenities to eligible rural households who are houseless or living in kutcha/dilapidated houses.',
    scope: 'CENTRAL',
    stateCode: null,
    category: 'Housing',
    benefitDescription: 'Rs 1.20 lakh in plain areas and Rs 1.30 lakh in hilly/difficult areas for house construction.',
    eligibilityRules: {
      all: [
        { field: 'annualIncome', operator: 'LTE', value: 300000, label: 'Household income limit', description: 'Household annual income should be within BPL or eligible category limits' },
        { field: 'residenceType', operator: 'EQ', value: 'RURAL', label: 'Rural residence', description: 'Must reside in a rural area' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'INCOME_CERT', 'BANK_PASSBOOK', 'RATION_CARD'],
    applicationUrl: 'https://pmayg.nic.in/',
    sourceUrl: 'https://pmayg.nic.in/',
    sourceName: 'PMAY-G Official Portal',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 4. Central
  {
    id: 's004',
    title: 'Pradhan Mantri Ujjwala Yojana (PMUY)',
    summary: 'Free LPG connections to women from Below Poverty Line (BPL) households to reduce health hazards from cooking with fossil fuels.',
    scope: 'CENTRAL',
    stateCode: null,
    category: 'Energy',
    benefitDescription: 'Free LPG connection with a deposit-free cylinder and regulator. First refill and stove provided free under PMUY 2.0.',
    eligibilityRules: {
      all: [
        { field: 'age', operator: 'GTE', value: 18, label: 'Minimum age', description: 'Applicant must be at least 18 years old' },
        { field: 'gender', operator: 'EQ', value: 'FEMALE', label: 'Gender', description: 'LPG connection is issued in the name of an adult woman of the household' },
        { field: 'annualIncome', operator: 'LTE', value: 200000, label: 'BPL household', description: 'Must belong to a Below Poverty Line household' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'RATION_CARD', 'BANK_PASSBOOK', 'PHOTO'],
    applicationUrl: 'https://pmuy.gov.in/',
    sourceUrl: 'https://pmuy.gov.in/',
    sourceName: 'PMUY Official Portal',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 5. Central
  {
    id: 's005',
    title: 'Sukanya Samriddhi Yojana',
    summary: 'Small savings scheme for the girl child offering attractive interest rates and tax benefits under Section 80C, with a maturity period of 21 years.',
    scope: 'CENTRAL',
    stateCode: null,
    category: 'Financial Inclusion',
    benefitDescription: 'High interest rate savings account (currently ~8% p.a.) with tax benefits. Minimum deposit Rs 250/year, maximum Rs 1.5 lakh/year.',
    eligibilityRules: {
      all: [
        { field: 'gender', operator: 'EQ', value: 'FEMALE', label: 'Girl child', description: 'Account is opened for a girl child below 10 years of age' },
        { field: 'age', operator: 'LTE', value: 10, label: 'Maximum age', description: 'Girl child must be below 10 years of age at account opening' }
      ],
      any: []
    },
    requiredDocuments: ['BIRTH_CERT', 'AADHAAR', 'PHOTO', 'PAN'],
    applicationUrl: 'https://nsiindia.gov.in/',
    sourceUrl: 'https://nsiindia.gov.in/',
    sourceName: 'National Savings Institute',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 6. Central
  {
    id: 's006',
    title: 'PM Vishwakarma Yojana',
    summary: 'End-to-end support to artisans and craftspeople through recognition, skill upgradation, toolkit incentives, credit support and market linkage.',
    scope: 'CENTRAL',
    stateCode: null,
    category: 'Skill Development',
    benefitDescription: 'PM Vishwakarma Certificate and ID card; up to Rs 15,000 toolkit incentive; collateral-free credit up to Rs 3 lakh; skill training with stipend of Rs 500/day.',
    eligibilityRules: {
      all: [
        { field: 'age', operator: 'GTE', value: 18, label: 'Minimum age', description: 'Must be at least 18 years old' },
        { field: 'employmentStatus', operator: 'IN', value: ['SELF_EMPLOYED', 'SELF_EMPLOYED_ARTISAN', 'INFORMAL_WORKER'], label: 'Artisan/craftsperson', description: 'Must be working with hands and tools in one of the 18 identified trades' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'BANK_PASSBOOK', 'PHOTO', 'SELF_DECLARATION'],
    applicationUrl: 'https://pmvishwakarma.gov.in/',
    sourceUrl: 'https://pmvishwakarma.gov.in/',
    sourceName: 'PM Vishwakarma Official Portal',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 7. Central
  {
    id: 's007',
    title: 'Indira Gandhi National Old Age Pension Scheme (IGNOAPS)',
    summary: 'Monthly pension to BPL citizens above 60 years of age under the National Social Assistance Programme.',
    scope: 'CENTRAL',
    stateCode: null,
    category: 'Social Security',
    benefitDescription: 'Rs 200 per month for persons aged 60-79 years, Rs 500 per month for persons aged 80+ years. States may add top-up amounts.',
    eligibilityRules: {
      all: [
        { field: 'age', operator: 'GTE', value: 60, label: 'Minimum age', description: 'Must be 60 years or older' },
        { field: 'annualIncome', operator: 'LTE', value: 200000, label: 'BPL status', description: 'Must belong to a household living below the poverty line' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'BIRTH_CERT', 'INCOME_CERT', 'BANK_PASSBOOK', 'PHOTO'],
    applicationUrl: 'https://nsap.nic.in/',
    sourceUrl: 'https://nsap.nic.in/',
    sourceName: 'NSAP Official Portal',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },

  // 8. Madhya Pradesh
  {
    id: 's008',
    title: 'Mukhya Mantri Ladli Behna Yojana',
    summary: 'Monthly financial assistance of Rs 1,250 to eligible women of Madhya Pradesh for their empowerment and financial independence.',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Women & Child Development',
    benefitDescription: 'Rs 1,250 per month (Rs 15,000 per year) transferred directly to bank account.',
    eligibilityRules: {
      all: [
        { field: 'gender', operator: 'EQ', value: 'FEMALE', label: 'Gender', description: 'Applicant must be female' },
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State of residence', description: 'Must be a resident of Madhya Pradesh' },
        { field: 'age', operator: 'GTE', value: 21, label: 'Minimum age', description: 'Must be at least 21 years old' },
        { field: 'age', operator: 'LTE', value: 60, label: 'Maximum age', description: 'Must be 60 years or younger' },
        { field: 'annualIncome', operator: 'LTE', value: 250000, label: 'Family income', description: 'Family annual income should not exceed Rs 2.5 lakh' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'BANK_PASSBOOK', 'DOMICILE', 'INCOME_CERT'],
    applicationUrl: 'https://ladlibahna.mp.gov.in/',
    sourceUrl: 'https://ladlibahna.mp.gov.in/',
    sourceName: 'Ladli Behna Official Portal (MP)',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 9. Madhya Pradesh
  {
    id: 's009',
    title: 'Mukhya Mantri Kanya Vivah / Nikah Yojana',
    summary: 'Financial assistance of Rs 55,000 for marriage of daughters from economically weaker families in Madhya Pradesh.',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Women & Child Development',
    benefitDescription: 'Rs 55,000 financial assistance including Rs 11,000 for household items, Rs 38,000 fixed deposit, and Rs 6,000 for event expenses.',
    eligibilityRules: {
      all: [
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State', description: 'Must be a resident of Madhya Pradesh' },
        { field: 'annualIncome', operator: 'LTE', value: 200000, label: 'Family income', description: 'Family annual income should not exceed Rs 2 lakh' },
        { field: 'age', operator: 'GTE', value: 18, label: 'Bride minimum age', description: 'Bride must be at least 18 years old' }
      ],
      any: [
        { field: 'socialCategory', operator: 'IN', value: ['SC', 'ST', 'OBC', 'GENERAL_BPL'], label: 'Social category', description: 'Family should belong to BPL, SC, ST or OBC category' }
      ]
    },
    requiredDocuments: ['AADHAAR', 'DOMICILE', 'INCOME_CERT', 'CASTE_CERT', 'BIRTH_CERT', 'BANK_PASSBOOK'],
    applicationUrl: 'https://mpedistrict.gov.in/',
    sourceUrl: 'https://mpedistrict.gov.in/',
    sourceName: 'MP e-District Portal',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 10. Madhya Pradesh
  {
    id: 's010',
    title: 'Mukhya Mantri Seekho Kamao Yojana',
    summary: 'Skill development programme for youth in Madhya Pradesh providing on-the-job training at registered establishments with monthly stipend.',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Skill Development',
    benefitDescription: 'Monthly stipend during training: Rs 8,000 (for 12th pass), Rs 8,500 (for ITI), Rs 9,000 (for diploma), Rs 10,000 (for graduate and above).',
    eligibilityRules: {
      all: [
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State', description: 'Must be a resident of Madhya Pradesh' },
        { field: 'age', operator: 'GTE', value: 18, label: 'Minimum age', description: 'Must be at least 18 years old' },
        { field: 'age', operator: 'LTE', value: 29, label: 'Maximum age', description: 'Must be 29 years or younger' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'DOMICILE', 'EDUCATION_MARKSHEET', 'BANK_PASSBOOK', 'PHOTO'],
    applicationUrl: 'https://mmsky.mp.gov.in/',
    sourceUrl: 'https://mmsky.mp.gov.in/',
    sourceName: 'MMSKY Official Portal (MP)',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 11. Madhya Pradesh
  {
    id: 's011',
    title: 'Mukhya Mantri Kisan Kalyan Yojana',
    summary: 'State-level supplementary income support scheme by Government of Madhya Pradesh providing Rs 6,000 annually in addition to Central PM-Kisan (total Rs 12,000 per year for MP farmers).',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Agriculture',
    benefitDescription: 'Additional financial assistance of Rs 6,000 per year credited directly to Aadhaar-linked bank accounts of verified MP farmer families.',
    eligibilityRules: {
      all: [
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State of residence', description: 'Must be a bona fide resident of Madhya Pradesh' },
        { field: 'employmentStatus', operator: 'IN', value: ['FARMER', 'SELF_EMPLOYED_AGRICULTURE'], label: 'Farmer status', description: 'Must be registered as a farmer with landholding in MP' },
        { field: 'age', operator: 'GTE', value: 18, label: 'Minimum age', description: 'Applicant must be at least 18 years old' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'BANK_PASSBOOK', 'LAND_RECORD', 'DOMICILE', 'FARMER_ID'],
    applicationUrl: 'https://saara.mp.gov.in/',
    sourceUrl: 'https://saara.mp.gov.in/',
    sourceName: 'MP SAARA Revenue Portal (Revenue Department, GoMP)',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 12. Madhya Pradesh
  {
    id: 's012',
    title: 'Mukhya Mantri Medhavi Vidyarthi Yojana',
    summary: 'Financial support scheme for meritorious students of Madhya Pradesh who scored 70%+ in MP Board or 85%+ in CBSE/ICSE 12th exams to pursue higher technical and professional education.',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Education',
    benefitDescription: 'Full course tuition fees paid directly to academic institutions (engineering, medical, polytechnic, law, government degree courses) by GoMP.',
    eligibilityRules: {
      all: [
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State of residence', description: 'Must be a resident of Madhya Pradesh' },
        { field: 'annualIncome', operator: 'LTE', value: 600000, label: 'Family annual income', description: 'Father/Guardian annual income must not exceed Rs 6 lakh' },
        { field: 'educationLevel', operator: 'IN', value: ['HIGHER_SECONDARY', 'GRADUATE'], label: 'Education qualification', description: 'Must have passed Class 12 with prescribed merit percentage' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'DOMICILE', 'INCOME_CERT', 'EDUCATION_MARKSHEET', 'BANK_PASSBOOK'],
    applicationUrl: 'http://scholarshipportal.mp.nic.in/MedhaviChhatra/',
    sourceUrl: 'http://scholarshipportal.mp.nic.in/MedhaviChhatra/',
    sourceName: 'MP State Scholarship Portal 2.0 (GoMP)',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 13. Madhya Pradesh
  {
    id: 's013',
    title: 'Mukhya Mantri Jan Kalyan (Sambal 2.0) Yojana',
    summary: 'Comprehensive social security cover for unorganized sector workers in Madhya Pradesh providing maternity relief, accidental death benefits, funeral assistance, and educational support.',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Social Security',
    benefitDescription: 'Assistance of Rs 4 lakh on accidental death, Rs 2 lakh on normal death, Rs 2 lakh on permanent disability, Rs 16,000 maternity assistance, and waiver of electricity bill arrears.',
    eligibilityRules: {
      all: [
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State of residence', description: 'Must be a resident of Madhya Pradesh' },
        { field: 'age', operator: 'GTE', value: 18, label: 'Minimum age', description: 'Must be at least 18 years old' },
        { field: 'age', operator: 'LTE', value: 60, label: 'Maximum age', description: 'Must be 60 years or younger' },
        { field: 'employmentStatus', operator: 'IN', value: ['INFORMAL_WORKER', 'SELF_EMPLOYED', 'UNEMPLOYED'], label: 'Unorganized worker', description: 'Must be registered as an unorganized sector labourer in MP' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'DOMICILE', 'BANK_PASSBOOK', 'PHOTO'],
    applicationUrl: 'https://sambal.mp.gov.in/',
    sourceUrl: 'https://sambal.mp.gov.in/',
    sourceName: 'Labour Department Portal (Sambal 2.0, GoMP)',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 14. Madhya Pradesh
  {
    id: 's014',
    title: 'Mukhyamantri Udyam Kranti Yojana',
    summary: 'Self-employment scheme providing collateral-free bank loans with interest subsidy and credit guarantee for youth of Madhya Pradesh to establish manufacturing, service, or retail businesses.',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Skill & Employment',
    benefitDescription: 'Project loan from Rs 1 lakh up to Rs 50 lakh for manufacturing and up to Rs 25 lakh for services, with 3% annual interest subsidy for 7 years.',
    eligibilityRules: {
      all: [
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State of residence', description: 'Must be a resident of Madhya Pradesh' },
        { field: 'age', operator: 'GTE', value: 18, label: 'Minimum age', description: 'Must be at least 18 years old' },
        { field: 'age', operator: 'LTE', value: 45, label: 'Maximum age', description: 'Must be 45 years or younger' },
        { field: 'annualIncome', operator: 'LTE', value: 1200000, label: 'Family annual income', description: 'Family income should not exceed Rs 12 lakh' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR', 'DOMICILE', 'EDUCATION_MARKSHEET', 'BANK_PASSBOOK', 'PAN'],
    applicationUrl: 'https://msme.mponline.gov.in/',
    sourceUrl: 'https://msme.mponline.gov.in/',
    sourceName: 'MP MSME Portal (Department of MSME, GoMP)',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  },
  // 15. Madhya Pradesh
  {
    id: 's015',
    title: 'Deendayal Antyodaya Rasoi Yojana',
    summary: 'Urban food security program providing hygienic, balanced, and nutritious meals to poor labourers, daily wage workers, and citizens in Madhya Pradesh cities for Rs 5 per thali.',
    scope: 'STATE',
    stateCode: 'MP',
    category: 'Social Welfare',
    benefitDescription: 'Nutritious lunch meal thali at heavily subsidized rate of Rs 5 per person at municipal kitchen centres across all district headquarters of MP.',
    eligibilityRules: {
      all: [
        { field: 'stateCode', operator: 'EQ', value: 'MP', label: 'State of residence', description: 'Must be in Madhya Pradesh' },
        { field: 'residenceType', operator: 'IN', value: ['URBAN', 'SEMI_URBAN'], label: 'Urban area', description: 'Operates in municipal and urban local body areas of MP' }
      ],
      any: []
    },
    requiredDocuments: ['AADHAAR'],
    applicationUrl: 'https://mpurban.gov.in/',
    sourceUrl: 'https://mpurban.gov.in/',
    sourceName: 'Urban Development & Housing Department, GoMP',
    verifiedAt: '2026-11-02T18:00:00Z',
    reviewStatus: 'APPROVED',
    published: true
  }
];

// In-Memory Helper for Request Body parsing
function readBody(req) {
  return new Promise((resolve, reject) => {
    let data = '';
    req.on('data', chunk => { data += chunk; });
    req.on('end', () => {
      try {
        resolve(data ? JSON.parse(data) : {});
      } catch (e) {
        reject(e);
      }
    });
    req.on('error', reject);
  });
}

function sendJson(res, status, obj) {
  res.writeHead(status, {
    'Content-Type': 'application/json',
    'Access-Control-Allow-Origin': '*',
    'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type, Authorization'
  });
  res.end(JSON.stringify(obj));
}

function authenticate(req) {
  const authHeader = req.headers['authorization'];
  if (!authHeader || !authHeader.startsWith('Bearer ')) return null;
  const token = authHeader.substring(7);
  return verifyJwt(token);
}

// Deterministic Eligibility Evaluator (Identical to Java EligibilityService.java)
function evaluateEligibility(profile, scheme) {
  const matched = [];
  const unmet = [];
  const missing = [];

  function getFieldValue(f) {
    if (!profile) return null;
    switch (f) {
      case 'age': {
        if (!profile.dateOfBirth) return null;
        const dob = new Date(profile.dateOfBirth);
        const diff = Date.now() - dob.getTime();
        return Math.floor(diff / (1000 * 60 * 60 * 24 * 365.25));
      }
      case 'annualIncome': return profile.annualIncome != null ? Number(profile.annualIncome) : null;
      case 'stateCode': return profile.stateCode || null;
      case 'gender': return profile.gender || null;
      case 'maritalStatus': return profile.maritalStatus || null;
      case 'employmentStatus': return profile.employmentStatus || null;
      case 'educationLevel': return profile.educationLevel || null;
      case 'socialCategory': return profile.socialCategory || null;
      case 'residenceType': return profile.residenceType || null;
      case 'disability': return profile.disability != null ? Boolean(profile.disability) : null;
      default: return null;
    }
  }

  function checkCondition(c, mList, uList, missList) {
    const field = c.field;
    const op = c.operator || 'EQ';
    const val = c.value;
    const actual = getFieldValue(field);
    const label = c.label || field;
    const desc = c.description || `${label} ${op} ${val}`;

    if (actual === null || actual === undefined) {
      missList.push(`Please provide: ${label}`);
      return false;
    }

    let pass = false;
    if (typeof actual === 'number' && typeof val === 'number') {
      switch (op) {
        case 'LTE': pass = actual <= val; break;
        case 'LT':  pass = actual < val; break;
        case 'GTE': pass = actual >= val; break;
        case 'GT':  pass = actual > val; break;
        case 'EQ':  pass = actual === val; break;
        case 'NEQ': pass = actual !== val; break;
        default: pass = false;
      }
    } else {
      const aStr = String(actual).toLowerCase();
      if (op === 'EQ') {
        pass = aStr === String(val).toLowerCase();
      } else if (op === 'NEQ') {
        pass = aStr !== String(val).toLowerCase();
      } else if (op === 'IN' && Array.isArray(val)) {
        pass = val.some(item => String(item).toLowerCase() === aStr);
      } else if (op === 'CONTAINS') {
        pass = aStr.includes(String(val).toLowerCase());
      }
    }

    if (pass) {
      mList.push(desc);
    } else {
      uList.push(desc);
    }
    return pass;
  }

  const rules = scheme.eligibilityRules || { all: [], any: [] };
  const allRules = rules.all || [];
  for (const c of allRules) {
    checkCondition(c, matched, unmet, missing);
  }

  const anyRules = rules.any || [];
  if (Array.isArray(anyRules) && anyRules.length > 0) {
    const aMatch = [];
    const aUnmet = [];
    const aMissing = [];
    let anyPass = false;

    for (const c of anyRules) {
      const m = [];
      const u = [];
      const mi = [];
      checkCondition(c, m, u, mi);
      if (u.length === 0 && mi.length === 0) anyPass = true;
      aMatch.push(...m);
      aUnmet.push(...u);
      aMissing.push(...mi);
    }

    if (anyPass) {
      matched.push('At least one alternative eligibility condition is satisfied');
    } else if (aMissing.length > 0) {
      missing.push(...aMissing);
    } else {
      unmet.push('None of the alternative eligibility conditions are satisfied');
    }
  }

  const status = unmet.length > 0 ? 'NOT_ELIGIBLE' : (missing.length > 0 ? 'NEEDS_INFORMATION' : 'ELIGIBLE');
  return {
    schemeId: scheme.id,
    schemeTitle: scheme.title,
    status,
    matched,
    unmet,
    missingInformation: missing,
    notice: "Preliminary assessment only. Final eligibility is decided by the scheme's implementing department; verify current official guidelines."
  };
}

// Main Serverless Handler
module.exports = async function handler(req, res) {
  if (req.method === 'OPTIONS') {
    res.writeHead(204, {
      'Access-Control-Allow-Origin': '*',
      'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type, Authorization'
    });
    return res.end();
  }

  const parsedUrl = new URL(req.url, `http://${req.headers.host || 'localhost'}`);
  let pathname = parsedUrl.pathname;
  // Normalize path
  if (!pathname.startsWith('/api') && !pathname.startsWith('/actuator')) {
    pathname = '/api' + (pathname.startsWith('/') ? '' : '/') + pathname;
  }

  const query = Object.fromEntries(parsedUrl.searchParams.entries());
  const method = req.method;

  try {
    // 1. Health check
    if (pathname === '/actuator/health' || pathname === '/api/actuator/health') {
      return sendJson(res, 200, { status: 'UP', groups: ['liveness', 'readiness'] });
    }

    // 2. Auth: Register
    if (pathname === '/api/v1/auth/register' && method === 'POST') {
      const body = await readBody(req);
      if (!body.email || !body.password || !body.fullName) {
        return sendJson(res, 400, { message: 'Full name, email, and password are required' });
      }
      const emailNorm = body.email.toLowerCase().trim();
      if (users.has(emailNorm)) {
        return sendJson(res, 400, { message: 'An account with this email already exists' });
      }
      const newUser = {
        id: crypto.randomUUID(),
        email: emailNorm,
        fullName: body.fullName.trim(),
        passwordHash: hashPassword(body.password),
        role: 'CITIZEN',
        createdAt: new Date().toISOString()
      };
      users.set(emailNorm, newUser);
      const token = signJwt({ sub: newUser.id, role: newUser.role });
      return sendJson(res, 200, {
        token,
        tokenType: 'Bearer',
        fullName: newUser.fullName,
        email: newUser.email,
        role: newUser.role
      });
    }

    // 3. Auth: Login
    if (pathname === '/api/v1/auth/login' && method === 'POST') {
      const body = await readBody(req);
      const emailNorm = (body.email || '').toLowerCase().trim();
      const user = users.get(emailNorm);
      if (!user || user.passwordHash !== hashPassword(body.password || '')) {
        return sendJson(res, 400, { message: 'Invalid email or password' });
      }
      const token = signJwt({ sub: user.id, role: user.role });
      return sendJson(res, 200, {
        token,
        tokenType: 'Bearer',
        fullName: user.fullName,
        email: user.email,
        role: user.role
      });
    }

    // 4. Schemes: List
    if (pathname === '/api/v1/schemes' && method === 'GET') {
      const stateParam = query.state ? query.state.trim().toUpperCase() : null;
      const catParam = query.category ? query.category.trim().toLowerCase() : null;

      const filtered = schemes.filter(s => {
        if (!s.published || s.reviewStatus !== 'APPROVED') return false;
        // State filter logic
        if (stateParam && stateParam !== 'ALL') {
          const isCentral = s.scope === 'CENTRAL';
          const isMatchingState = s.stateCode && s.stateCode.toUpperCase() === stateParam;
          if (!isCentral && !isMatchingState) return false;
        }
        // Category filter
        if (catParam) {
          const matchCat = s.category && s.category.toLowerCase().includes(catParam);
          const matchTitle = s.title && s.title.toLowerCase().includes(catParam);
          if (!matchCat && !matchTitle) return false;
        }
        return true;
      });

      // Sort: CENTRAL first, then Title
      filtered.sort((a, b) => {
        if (a.scope !== b.scope) return a.scope === 'CENTRAL' ? -1 : 1;
        return a.title.localeCompare(b.title);
      });

      return sendJson(res, 200, filtered);
    }

    // 5. Schemes: Detail
    if (pathname.startsWith('/api/v1/schemes/') && method === 'GET') {
      const id = pathname.replace('/api/v1/schemes/', '');
      const scheme = schemes.find(s => s.id === id);
      if (!scheme || !scheme.published || scheme.reviewStatus !== 'APPROVED') {
        return sendJson(res, 404, { message: 'Scheme not found or not published' });
      }
      return sendJson(res, 200, scheme);
    }

    // 6. Documents: Catalog
    if (pathname === '/api/v1/documents/catalog' && method === 'GET') {
      return sendJson(res, 200, documentCatalog.filter(d => d.active));
    }

    // Protected Routes Check
    const authUser = authenticate(req);
    if (!authUser) {
      return sendJson(res, 401, { message: 'Unauthorized: Valid token required' });
    }
    const currentUserId = authUser.sub;

    // 7. Profile: GET
    if (pathname === '/api/v1/profile' && method === 'GET') {
      const p = profiles.get(currentUserId);
      if (!p) {
        return sendJson(res, 404, { message: 'Citizen profile not found' });
      }
      return sendJson(res, 200, p);
    }

    // 8. Profile: PUT
    if (pathname === '/api/v1/profile' && method === 'PUT') {
      const body = await readBody(req);
      const updated = {
        userId: currentUserId,
        dateOfBirth: body.dateOfBirth || null,
        stateCode: body.stateCode ? body.stateCode.toUpperCase() : null,
        district: body.district || null,
        gender: body.gender || null,
        maritalStatus: body.maritalStatus || null,
        annualIncome: body.annualIncome != null ? Number(body.annualIncome) : null,
        employmentStatus: body.employmentStatus || null,
        educationLevel: body.educationLevel || null,
        socialCategory: body.socialCategory || null,
        residenceType: body.residenceType || null,
        disability: body.disability != null ? Boolean(body.disability) : null,
        interests: body.interests || [],
        updatedAt: new Date().toISOString()
      };
      profiles.set(currentUserId, updated);
      return sendJson(res, 200, updated);
    }

    // 9. Eligibility: GET
    if (pathname.startsWith('/api/v1/eligibility/') && method === 'GET') {
      const schemeId = pathname.replace('/api/v1/eligibility/', '');
      const scheme = schemes.find(s => s.id === schemeId);
      if (!scheme || !scheme.published || scheme.reviewStatus !== 'APPROVED') {
        return sendJson(res, 404, { message: 'Scheme not found' });
      }
      const profile = profiles.get(currentUserId);
      if (!profile) {
        return sendJson(res, 400, { message: 'Complete your citizen profile first' });
      }
      const assessment = evaluateEligibility(profile, scheme);
      return sendJson(res, 200, assessment);
    }

    // 10. Documents: My Checklist GET
    if (pathname === '/api/v1/documents/my-checklist' && method === 'GET') {
      const map = userChecklists.get(currentUserId) || new Map();
      return sendJson(res, 200, Array.from(map.values()));
    }

    // 11. Documents: My Checklist PUT
    if (pathname === '/api/v1/documents/my-checklist' && method === 'PUT') {
      const body = await readBody(req);
      const doc = documentCatalog.find(d => d.id === body.documentId);
      if (!doc) {
        return sendJson(res, 404, { message: 'Document catalog item not found' });
      }
      let map = userChecklists.get(currentUserId);
      if (!map) {
        map = new Map();
        userChecklists.set(currentUserId, map);
      }
      const item = {
        id: crypto.randomUUID(),
        documentId: doc.id,
        code: doc.code,
        title: doc.title,
        category: doc.category,
        status: body.status || 'NEEDED',
        notes: body.notes || '',
        updatedAt: new Date().toISOString()
      };
      map.set(doc.id, item);
      return sendJson(res, 200, item);
    }

    // 12. Documents: My Checklist DELETE
    if (pathname.startsWith('/api/v1/documents/my-checklist/') && method === 'DELETE') {
      const itemId = pathname.replace('/api/v1/documents/my-checklist/', '');
      const map = userChecklists.get(currentUserId);
      if (map) {
        for (const [docId, val] of map.entries()) {
          if (val.id === itemId) {
            map.delete(docId);
            break;
          }
        }
      }
      return sendJson(res, 200, { success: true });
    }

    // 13. Application Tracker: GET
    if (pathname === '/api/v1/my-applications' && method === 'GET') {
      const map = userApplications.get(currentUserId) || new Map();
      return sendJson(res, 200, Array.from(map.values()));
    }

    // 14. Application Tracker: POST
    if (pathname === '/api/v1/my-applications' && method === 'POST') {
      const body = await readBody(req);
      const scheme = schemes.find(s => s.id === body.schemeId);
      if (!scheme) {
        return sendJson(res, 404, { message: 'Scheme not found' });
      }
      let map = userApplications.get(currentUserId);
      if (!map) {
        map = new Map();
        userApplications.set(currentUserId, map);
      }
      for (const app of map.values()) {
        if (app.schemeId === body.schemeId) {
          return sendJson(res, 400, { message: 'This scheme is already in your tracker' });
        }
      }
      const newApp = {
        id: crypto.randomUUID(),
        schemeId: scheme.id,
        status: 'INTERESTED',
        notes: body.notes || '',
        appliedOn: null,
        createdAt: new Date().toISOString(),
        lastUpdatedAt: new Date().toISOString()
      };
      map.set(newApp.id, newApp);
      return sendJson(res, 200, newApp);
    }

    // 15. Application Tracker: PUT
    if (pathname.startsWith('/api/v1/my-applications/') && method === 'PUT') {
      const appId = pathname.replace('/api/v1/my-applications/', '');
      const map = userApplications.get(currentUserId);
      const app = map ? map.get(appId) : null;
      if (!app) {
        return sendJson(res, 404, { message: 'Tracker entry not found' });
      }
      const body = await readBody(req);
      app.status = body.status || app.status;
      app.notes = body.notes !== undefined ? body.notes : app.notes;
      app.appliedOn = body.appliedOn || app.appliedOn;
      app.lastUpdatedAt = new Date().toISOString();
      return sendJson(res, 200, app);
    }

    // 16. Application Tracker: DELETE
    if (pathname.startsWith('/api/v1/my-applications/') && method === 'DELETE') {
      const appId = pathname.replace('/api/v1/my-applications/', '');
      const map = userApplications.get(currentUserId);
      if (map) map.delete(appId);
      return sendJson(res, 200, { success: true });
    }

    // 17. Admin Review Queue: GET
    if (pathname === '/api/v1/admin/scheme-reviews/queue' && method === 'GET') {
      if (authUser.role !== 'ADMIN' && authUser.role !== 'REVIEWER') {
        return sendJson(res, 403, { message: 'Access denied: Admin or Reviewer role required' });
      }
      const inReview = schemes.filter(s => s.reviewStatus === 'IN_REVIEW');
      return sendJson(res, 200, inReview);
    }

    // Fallback 404
    return sendJson(res, 404, { message: `Route ${method} ${pathname} not found` });

  } catch (err) {
    return sendJson(res, 500, { message: err.message || 'Internal server error' });
  }
};
