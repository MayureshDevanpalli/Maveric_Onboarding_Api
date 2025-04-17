package com.example.ExampleApiDemo.service;

import com.example.ExampleApiDemo.model.ResumeData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
@Service
public class ResumeService {

  @Value("${gemini.api.key}")
  private String apiKey;

  private String API_URL;

  @PostConstruct
  public void init() {
    API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey;
    System.out.println("API Key: " + apiKey); // Debug
  }

  private final RestTemplate restTemplate = new RestTemplate();

  /*public ResumeData extractResumeData(MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    List<Map<String, Object>> contents = new ArrayList<>();

    if ("pdf".equals(extension)) {
      byte[] pdfBytes = file.getBytes();
      String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

      Map<String, Object> pdfPart = new HashMap<>();
      pdfPart.put("inlineData", Map.of(
        "mimeType", "application/pdf",
        "data", base64Pdf
      ));
      contents.add(pdfPart);

    } else if ("docx".equals(extension)) {
      String extractedText = extractTextFromDocx(file);
      contents.add(Map.of("text", extractedText));

    } else {
      throw new IllegalArgumentException("Unsupported file type. Please upload a PDF or DOCX.");
    }

    // Request body without any manual prompt
    Map<String, Object> body = new HashMap<>();
    body.put("contents", contents);
    body.put("generationConfig", Map.of(
      "responseMimeType", "application/json"
    ));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
      ObjectMapper mapper = new ObjectMapper();
      return mapper.readValue(response.getBody(), ResumeData.class);
    } catch (HttpClientErrorException e) {
      throw new RuntimeException("Gemini API call failed: " + e.getResponseBodyAsString());
    }
  }*/


  //ST Start
  public ResumeData extractResumeData(MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    String resumeText;

    if ("pdf".equals(extension)) {
      resumeText = extractTextFromPdf(file);
    } else if ("docx".equals(extension)) {
      resumeText = extractTextFromDocx(file);
    } else {
      throw new IllegalArgumentException("Unsupported file type. Please upload a PDF or DOCX.");
    }

   // String prompt = "Extract the following structured data from this resume in JSON format as per schema: name, email, phone number, skills, experience, education.";
String prompt = "\"\"\"<objective>\n" +
  "            Parse a text-formatted resume efficiently and extract diverse candidate's data into a structured JSON format.\n" +
  "            </objective>\n" +
  "\n" +
  "            <input>\n" +
  "            The following text is the candidate's resume in plain text format:\n" +
  "\n" +
  "            {resume_text}\n" +
  "            </input>\n" +
  "\n" +
  "            <instructions>\n" +
  "            ## Follow these steps to extract and structure the resume information:\n" +
  "\n" +
  "            1. Analyze Structure:\n" +
  "            - Examine the text-formatted resume to identify key sections (e.g., personal information, education, experience, skills, certifications).\n" +
  "            - Note any unique formatting or organization within the resume.\n" +
  "\n" +
  "            2. Extract Information:\n" +
  "            - Systematically parse each section, extracting relevant details.\n" +
  "            - Pay attention to dates, titles, organizations, and descriptions.\n" +
  "\n" +
  "            3. Handle Variations:\n" +
  "            - Account for different resume styles, formats, and section orders.\n" +
  "            - Adapt the extraction process to accurately capture data from various layouts.\n" +
  "\n" +
  "            5. Optimize Output:\n" +
  "            - Handle missing or incomplete information appropriately (use null values or empty arrays/objects as needed).\n" +
  "            - Standardize date formats, if applicable.\n" +
  "\n" +
  "            6. Validate:\n" +
  "            - Review the extracted data for consistency and completeness.\n" +
  "            - Ensure all required fields are populated if the information is available in the resume.\n" +
  "\n" +
  "            ## Step to follow to write a JSON resume section of \"Awards\" for an candidate.\n" +
  "\n" +
  "\n" +
  "            1. Analyze my achievements details to match job requirements.\n" +
  "            2. Create a JSON resume section that highlights strongest matches\n" +
  "            3. Optimize JSON section for clarity and relevance to the job description.\n" +
  "\n" +
  "            Instructions:\n" +
  "            1. Focus: Craft relevant achievements aligned with the job description.\n" +
  "            2. Honesty: Prioritize truthfulness and objective language.\n" +
  "            3. Specificity: Prioritize relevance to the specific job over general achievements.\n" +
  "            4. Style:\n" +
  "            4.1. Voice: Use active voice whenever possible.\n" +
  "            4.2. Proofreading: Ensure impeccable spelling and grammar.\n" +
  "\n" +
  "            <example>\n" +
  "            \"awards\": [\n" +
  "                \"Won E-yantra Robotics Competition 2018 - IITB.\",\n" +
  "                \"1st prize in “Prompt Engineering Hackathon 2023 for Humanities”\",\n" +
  "                \"Received the 'Extra Miller - 2021' award at Winjit Technologies for outstanding performance.\",\n" +
  "                [and So on ...]\n" +
  "            ]\n" +
  "            </example>\n" +
  "\n" +
  "            ## Step to follow to write a JSON resume section of \"Certifications\" for an applicant applying for job posts.\n" +
  "\n" +
  "            1. Analyze my certification details to match job requirements.\n" +
  "            2. Create a JSON resume section that highlights strongest matches\n" +
  "            3. Optimize JSON section for clarity and relevance to the job description.\n" +
  "\n" +
  "            Instructions:\n" +
  "            1. Focus: Include relevant certifications aligned with the job description.\n" +
  "            2. Proofreading: Ensure impeccable spelling and grammar.\n" +
  "\n" +
  "            <example>\n" +
  "            \"certifications\": [\n" +
  "                {{\n" +
  "                \"name\": \"Deep Learning Specialization\",\n" +
  "                \"by\": \"DeepLearning.AI, Coursera Inc.\",\n" +
  "                \"link\": \"https://www.coursera.org/account/accomplishments/specialization/G3WPNWRYX628\"\n" +
  "                }},\n" +
  "                {{\n" +
  "                \"name\": \"Server-side Backend Development\",\n" +
  "                \"by\": \"The Hong Kong University of Science and Technology.\",\n" +
  "                \"link\": \"https://www.coursera.org/account/accomplishments/verify/TYMQX23D4HRQ\"\n" +
  "                }}\n" +
  "                ...\n" +
  "            ],\n" +
  "            </example>\n" +
  "\n" +
  "            ## Step to follow to write a JSON resume section of \"Education\" for an candidate:\n" +
  "\n" +
  "            1. Analyze my education details to match job requirements.\n" +
  "            2. Create a JSON resume section that highlights strongest matches\n" +
  "            3. Optimize JSON section for clarity and relevance to the job description.\n" +
  "\n" +
  "            Instructions:\n" +
  "            - Maintain truthfulness and objectivity in listing experience.\n" +
  "            - Prioritize specificity - with respect to job - over generality.\n" +
  "            - Proofread and Correct spelling and grammar errors.\n" +
  "            - Aim for clear expression over impressiveness.\n" +
  "            - Prefer active voice over passive voice.\n" +
  "\n" +
  "            <example>\n" +
  "            \"education\": [\n" +
  "            {{\n" +
  "                \"degree\": \"Masters of Science - Computer Science (Thesis)\",\n" +
  "                \"university\": \"Arizona State University, Tempe, USA\",\n" +
  "                \"from_date\": \"Aug 2023\",\n" +
  "                \"to_date\": \"May 2025\",\n" +
  "                \"grade\": \"3.8/4\",\n" +
  "                \"coursework\": [\n" +
  "                \"Operational Deep Learning\",\n" +
  "                \"Software verification, Validation and Testing\",\n" +
  "                \"Social Media Mining\",\n" +
  "                [and So on ...]\n" +
  "                ]\n" +
  "            }}\n" +
  "            [and So on ...]\n" +
  "            ],\n" +
  "            </example>\n" +
  "\n" +
  "            ## Step to follow to write a JSON resume section of \"Credits\" for an candidate:\n" +
  "\n" +
  "            1. Analyze my Credits details to match job requirements.\n" +
  "            2. Create a JSON resume section that highlights strongest matches.\n" +
  "            3. Optimize JSON section for clarity and relevance to the job description.\n" +
  "\n" +
  "            Instructions:\n" +
  "            - look under the `skills` section to find the credits.\n" +
  "            - keep all the listed `skills` from extracted text.\n" +
  "            - Specificity: Prioritize relevance to the specific job over general achievements.\n" +
  "            - Proofreading: Ensure impeccable spelling and grammar.\n" +
  "\n" +
  "            <example>\n" +
  "            \"skill_section\": [\n" +
  "                {{\n" +
  "                \"category\": \"Programming Languages\",\n" +
  "                \"items\": [\"Python\", \"JavaScript\", \"C#\", and so on ...]\n" +
  "                }},\n" +
  "                {{\n" +
  "                \"category\": \"Cloud and DevOps\",\n" +
  "                \"items\": [ \"Azure\", \"AWS\", and so on ... ]\n" +
  "                }},\n" +
  "                and so on ...\n" +
  "            ]\n" +
  "            </example>\n" +
  "\n" +
  "            ## Step to follow to write a JSON resume section of \"Work Experience\" for an candidate:\n" +
  "\n" +
  "            1. Analyze my Work details to match job requirements.\n" +
  "            2. Create a JSON resume section that highlights strongest matches\n" +
  "            3. Optimize JSON section for clarity and relevance to the job description.\n" +
  "\n" +
  "            Instructions:\n" +
  "            1. Focus: Craft three highly relevant work experiences aligned with the job description.\n" +
  "            2. Content:\n" +
  "            2.1. Bullet points: 3 per experience, closely mirroring job requirements.\n" +
  "            2.2. Impact: Quantify each bullet point for measurable results.\n" +
  "            2.3. Storytelling: Utilize STAR methodology (Situation, Task, Action, Result) implicitly within each bullet point.\n" +
  "            2.4. Action Verbs: Showcase soft skills with strong, active verbs.\n" +
  "            2.5. Honesty: Prioritize truthfulness and objective language.\n" +
  "            2.6. Structure: Each bullet point follows \"Did X by doing Y, achieved Z\" format.\n" +
  "            2.7. Specificity: Prioritize relevance to the specific job over general achievements.\n" +
  "            3. Style:\n" +
  "            3.1. Clarity: Clear expression trumps impressiveness.\n" +
  "            3.2. Voice: Use active voice whenever possible.\n" +
  "            3.3. Proofreading: Ensure impeccable spelling and grammar.\n" +
  "\n" +
  "            <example>\n" +
  "            \"work_experience\": [\n" +
  "                {{\n" +
  "                \"role\": \"Software Engineer\",\n" +
  "                \"company\": \"Winjit Technologies\",\n" +
  "                \"location\": \"Pune, India\"\n" +
  "                \"from_date\": \"Jan 2020\",\n" +
  "                \"to_date\": \"Jun 2022\",\n" +
  "                \"tools\": [\"jira\", \"git\", so on ... ]\n" +
  "                \"description\": [\n" +
  "                    \"Engineered 10+ RESTful APIs Architecture and Distributed services; Designed 30+ low-latency responsive UI/UX application features with high-quality web architecture; Managed and optimized large-scale Databases. (Systems Design)\",  \n" +
  "                    \"Initiated and Designed a standardized solution for dynamic forms generation, with customizable CSS capabilities feature, which reduces development time by 8x; Led and collaborated with a 12 member cross-functional team. (Idea Generation)\"  \n" +
  "                    and so on ...\n" +
  "                ]\n" +
  "                }},\n" +
  "                {{\n" +
  "                \"role\": \"Research Intern\",\n" +
  "                \"company\": \"IMATMI, Robbinsville\",\n" +
  "                \"location\": \"New Jersey (Remote)\"\n" +
  "                \"from_date\": \"Mar 2019\",\n" +
  "                \"to_date\": \"Aug 2019\",\n" +
  "                \"tools\": [\"Jenkins\", \"Intelli\", so on ... ]\n" +
  "                \"description\": [\n" +
  "                    \"Conducted research and developed a range of ML and statistical models to design analytical tools and streamline HR processes, optimizing talent management systems for increased efficiency.\",\n" +
  "                    \"Created 'goals and action plan generation' tool for employees, considering their weaknesses to facilitate professional growth.\",\n" +
  "                    and so on ...\n" +
  "                ]\n" +
  "                }}\n" +
  "            ],\n" +
  "            </example>\n" +
  "\n" +
  "\n" +
  "            ## Step to follow to write a JSON resume section of \"Project Experience\" for an candidate:\n" +
  "\n" +
  "            1. Analyze my project details to match job requirements.\n" +
  "            2. Create a JSON resume section that highlights strongest matches\n" +
  "            3. Optimize JSON section for clarity and relevance to the job description.\n" +
  "\n" +
  "            Instructions:\n" +
  "            1. Focus: Craft all project experiences present in the context.\n" +
  "            2. Content:\n" +
  "            2.1. Bullet points: 3 per experience, closely mirroring job requirements.\n" +
  "            2.2. Impact: Quantify each bullet point for measurable results.\n" +
  "            2.3. Storytelling: Utilize STAR methodology (Situation, Task, Action, Result) implicitly within each bullet point.\n" +
  "            2.4. Action Verbs: Showcase soft skills with strong, active verbs.\n" +
  "            2.5. Honesty: Prioritize truthfulness and objective language.\n" +
  "            2.6. Structure: Each bullet point follows \"Did X by doing Y, achieved Z\" format.\n" +
  "            2.7. Specificity: Prioritize relevance to the specific job over general achievements.\n" +
  "            3. Style:\n" +
  "            3.1. Clarity: Clear expression trumps impressiveness.\n" +
  "            3.2. Voice: Use active voice whenever possible.\n" +
  "            3.3. Proofreading: Ensure impeccable spelling and grammar.\n" +
  "\n" +
  "            <example>\n" +
  "            \"projects\": [\n" +
  "                {{\n" +
  "                \"Role\": \"Team Lead\"\n" +
  "                \"name\": \"Search Engine for All file types - Sunhack Hackathon - Meta & Amazon Sponsored\",\n" +
  "                \"type\": \"Hackathon\",\n" +
  "                \"link\": \"https://devpost.com/software/team-soul-1fjgwo\",\n" +
  "                \"from_date\": \"Nov 2023\",\n" +
  "                \"to_date\": \"Nov 2023\",\n" +
  "                \"tools\": [\"Node\", \"JS\", \".NET\", \"Redux\", \"MSAL\", \"MongoDB\", so on ... ]\n" +
  "                \"description\": [\n" +
  "                    \"1st runner up prize in crafted AI persona, to explore LLM's subtle contextual understanding and create innovative collaborations between humans and machines.\",\n" +
  "                    \"Devised a TabNet Classifier Model having 98.7% accuracy in detecting forest fire through IoT sensor data, deployed on AWS and edge devices 'Silvanet Wildfire Sensors' using technologies TinyML, Docker, Redis, and celery.\",\n" +
  "                    [and So on ...]\n" +
  "                ],\n" +
  "                \"responsibilities\": [\n" +
  "                    \"Envisioned Solution Architecture and Design for modernization efforts\",\n" +
  "                    \"Adopted DevOps practices including CI/CD, Test Automation, Deployment automation, etc.\",\n" +
  "                    \"Participated in release review/requirement analysis and design review meetings\",\n" +
  "                    [and So on ...]\n" +
  "                ]\n" +
  "                }}\n" +
  "                [and So on ...]\n" +
  "            ]\n" +
  "            </example>\n" +
  "\n" +
  "\n" +
  "\n" +
  "            </instructions>";
    Map<String, Object> body = Map.of(
      "contents", List.of(
        Map.of("parts", List.of(
          Map.of("text", prompt + "\n\n" + resumeText)
        ))
      ),
      "generationConfig", Map.of(
        "temperature", 0.3
        //"maxOutputTokens", 1024
      )
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
      ObjectMapper mapper = new ObjectMapper();
      String rawResponse = response.getBody();
      System.out.println(rawResponse);
      String json = extractJsonFromGeminiResponse(rawResponse);
      System.out.println(json);
      ObjectMapper objectMapper=new ObjectMapper();
      ResumeData data=objectMapper.readValue(json,ResumeData.class);
     return data;
    } catch (HttpClientErrorException e) {
      throw new RuntimeException("Gemini API call failed: " + e.getResponseBodyAsString());
    }
  }
  //ST End
  private String extractTextFromDocx(MultipartFile file) throws IOException {
    try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
      XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
      return extractor.getText();
    }
  }

  private String extractTextFromPdf(MultipartFile file) throws IOException {
    try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(file.getInputStream())) {
      return new org.apache.pdfbox.text.PDFTextStripper().getText(document);
    }
  }

  private String extractJsonFromGeminiResponse(String responseBody) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      Map<?, ?> map = mapper.readValue(responseBody, Map.class);
      List<?> candidates = (List<?>) map.get("candidates");

      if (!candidates.isEmpty()) {
        Map<?, ?> first = (Map<?, ?>) candidates.get(0);
        Map<?, ?> content = (Map<?, ?>) first.get("content");
        List<?> parts = (List<?>) content.get("parts");

        if (!parts.isEmpty()) {
          Map<?, ?> textPart = (Map<?, ?>) parts.get(0);
          String fullText = textPart.get("text").toString();
          System.out.println("-----------------------------------");
          System.out.println(fullText);
          return fullText;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to extract JSON from Gemini response", e);
    }
    return "{}";
  }
}


