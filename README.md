Functionality

The API exposes a single endpoint:

/extract_resume_details/

This endpoint accepts a resume file as a multipart/form-data upload (supporting both .pdf and .docx files) and returns a JSON response containing the extracted information, structured according to the schemas.py definition. Key extracted details include:

Headers: Candidate's name and designation.

Professional Summary: A concise overview of the candidate's profile.

Professional Experience: Detailed history of work experience.

Awards: Recognition and achievements.

Certifications: Relevant professional certifications.

Education: Educational qualifications.

Credits (Skills): Categorized skills and technologies.

Project Experience: Details of projects undertaken.

The extraction process is guided by prompts defined in prompt_manager.py, leveraging Google Gemini's capabilities to understand and structure resume data.

Getting Started

Follow these steps to set up and run the application:

Prerequisites

Python 3.7+

pip (Python package installer)

Google Gemini API Key: You will need a Google Gemini API key to use the language model. Obtain one and be prepared to set it as an environment variable.

Installation

Clone the repository:
Clone this repository to your local machine using Git:
гигант
git clone <repository_url>
cd mayureshdevanpalli-maveric_onboarding_api

Install Dependencies:
Navigate to the project directory and install the required Python packages using pip:

bash
pip install -r requirements.txt

Set the Google Gemini API Key:
Set your Google Gemini API key as an environment variable. You can do this in your terminal or by adding it to your system's environment variables.

Option 1: Terminal (for the current session)

bash
export GEMINI_API_KEY="YOUR_API_KEY_HERE"

Replace "YOUR_API_KEY_HERE" with your actual API key.

Option 2: .env file (requires python-decouple)

You can also create a .env file in the project root directory and add your API key there:

.env
GEMINI_API_KEY=YOUR_API_KEY_HERE

python-decouple will automatically load this variable.

Running the Application

Start the FastAPI server:
Navigate to the project directory in your terminal and run the following command:

bash
uvicorn main:app --reload --host 0.0.0.0 --port 8090

main:app: Specifies the app object in the main.py file.

--reload: Enables automatic reloading of the server on code changes (for development).

--host 0.0.0.0: Makes the application accessible from any network interface.

--port 8090: Specifies the port the application will run on.

Accessing the API:
Once the server is running, you can send POST requests to the /extract_resume_details/ endpoint.

API Usage

You can use tools like curl, Postman, or a Python requests library to interact with the API.

Example Request (using curl):

To extract details from a PDF file:

bash
curl -X POST -F "file=@/path/to/your/resume.pdf" http://localhost:8090/extract_resume_details/

To extract details from a DOCX file:

bash
curl -X POST -F "file=@/path/to/your/resume.docx" http://localhost:8090/extract_resume_details/

Replace /path/to/your/resume.pdf or /path/to/your/resume.docx with the actual path to your resume file.

Example Response:

The API will return a JSON response structured according to the ResumeSchema defined in schemas.py. A simplified example might look like this:

json
{
"headers": {
"candidateName": "John Doe",
"candidatePosition": "Software Engineer"
},
"professionalSummary": "Highly motivated software engineer...",
"professionalExperience": [
"Developed and maintained...",
"Collaborated with cross-functional teams..."
],
"awards": ["Employee of the Month"],
"certifications": ["AWS Certified Developer"],
"education": ["B.Sc. in Computer Science"],
"credits": [
{
"category": "Programming Languages",
"items": ["Python", "Java", "JavaScript"]
}
],
"projectExperience": [
{
"client": "Acme Corp",
"project": "New Feature Development",
"role": "Software Engineer",
"duration": "2022-2023",
"tools": ["React", "Node.js", "MongoDB"],
"description": "Developed key features...",
"responsibilities": ["Implemented user interfaces...", "Wrote unit tests..."]
}
]
}

Error Handling

The API provides basic error handling and will return appropriate HTTP status codes and JSON error messages in case of issues, such as:

400 Bad Request: For unsupported file formats.

500 Internal Server Error: For other unexpected errors during processing.

Contributing

Contributions to this project are welcome. Please feel free to open issues for bug reports or feature requests, or submit pull requests with your improvements.

License

This project is licensed under the [Specify your license here, e.g., MIT License] - see the LICENSE file for details.
