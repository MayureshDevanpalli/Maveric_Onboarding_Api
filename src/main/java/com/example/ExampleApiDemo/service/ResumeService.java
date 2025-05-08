package com.example.ExampleApiDemo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.ExampleApiDemo.exceptions.GeminiException;
import com.example.ExampleApiDemo.model.ResumeData;
import com.example.ExampleApiDemo.model.SkillRequest;
import com.example.ExampleApiDemo.model.SkillResponse;
import com.example.ExampleApiDemo.util.ResumeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

  // ST Start
  public ResumeData extractResumeData(MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    String resumeText = "";

    if ("pdf".equals(extension)) {
      resumeText = extractTextFromPdf(file);
    } else if ("docx".equals(extension)) {
      resumeText = extractTextFromDocx(file);
    } else {
      throw new IllegalArgumentException("Unsupported file type. Please upload a PDF or DOCX.");
    }

    String prompt = ResumeUtils.RESUME_PROMPT;

    Map<String, Object> body = Map.of(
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", prompt + "\n\n" + resumeText)))),
        "generationConfig", Map.of(
            "temperature", 0.3
        // "maxOutputTokens", 1024
        ));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
      String jsonResponse = extractJsonFromGeminiResponse(response.getBody());
      return new ObjectMapper().readValue(jsonResponse, ResumeData.class);
    } catch (Exception e) {
      log.error("Gemini API call failed: {}" + e.getLocalizedMessage());
      throw new GeminiException("Error while extracting resume data. Please try again later.", e);
    }
  }

  public ResumeData extractRawWithFormatterPrompt(MultipartFile file) throws IOException {
    String fileName = file.getOriginalFilename();
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    String resumeText = "";

    if ("pdf".equals(extension)) {
      resumeText = extractTextFromPdf(file);
    } else if ("docx".equals(extension)) {
      resumeText = extractTextFromDocx(file);
    } else {
      throw new IllegalArgumentException("Unsupported file type. Please upload a PDF or DOCX.");
    }

    String prompt = ResumeUtils.RAW_FORMATTER_PROMPT;

    Map<String, Object> body = Map.of(
        "contents", List.of(
            Map.of("parts", List.of(
                Map.of("text", prompt + "\n\n" + resumeText)))),
        "generationConfig", Map.of(
            "temperature", 0.3
        // "maxOutputTokens", 1024
        ));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

    try {
      ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
      String jsonResponse = extractJsonFromGeminiResponse(response.getBody());
      return new ObjectMapper().readValue(jsonResponse, ResumeData.class);
    } catch (Exception e) {
      log.error("Gemini API call failed: {}" , e.getLocalizedMessage());
      throw new GeminiException("Error while extracting raw resume data. Please try again later.", e);
    }
  }

  // ST End
  private String extractTextFromDocx(MultipartFile file) throws IOException {
    try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
      return new XWPFWordExtractor(doc).getText();
    }
  }

  private String extractTextFromPdf(MultipartFile file) throws IOException {
    try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument
        .load(file.getInputStream())) {
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
          String fullText = textPart.get("text").toString().replace("```", "").replace("json", "");
          return fullText;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to extract JSON from Gemini response", e);
    }
    return "{}";
  }

  public SkillResponse extractSkills(SkillRequest request) {
    try {
      String skillPrompt = "";
      Path path = Paths.get("src/main/resources/templates/skillPrompt.txt");
      skillPrompt = Files.readString(path);
      String prompt = skillPrompt + "\n\nResume JSON:\n" +
          new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request.getResumeData()) +
          "\n\nJob Description:\n" + request.getJobDescription();

      Map<String, Object> body = Map.of(
          "contents", List.of(
              Map.of("parts", List.of(
                  Map.of("text", prompt)))),
          "generationConfig", Map.of(
              "temperature", 0.3));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);
      String json = extractJsonFromGeminiResponse(response.getBody());
      if (json.startsWith("```json"))
        json = json.substring(7).trim();
      if (json.endsWith("```"))
        json = json.substring(0, json.length() - 3).trim();

      SkillResponse skillResponse = new ObjectMapper().readValue(json, SkillResponse.class);
      Collections.sort(skillResponse.getResumeSkill());
      Collections.sort(skillResponse.getMatchedSkills());
      Collections.sort(skillResponse.getRequiredSkills());
      // return new ObjectMapper().readValue(json, SkillResponse.class);
      return skillResponse;

    } catch (Exception e) {
      throw new RuntimeException("Failed to extract skills using Gemini", e);
    }
  }

}
