package com.example.ExampleApiDemo.util;

public class ResumeUtils {

	public final static String RESUME_PROMPT = "<objective>\r\n" + //
			"Parse a text-formatted resume efficiently and extract diverse candidate's data into a structured JSON format.\r\n"
			+ //
			"</objective>\r\n" + //
			"\r\n" + //
			"<input>\r\n" + //
			"The following text is the candidate's resume in plain text format:\r\n" + //
			"\r\n" + //
			"{resume_text}\r\n" + //
			"</input>\r\n" + //
			"\r\n" + //
			"<instructions>\r\n" + //
			"## Follow these steps to extract and structure the resume information:\r\n" + //
			"\r\n" + //
			"1. Analyze Structure:\r\n" + //
			"- Examine the text-formatted resume to identify key sections (e.g., personal information, education, experience, skills, certifications).\r\n"
			+ //
			"- Note any unique formatting or organization within the resume.\r\n" + //
			"\r\n" + //
			"2. Extract Information:\r\n" + //
			"- Systematically parse each section, extracting relevant details.\r\n" + //
			"- Pay attention to dates, titles, organizations, and descriptions.\r\n" + //
			"\r\n" + //
			"3. Handle Variations:\r\n" + //
			"- Account for different resume styles, formats, and section orders.\r\n" + //
			"- Adapt the extraction process to accurately capture data from various layouts.\r\n" + //
			"\r\n" + //
			"5. Optimize Output:\r\n" + //
			"- Handle missing or incomplete information appropriately (use empty arrays/objects as needed).\r\n" + //
			"- Standardize date formats, if applicable.\r\n" + //
			"- have all the keys of JOSN in lowercase.\r\n" + //
			"- if there is more than one word in a key, convert them into camelCase. \r\n" + //
			"- make professionalSummary as String. \r\n" + //
			"- make professionalExperience as List of String and each String should be within 15 to 20 words. example \"professionalExperience\": [ \r\n"
			+ //
			"Delivered Finacle core customizations to prevent and monitor fraudulent transactions and anti-money laundering activities, integrating Finacle with Clari5 application.\",\r\n"
			+ //
			"Developed and implemented a pricing module in Finacle Core for a Tanzanian bank, decommissioning their legacy TBMS application for charge collection.\",\r\n"
			+ //
			"Customized CRM functionalities, coordinating client interaction and supporting UAT testing and production deployment for a Mauritian bank.\",\r\n"
			+ //
			"Worked in Agile methodology, developing PL/SQL packages, procedures, and functions for various backend programs, enhancing existing customizations and optimizing queries for Deutsche Bank.\",\r\n"
			+ //
			"Developed custom menus, batch jobs, and product customizations using Finacle scripting, JavaScript, JSP, and iReport, creating database objects using SQL and shell scripts for UCO Bank.\",\r\n"
			+ //
			"Developed custom menus, performed Finacle scripting and ONS customizations, debugged issues, and performed unit testing for Bank of India (BOI).\" ] \r\n"
			+ // " + //
			"- also add this json at first: \"headers\": {\n" + //
			"      \"candidateName\": \"candidate name\",\n" + //
			"      \"candidatePosition\": \"candidate's current position\"\n" + //
			"    } \r\n" + //
			"\r\n" + //
			"6. Validate:\r\n" + //
			"- Review the extracted data for consistency and completeness.\r\n" + //
			"- Ensure all required fields are populated if the information is available in the {resume_text}.\r\n" + //
			"\r\n" + //
			"7. Title/Headers Handling:\r\n" + //
			"- Maintain the order of clients in project experience.\r\n" + //
			"- Identify the correct project names over general subheaders.\r\n" + //
			"\r\n" + //
			"## Step to follow to write a JSON resume section of \"Professional Summary\" for the candidate.\r\n" + //
			"1. Analyze my `professional summary` details from {resume_text} to match job requirements.\r\n" + //
			"2. Create a JSON resume section that highlights strongest matches\r\n" + //
			"3. Optimize JSON section for clarity and relevance to the {resume_text}.\r\n" + //
			"\r\n" + //
			"Instructions:\r\n" + //
			"1. Focus: Craft relevant `professional summary` aligned with the {resume_text}.\r\n" + //
			"2. Content:\r\n" + //
			"2.1. Paragraph: single paragraph with limit of 4-6 lines, closely mirroring {resume_text}.\r\n" + //
			"2.2. Impact: Quantify paragraph point for measurable results.\r\n" + //
			"2.3. Storytelling: Utilize STAR methodology (Situation, Task, Action, Result) implicitly within paragraph text.\r\n"
			+ //
			"2.4. Action Verbs: Showcase technical skills with strong, active verbs.\r\n" + //
			"2.5. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"2.6. Structure: Each paragraph follows \"XX+ years of experience...\" format.\r\n" + //
			"3. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"4. Specificity: Prioritize relevance to the {resume_text} over general `professional summary` details.\r\n"
			+ //
			"5. Style:\r\n" + //
			"5.1. Voice: Use active voice whenever possible.\r\n" + //
			"5.2. Proofreading: Ensure impeccable spelling and grammar.\r\n" + //
			"\r\n" + //
			"<example>\r\n" + //
			"\"professional summary\": [\r\n" + //
			"\t{{\r\n" + //
			"\t\"Seasoned Business Analyst/Project manager with over 10 years of consultancy expertise. Specializing in overseeing software development projects across Manufacturing, Banking, and Finance sectors. Proficient in both Waterfall and Agile methodologies. Effective in team management, stakeholder relations, and technology adaptation. Known for guiding teams to project success through coaching and mentoring.\"\r\n"
			+ //
			"\t}},\r\n" + //
			"\t{{\r\n" + //
			"\t\"As a Technical Lead with extensive experience in web application development, particularly in MERN stack projects, JavaScript, TypeScript and Next JS, I have worked across multiple domains, including assurance, telecom, IT infrastructure, and retail applications. I have participated in designing application architecture from the ground up and worked with Microsoft Azure DevOps and AWS services such as ECS, S3 for static site deployment and Elastic Beanstalk for Node JS applications.\"\r\n"
			+ //
			"\t}},\r\n" + //
			"\t[and So on ...]\r\n" + //
			"]\r\n" + //
			"</example>\r\n" + //
			"\r\n" + //
			"## Step to follow to write a JSON resume section of \"Professional Experience\" for the candidate.\r\n" + //
			"if `professional experience` section present in {resume_text}:\r\n" + //
			"\t{{\r\n" + //
			"\t1. Analyze my `professional experience` details from {resume_text} to match job requirements.\r\n" + //
			"\t2. Create a JSON resume section that highlights strongest matches\r\n" + //
			"\t3. Optimize JSON section for clarity and relevance to the {resume_text}.\r\n" + //
			"\r\n" + //
			"\tInstructions:\r\n" + //
			"\t1. Focus: Craft relevant `professional experience` aligned with the {resume_text}.\r\n" + //
			"\t2. Content:\r\n" + //
			"\t2.1. Bullet points: 4-7 bullet points as per the {resume_text} do not exceed more than 220 tokens.\r\n" + //
			"\t2.2. Impact: Quantify bullet points for measurable results.\r\n" + //
			"\t2.3. Storytelling: Utilize STAR methodology (Situation, Task, Action, Result) implicitly within each bullet point.\r\n"
			+ //
			"\t2.4. Action Verbs: Showcase technical skills with strong, active verbs.\r\n" + //
			"\t2.5. Honesty: Prioritize truthfulness and objective language, mirrors to {resume_text}.\r\n" + //
			"\t2.6. Structure: Each bullet point follows \"Did X by doing Y, achieved Z\" format.\r\n" + //
			"\t2.7. Specificity: Prioritize relevance to the {resume_text} over general `professional experience`.\r\n"
			+ //
			"\t3. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"\t4. Specificity: Prioritize relevance to the {resume_text} over general `professional experience` details.\r\n"
			+ //
			"\t5. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"\t6. Style:\r\n" + //
			"\t6.1. Voice: Use active voice whenever possible.\r\n" + //
			"\t6.2. Proofreading: Ensure impeccable spelling and grammar.\r\n" + //
			"\t}}\r\n" + //
			"else:\r\n" + //
			"\t{{\r\n" + //
			"\t1. Analyze {resume_text} for `professional experience` details.\r\n" + //
			"\t2. Create a JSON resume section that highlights strongest matches\r\n" + //
			"\t3. Optimize JSON section for clarity and relevance to the {resume_text}.\r\n" + //
			"\r\n" + //
			"\tInstructions:\r\n" + //
			"\t1. Focus: Craft relevant `professional experience` aligned with the {resume_text}.\r\n" + //
			"\t2. Content:\r\n" + //
			"\t2.1. Bullet points: design all possible bullet points as per the {resume_text}.\r\n" + //
			"\t2.2. Impact: Quantify bullet points for measurable results.\r\n" + //
			"\t2.3. Storytelling: Utilize STAR methodology (Situation, Task, Action, Result) implicitly within each bullet point.\r\n"
			+ //
			"\t2.4. Action Verbs: Showcase technical skills with strong, active verbs.\r\n" + //
			"\t2.5. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"\t2.6. Structure: Each bullet point follows \"Did X by doing Y, achieved Z\" format.\r\n" + //
			"\t2.7. Specificity: Prioritize relevance to the {resume_text} over general `professional experience`.\r\n"
			+ //
			"\t3. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"\t4. Specificity: Prioritize relevance to the {resume_text} over general `professional experience` details.\r\n"
			+ //
			"\t5. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"\t6. Style:\r\n" + //
			"\t6.1. Voice: Use active voice whenever possible.\r\n" + //
			"\t6.2. Proofreading: Ensure impeccable spelling and grammar.}}\r\n" + //
			"\r\n" + //
			"<example>\r\n" + //
			"\"professional experience\": [\r\n" + //
			"\t[\r\n" + //
			"\t\t\"Skilled in the formulation and implementation of pioneering software solutions, substantially elevating business productivity.\",\r\n"
			+ //
			"\t\t\"Proficient in utilizing a diverse set of programming languages, tools, databases for backend development, ensuring seamless integration and optimal performance.\",\r\n"
			+ //
			"\t\t\"Renowned for successfully implementing strategies that amplify overall performance of the software systems.\",\r\n"
			+ //
			"\t\t\"Exhibits resilient leadership characteristics, promoting team collaboration and propelling progress amidst intricate technical obstacles.\",\r\n"
			+ //
			"\t\t[and So on ...],\r\n" + //
			"\t],\r\n" + //
			"\t[\r\n" + //
			"\t\t\"10 Years of BFSI industry experience in Banking (HDFC Bank) and Insurance (ICICI Lombard), with MBA background.\",\r\n"
			+ //
			"\t\t\"Domain Knowledge: Retail and Corporate Banking - CASA, Term Deposits, Mortgages, Payments, Cards, Collections, Recoveries, Treasury, Insurance, Risk Management, Digital Banking, End-to-end Financial Processes.\",\r\n"
			+ //
			"\t\t\"Proficiency Forte: Customer journeys, Operations and processes, Regulatory compliance, Applications and functionality, IT Product hands-on, Platform migration, Workflow management, Digital transformation, UI and UX Enhancements, Business readiness, Data and MI reporting, Investments and Portfolio management,\",\r\n"
			+ //
			"\t\t[and So on ...]\r\n" + //
			"\t],\r\n" + //
			"\t[\r\n" + //
			"\t\t\"Requirement Elicitation\",\r\n" + //
			"\t\t\"Stakeholder Management\",\r\n" + //
			"\t\t\"Problem Solving\",\r\n" + //
			"\t\t\"Effective Communication\",\r\n" + //
			"\t\t\"Team Collaboration\",\r\n" + //
			"\t\t\"Data Analysis\",\r\n" + //
			"\t\t\"Risk Management\",\r\n" + //
			"\t\t\"Project Management\",\r\n" + //
			"\t\t\"Agile Methodologies\",\r\n" + //
			"\t\t[and So on ...]\r\n" + //
			"\t],\r\n" + //
			"\t[and So on ...]\r\n" + //
			"]\r\n" + //
			"</example>\r\n" + //
			"\r\n" + //
			"## Step to follow to write a JSON resume section of \"Awards\" for the candidate.\r\n" + //
			"1. Analyze my achievements details to match job requirements.\r\n" + //
			"2. Create a JSON resume section that highlights strongest matches\r\n" + //
			"3. Optimize JSON section for clarity and relevance to the job description.\r\n" + //
			"\r\n" + //
			"Instructions:\r\n" + //
			"1. Focus: Craft relevant achievements aligned with the {resume_text}.\r\n" + //
			"2. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"3. Specificity: Prioritize relevance to the specific job over general achievements.\r\n" + //
			"4. Style:\r\n" + //
			"4.1. Voice: Use active voice whenever possible.\r\n" + //
			"4.2. Proofreading: Ensure impeccable spelling and grammar.\r\n" + //
			"\r\n" + //
			"<example>\r\n" + //
			"\"awards\": [\r\n" + //
			"\t\"Won E-yantra Robotics Competition 2018 - IITB.\",\r\n" + //
			"\t\"1st prize in “Prompt Engineering Hackathon 2023 for Humanities”\",\r\n" + //
			"\t\"Received the 'Extra Miller - 2021' award at Winjit Technologies for outstanding performance.\",\r\n" + //
			"\t[and So on ...]\r\n" + //
			"]\r\n" + //
			"</example>\r\n" + //
			"\r\n" + //
			"## Step to follow to write a JSON resume section of \"Certifications\" for an applicant applying for job posts.\r\n"
			+ //
			"\r\n" + //
			"1. Analyze my certification details to match job requirements.\r\n" + //
			"2. Create a JSON resume section that highlights strongest matches.\r\n" + //
			"3. Optimize JSON section for clarity and relevance to the job description.\r\n" + //
			"\r\n" + //
			"Instructions:\r\n" + //
			"1. Focus: Include relevant certifications aligned with the job description.\r\n" + //
			"2. Proofreading: Ensure impeccable spelling and grammar.\r\n" + //
			"\r\n" + //
			"<example>\r\n" + //
			"\"certifications\": [\r\n" + //
			"\t\"Deep Learning Specialization by DeepLearning.AI, Coursera Inc.\",\r\n" + //
			"\t\"Server-side Backend Development by The Hong Kong University of Science and Technology.\",\r\n" + //
			"\t[and So on ...]\r\n" + //
			"],\r\n" + //
			"</example>\r\n" + //
			"\r\n" + //
			"## Step to follow to write a JSON resume section of \"Education\" for an candidate:\r\n" + //
			"\r\n" + //
			"1. Analyze my education details to match job requirements.\r\n" + //
			"2. Create a JSON resume section that highlights strongest matches\r\n" + //
			"3. Optimize JSON section for clarity and relevance to the job description.\r\n" + //
			"\r\n" + //
			"Instructions:\r\n" + //
			"- Keep education from Bachelor's degree onwards, igonre previous qualifications.\r\n" + //
			"- Maintain truthfulness and objectivity in listing experience.\r\n" + //
			"- Prioritize specificity - with respect to job - over generality.\r\n" + //
			"- Proofread and Correct spelling and grammar errors.\r\n" + //
			"- Aim for clear expression over impressiveness.\r\n" + //
			"- Prefer active voice over passive voice.\r\n" + //
			"\r\n" + //
			"<example>\r\n" + //
			"\"education\": [\r\n" + //
			"\t\"B.Tech in Information Technology, Full-time, Graduated in 2009\",\r\n" + //
			"\t\"M. Tech Integrated Software Engineering, Vellore Institute of Technology, Tamil Nādu, India, 2021\",\r\n"
			+ //
			"\t\"Masters of Science - Computer Science (Thesis), Arizona State University, Tempe, USA, 2025\",\r\n" + //
			"\t\"Passed with 75% Marks in B. E (E.C. E) at K. Ramakrishnan College of Technology, Trichy\",\r\n" + //
			"[and So on ...]\r\n" + //
			"],\r\n" + //
			"</example>\r\n" + //
			"\r\n" + //
			"## Step to follow to write a JSON resume section of \"Credits\" for an candidate:\r\n" + //
			"\r\n" + //
			"1. Analyze my Credits details to match job requirements.\r\n" + //
			"2. Create a JSON resume section that highlights strongest matches.\r\n" + //
			"3. Optimize JSON section for clarity and relevance to the job description.\r\n" + //
			"4. credits must be if List of object as {category: 'string', items: ['string', 'string'...] }.\r\n" + //
			"\r\n" + //
			"Instructions:\r\n" + //
			"- look under the `skills` section to find the credits.\r\n" + //
			"- keep all the listed `skills` from extracted text.\r\n" + //
			"- Specificity: Prioritize relevance to the specific job over general achievements.\r\n" + //
			"- Proofreading: Ensure impeccable spelling and grammar.\r\n" + //
			"\r\n" + //
			"<example>\r\n" + //
			"\"credits\": [\r\n" + //
			"\t{{\r\n" + //
			"\t\"category\": \"Programming Languages\",\r\n" + //
			"\t\"items\": [\"Python\", \"JavaScript\", \"C#\", and so on ...]\r\n" + //
			"\t}},\r\n" + //
			"\t{{\r\n" + //
			"\t\"category\": \"Cloud and DevOps\",\r\n" + //
			"\t\"items\": [ \"Azure\", \"AWS\", and so on ... ]\r\n" + //
			"\t}},\r\n" + //
			"\tand so on ...\r\n" + //
			"]\r\n" + //
			"</example>\r\n" + //
			"\r\n" + //
			"## Step to follow to write a JSON resume section of \"Project Experience\" for an candidate:\r\n" + //
			"\r\n" + //
			"1. Analyze my project details to match job requirements from {resume_text}.\r\n" + //
			"2. Create a JSON resume section that highlights strongest matches\r\n" + //
			"3. Optimize JSON section for clarity and relevance to the job description.\r\n" + //
			"\r\n" + //
			"Instructions:\r\n" + //
			"1. Focus: Craft all relevant project experiences present in the {resume_text}.\r\n" + //
			"2. Content:\r\n" + //
			"2.1. Bullet points: all per experiences, without making any modifications.\r\n" + //
			"2.2. Impact: Quantify each bullet point for measurable results.\r\n" + //
			"2.3. Storytelling: Utilize STAR methodology (Situation, Task, Action, Result) implicitly within each bullet point.\r\n"
			+ //
			"2.4. Action Verbs: Showcase soft skills with strong, active verbs.\r\n" + //
			"2.5. Honesty: Prioritize truthfulness and objective language.\r\n" + //
			"2.6. Structure: Each bullet point follows \"Did X by doing Y, achieved Z\" format.\r\n" + //
			"2.7. Specificity: Prioritize relevance to the specific job over general achievements.\r\n" + //
			"3. Style:\r\n" + //
			"3.1. Clarity: Clear expression trumps impressiveness.\r\n" + //
			"3.2. Voice: Use active voice whenever possible.\r\n" + //
			"3.3. Proofreading: Ensure impeccable spelling and grammar.\r\n" + //
			"\r\n" + //
			"<example>\r\n" + //
			"\"projectExperience\": [\r\n" + //
			"\t{{\r\n" + //
			"\t\"projectDetails\":[\r\n" + //
			"\t{{\r\n" + //
			"\t\"key\": \"client\",\r\n" + //
			"\t\"value\": \"CustomerXPs Software Pvt Lmt\",\r\n" + //
			"\t}},\r\n" + //
			"\t{{\r\n" + //
			"\t\"key\": \"project\",\r\n" + //
			"\t\"value\": \"Search Engine for All file types - Sunhack Hackathon - Meta & Amazon Sponsored\",\r\n" + //
			"\t}},\r\n" + //
			"\t{{\r\n" + //
			"\t\"key\": \"role\",\r\n" + //
			"\t\"value\": \"Team Lead\",\r\n" + //
			"\t}},\r\n" + //
			"\t{{\r\n" + //
			"\t\"key\": \"location\",\r\n" + //
			"\t\"value\": \"Pune, Maharashtra\",\r\n" + //
			"\t}},\r\n" + //
			"\t{{\r\n" + //
			"\t\"key\": \"duration\",\r\n" + //
			"\t\"value\": \"Nov 2023 - Jan 2025\",\r\n" + //
			"\t}},\r\n" + //
			"\t{{\r\n" + //
			"\t\"key\": \"tools\",\r\n" + //
			"\t\"value\": [\"Node\", \"JS\", \".NET\", \"Redux\", \"MSAL\", \"MongoDB\", so on ... ]\r\n" + //
			"\t}},\r\n" + //
			"]\r\n" + //
			"\t\"description\": \"Automated data ingestion and market risk visualization using historical data for decision-making.\"\r\n"
			+ //
			"\t\"responsibilities\": [\r\n" + //
			"\t\t\"Envisioned Solution Architecture and Design for modernization efforts\",\r\n" + //
			"\t\t\"Adopted DevOps practices including CI/CD, Test Automation, Deployment automation, etc.\",\r\n" + //
			"\t\t\"Participated in release review/requirement analysis and design review meetings\",\r\n" + //
			"\t\t[and So on ...]\r\n" + //
			"\t]\r\n" + //
			"\t}}\r\n" + //
			"\t[and So on ...]\r\n" + //
			"]\r\n" + //
			"</example>\r\n" + //
			"\r\n" + //
			"</instructions>";

	// new “raw formatter” prompt:
	public static final String RAW_FORMATTER_PROMPT = "Extract the following resume text into a JSON object with exactly these top-level fields and subfields.  Do **not** paraphrase, summarize, or alter any wording—take each section verbatim:\n"
			+
			"\n" +
			"Resume text:\n" +
			"{resume_text}\n" +
			"\n" +
			"Output JSON schema:\n" +
			"{\n" +
			"  \"headers\": {\n" +
			"    \"candidateName\": string,         // full name from top of resume\n" +
			"    \"candidatePosition\": string      // current role/title (if present; otherwise empty string)\n" +
			"  },\n" +
			"  \"professionalSummary\": string,     // the entire “PROFESSIONAL SUMMARY” paragraph\n" +
			"  \"professionalExperience\": [        // list each bullet under “WORK EXPERIENCE” as a separate string\n"
			+
			"    string, ...\n" +
			"  ],\n" +
			"  \"awards\": [                        // list any awards (if none, output [])\n" +
			"    string, ...\n" +
			"  ],\n" +
			"  \"certifications\": [                // list any certifications (if none, [])\n" +
			"    string, ...\n" +
			"  ],\n" +
			"  \"education\": [                     // list each education entry (institution + degree + dates) as a string\n"
			+
			"    string, ...\n" +
			"  ],\n" +
			"  \"credits\": [                       // list skill-categories and items, e.g.:\n" +
			"    {\n" +
			"      \"category\": string,\n" +
			"      \"items\": [ string, ... ]\n" +
			"    }, ...\n" +
			"  ],\n" +
			"  \"projectExperience\": [             // if present, list each project as object with these keys:\n" +
			"    {\n" +
			"      \"projectDetails\": [ \n" +
			"			{key: \"string\", value: \"string\"}, // key like client, project, duration, role, etc and values as their value \n"
			+
			"			{key: \"string\", value: \"string\"},... \n" +
			"		],\n" +
			"      \"description\": string,\n" +
			"      \"responsibilities\": [ string, ... ]\n" +
			"    }, ...\n" +
			"  ]\n" +
			"}\n" +
			"\n" +
			"Rules:\n" +
			"1. Output **only** valid JSON matching this schema.\n" +
			"2. Use empty arrays (`[]`) or `\"\"` for missing sections/fields.\n" +
			"3. Do **not** add any extra keys or commentary.\n" +
			"4. Preserve exact whitespace-trimmed text from the resume (no rewording).\n" +
			"\n" +
			"Provide the JSON object as your sole output.\n";

}
