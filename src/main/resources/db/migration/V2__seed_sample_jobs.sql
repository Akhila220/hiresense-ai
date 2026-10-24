-- ==============================================================================
-- HireSense AI - V2 Sample Jobs Database Seeder
-- Clearly flagged with is_sample = true for transparency
-- ==============================================================================

INSERT INTO jobs (title, company, location, employment_type, experience_level, description, required_skills, preferred_skills, salary_range, is_sample)
VALUES
(
    'Junior Java Full-Stack Developer',
    'CloudScale Innovations (Sample)',
    'Remote / Bengaluru, India',
    'FULL_TIME',
    'ENTRY',
    'Looking for an enthusiastic Junior Java Full-Stack Developer to develop modern cloud-native web applications. You will work with Spring Boot microservices, PostgreSQL, and modern frontend templates or React.',
    'Java, Spring Boot, PostgreSQL, REST API, Git, HTML, CSS, JavaScript',
    'Docker, Thymeleaf, Hibernate, JUnit, Maven',
    '₹6,00,000 - ₹9,50,000 / year',
    TRUE
),
(
    'Backend Engineer (Java / Spring Boot)',
    'NexGen Fintech (Sample)',
    'Hyderabad, India (Hybrid)',
    'FULL_TIME',
    'MID',
    'Join our payments platform team to design high-throughput, low-latency microservices. Responsible for secure API design, database query optimization, and resilient transactional processing.',
    'Java, Spring Boot, Spring Security, PostgreSQL, Hibernate, Docker, REST API',
    'Redis, Kafka, AWS, CI/CD, Microservices',
    '₹12,00,000 - ₹18,00,000 / year',
    TRUE
),
(
    'Senior Cloud DevOps Engineer',
    'Aether Systems (Sample)',
    'Remote',
    'FULL_TIME',
    'SENIOR',
    'Lead our infrastructure automation, Kubernetes cluster orchestration, and continuous integration pipelines. Drive infrastructure-as-code and observability initiatives.',
    'AWS, Docker, Kubernetes, Terraform, CI/CD, Linux, Python, Git',
    'Prometheus, Grafana, Helm, Ansible, Shell Scripting',
    '₹22,00,000 - ₹32,00,000 / year',
    TRUE
),
(
    'Frontend React Developer',
    'PixelCraft Digital (Sample)',
    'Pune, India (Hybrid)',
    'FULL_TIME',
    'MID',
    'Build responsive, highly performant web applications with React, TypeScript, and modern state management. Partner closely with UX designers and backend API engineers.',
    'JavaScript, TypeScript, React, HTML5, CSS3, REST API, Git',
    'Next.js, Tailwind CSS, Redux, Jest, Figma',
    '₹10,00,000 - ₹15,00,000 / year',
    TRUE
),
(
    'Data Engineer',
    'OmniData Labs (Sample)',
    'Bengaluru, India',
    'FULL_TIME',
    'MID',
    'Design, build, and maintain robust batch and streaming data pipelines. Transform raw event data into structured data warehouse models for business intelligence and machine learning.',
    'Python, SQL, PostgreSQL, Apache Spark, Airflow, Docker, Git',
    'AWS, Snowflake, Kafka, dbt, Pandas',
    '₹14,00,000 - ₹20,00,000 / year',
    TRUE
),
(
    'Software Engineering Intern',
    'InnoSphere Labs (Sample)',
    'Remote / Chennai, India',
    'INTERNSHIP',
    'ENTRY',
    'Exciting 6-month internship for pre-final or final year engineering students. Gain real-world software engineering experience working on web backends, database modeling, and unit testing.',
    'Java, Python, Object-Oriented Programming, Data Structures, SQL, Git',
    'Spring Boot, JavaScript, Linux, Postman',
    '₹25,000 - ₹40,000 / month',
    TRUE
),
(
    'Full-Stack Web Developer',
    'Vanguard HealthTech (Sample)',
    'Noida, India (Hybrid)',
    'FULL_TIME',
    'MID',
    'Develop secure healthcare portals and telemedicine integrations. Requires end-to-end development capabilities from relational schema design to responsive user interface implementation.',
    'Java, Spring Boot, JavaScript, PostgreSQL, Bootstrap, REST API, Git',
    'Docker, Spring Security, TypeScript, AWS, Maven',
    '₹11,00,000 - ₹16,00,000 / year',
    TRUE
),
(
    'QA Automation Engineer',
    'QualityFirst Soft (Sample)',
    'Remote',
    'FULL_TIME',
    'MID',
    'Develop comprehensive automated testing suites for web services and frontend applications. Integrate test suites into CI/CD build pipelines and ensure regression-free releases.',
    'Java, Selenium, JUnit, TestNG, REST API, Postman, Git',
    'Cucumber, Docker, Jenkins, Playwright, SQL',
    '₹8,00,000 - ₹13,00,000 / year',
    TRUE
),
(
    'Machine Learning / AI Associate',
    'CognitiveWorks AI (Sample)',
    'Bengaluru, India',
    'FULL_TIME',
    'ENTRY',
    'Work with the AI engineering team to preprocess unstructured document text, train NLP feature extractors, and integrate LLM APIs into user-facing web tools.',
    'Python, Machine Learning, NLP, SQL, Pandas, NumPy, Git',
    'PyTorch, HuggingFace, FastAPI, Docker, Vector Databases',
    '₹9,00,000 - ₹14,00,000 / year',
    TRUE
),
(
    'Site Reliability & Security Engineer',
    'Fortress Cyber Solutions (Sample)',
    'Remote / Mumbai, India',
    'FULL_TIME',
    'SENIOR',
    'Manage enterprise security posture, vulnerability scans, authentication security, secret rotation, and 99.99% application uptime SLAs across cloud deployments.',
    'Linux, AWS, Docker, Kubernetes, Network Security, Bash, Python, CI/CD',
    'OWASP, Terraform, Vault, Prometheus, PostgreSQL',
    '₹24,00,000 - ₹35,00,000 / year',
    TRUE
);
