package com.example.ExampleApiDemo.service;

import com.example.ExampleApiDemo.exceptions.GeminiException;
import com.example.ExampleApiDemo.model.*;
import com.example.ExampleApiDemo.util.ResumeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
      throw new GeminiException("Error while extracting resume data. Please try again later.");
    }
  }

  public byte[] downloadResume(ResumeData resumeData) {
    try (XWPFDocument document = new XWPFDocument(new ClassPathResource("Maveric_Template.docx").getInputStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {

      // Process headers
      processHeaders(document.getHeaderList(), resumeData.getHeaders().getCandidateName(),
          resumeData.getHeaders().getCandidatePosition());

      // Process summary
      processSummary(document, "SUMMARY", resumeData.getProfessionalSummary());

      // Process bullet list
      processBulletList(document, "EXPERIENCE", resumeData.getProfessionalExperience());

      // Process bullet list
      processBulletList(document, "AWARDS", resumeData.getAwards());

      // Process certifications
      processBulletList(document, "CERTIFICATION", resumeData.getCertifications());

      // Process educations
      processBulletList(document, "EDUCATION", resumeData.getEducation());

      // Process credits
      processCreditTable(document, "CREDITS", resumeData.getCredits());

      // Process project experience
      processProjectExperienceTable(document, "PROJECT_EXPERIENCE", resumeData.getProjectExperience());

      document.write(out);
      return out.toByteArray();

    } catch (IOException e) {
      log.error("Error while creating file: ", e);
    }
    return new byte[0];
  }

  private void processProjectExperienceTable(XWPFDocument doc, String placeholder,
      List<ProjectExperience> projectExperience) {

    List<XWPFParagraph> paragraphs = doc.getParagraphs();

    for (int i = 0; i < paragraphs.size(); i++) {
      XWPFParagraph paragraph = paragraphs.get(i);
      String text = paragraph.getText();

      if (text.contains(placeholder)) {
        // Remove runs containing the placeholder text
        for (XWPFRun run : paragraph.getRuns()) {
          if (run.getText(0) != null && run.getText(0).contains(placeholder)) {
            run.setText("", 0); // Clear the placeholder text
          }
        }

        // Insert table right after the paragraph
        XmlCursor cursor = paragraph.getCTP().newCursor();
        XWPFTable table = doc.insertNewTbl(cursor);

        // Add data rows
        for (ProjectExperience experience : projectExperience) {
          XWPFTableRow row = table.createRow();

          // Ensure all cells are created before setting text
          XWPFTableCell cell0 = row.getCell(0);
          if (cell0 == null) {
            cell0 = row.createCell();
          }
          // Clear existing paragraphs (optional but clean)
          cell0.removeParagraph(0);
          // Create a new paragraph and run
          XWPFParagraph para = cell0.addParagraph();

          createCellOfClientColumn(experience.getClient(), "Client: ", para);
          createCellOfClientColumn(experience.getProject(), "Project: ", para);
          createCellOfClientColumn(experience.getRole(), "Role: ", para);
          createCellOfClientColumn(experience.getDuration(), "Duration: ", para);
          createCellOfClientColumn(experience.getLocation(), "Location: ", para);
          createCellOfClientColumn(String.join(", ", experience.getTools()), "Tools: ", para);

          cell0.getCTTc().addNewTcPr().addNewShd().setFill("d8d4d4");

          // Ensure all cells are created before setting text
          XWPFTableCell cell1 = row.getCell(1);
          if (cell1 == null) {
            cell1 = row.createCell();
          }
          // Clear existing paragraphs (optional but clean)
          cell1.removeParagraph(0);
          // Create a new paragraph and run
          para = cell1.addParagraph();

          createDescriptionCell(experience.getDescription(), "Description: ", para);
          createResponsibilitiesCell(experience.getResponsibilities(),
              "Responsibilities: ", para, cell1);

        }
        if (table.getNumberOfRows() > 0) {
          table.removeRow(0);
        }
        break;
      }
    }
  }

  private void createCellOfClientColumn(String value, String label, XWPFParagraph para) {
    XWPFRun labelRun = para.createRun();
    labelRun.setBold(true);
    labelRun.setText(label);
    XWPFRun valueRun = para.createRun();
    valueRun.setBold(false);
    valueRun.setText(value);
    valueRun.addBreak();
  }

  private void createDescriptionCell(String value, String label, XWPFParagraph para) {
    XWPFRun labelRun = para.createRun();
    labelRun.setBold(true);
    labelRun.setText(label);
    labelRun.addBreak();

    XWPFRun valueRun = para.createRun();
    valueRun.setBold(false);
    valueRun.setText(value);
    valueRun.addBreak();
    valueRun.addBreak();
  }

  private void createResponsibilitiesCell(List<String> values, String label, XWPFParagraph para, XWPFTableCell cell) {
    // First paragraph for the label
    XWPFParagraph labelPara = cell.getParagraphs().get(0);
    XWPFRun labelRun = labelPara.createRun();
    labelRun.setBold(true);
    labelRun.setText(label);

    // Bullets — each in its own paragraph inside the cell
    for (String value : values) {
      XWPFParagraph bulletPara = cell.addParagraph(); // adds a new paragraph in the same cell
      bulletPara.setIndentationLeft(300); // optional, for visual alignment
      XWPFRun bulletRun = bulletPara.createRun();
      bulletRun.setText("\u2022 " + value);
    }
  }

  private void processCreditTable(XWPFDocument doc, String placeholder, List<Credit> credits) {
    List<IBodyElement> bodyElements = new ArrayList<>(doc.getBodyElements());

    for (int i = 0; i < bodyElements.size(); i++) {
      IBodyElement element = bodyElements.get(i);

      if (element instanceof XWPFParagraph) {
        XWPFParagraph paragraph = (XWPFParagraph) element;
        String text = paragraph.getText();

        if (text != null && text.contains(placeholder)) {
          int pos = doc.getPosOfParagraph(paragraph);
          doc.removeBodyElement(pos);

          // Create the table and insert at position
          XmlCursor cursor = doc.getParagraphArray(pos).getCTP().newCursor();
          XWPFTable table = doc.insertNewTbl(cursor);

          // Add rows for each Credit object

          System.out.println("credits: " + credits); // Debug
          for (Credit credit : credits) {
            XWPFTableRow row = table.createRow();
            XWPFTableCell cell1 = row.getCell(0);

            // Set the background color of the first column to red
            cell1.getCTTc().addNewTcPr().addNewShd().setFill("d8d4d4");

            // Make the text in the first column bold
            XWPFParagraph paragraph1 = cell1.getParagraphArray(0);
            XWPFRun run = paragraph1.createRun();
            run.setBold(true); // Set text to bold
            run.setText(credit.getCategory());

            // Add the second column with the items
            XWPFTableCell cell2 = row.addNewTableCell();
            cell2.setText(String.join(", ", credit.getItems()));

          }
          // Remove the first row after the table is populated
          table.removeRow(0);
          break; // done after first match
        }
      }
    }
  }

  private void processHeaders(List<XWPFHeader> headerList, String name, String position) {
    // Replace text in headers
    for (XWPFHeader header : headerList) {
      for (XWPFTable table : header.getTables()) {
        for (XWPFTableRow row : table.getRows()) {
          List<XWPFTableCell> cells = row.getTableCells();
          if (cells.size() >= 2) {
            replaceTextInCell(cells.get(0), "NAME", name);
            replaceTextInCell(cells.get(1), "POSITION", position);
          }
        }
      }
    }
  }

  private void processSummary(XWPFDocument document, String placeholder, String summary) {
    // Replace in body paragraphs
    for (XWPFParagraph paragraph : document.getParagraphs()) {
      replaceInParagraph(paragraph, placeholder, summary);
    }

    // Replace in body tables
    for (XWPFTable table : document.getTables()) {
      for (XWPFTableRow row : table.getRows()) {
        for (XWPFTableCell cell : row.getTableCells()) {
          for (XWPFParagraph paragraph : cell.getParagraphs()) {
            replaceInParagraph(paragraph, placeholder, summary);
          }
        }
      }
    }
  }

  private void processBulletList(XWPFDocument doc, String placeholder, List<String> bulletPoints) {
    List<XWPFParagraph> paragraphs = doc.getParagraphs();

    for (int i = 0; i < paragraphs.size(); i++) {
      XWPFParagraph para = paragraphs.get(i);
      String text = para.getText();

      if (text != null && text.contains(placeholder)) {
        int pos = doc.getPosOfParagraph(para);

        // Remove the placeholder
        doc.removeBodyElement(pos);

        // Insert bullet paragraphs at the same position
        for (int j = 0; j < bulletPoints.size(); j++) {
          XWPFParagraph newPara = doc.insertNewParagraph(
              doc.getParagraphArray(Math.min(pos + j, doc.getParagraphs().size() - 1)).getCTP().newCursor());
          newPara.setStyle("ListBullet");

          XWPFRun run = newPara.createRun();
          run.setText("\u2022 " + bulletPoints.get(j));
        }

        break;
      }
    }
  }

  private void replaceInParagraph(XWPFParagraph paragraph, String placeholder, String summary) {
    System.out.println("replaceInParagraph called: " + paragraph.getText()); // Debug
    for (XWPFRun run : paragraph.getRuns()) {
      String text = run.getText(0);
      if (text != null) {
        if (text.contains(placeholder)) {
          text = text.replace(placeholder, summary);
          System.out.println("Replacing text in body: " + text);
        }
        run.setText(text, 0);
      }
    }
  }

  private void replaceTextInCell(XWPFTableCell cell, String placeholder, String value) {
    for (XWPFParagraph paragraph : cell.getParagraphs()) {
      for (XWPFRun run : paragraph.getRuns()) {
        String text = run.getText(0);
        if (text != null && text.contains(placeholder)) {
          System.out.println("Replacing text in headers: " + text.replace(placeholder, value)); // Debug
          run.setText(text.replace(placeholder, value), 0);
        }
      }
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
      return new ObjectMapper().readValue(json, SkillResponse.class);

    } catch (Exception e) {
      throw new RuntimeException("Failed to extract skills using Gemini", e);
    }
  }
}
