package com.example.ExampleApiDemo.controller;

import com.example.ExampleApiDemo.model.ResumeData;
import com.example.ExampleApiDemo.model.SkillRequest;
import com.example.ExampleApiDemo.model.SkillResponse;
import com.example.ExampleApiDemo.service.ResumeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/resume")
@CrossOrigin(origins = "*")

public class ResumeController {

  @Autowired
  private final ResumeService resumeService;

  public ResumeController(ResumeService resumeService) {
    this.resumeService = resumeService;
  }

  @PostMapping("parse")
  public ResponseEntity<ResumeData> extract(@RequestParam("file") MultipartFile file) throws IOException {
    ResumeData data = resumeService.extractResumeData(file);
    return ResponseEntity.ok(data);
  }

  @PostMapping("download-resume")
  public ResponseEntity<byte[]> downloadResume(@RequestBody ResumeData resumeData) throws IOException {
    byte[] bytes = resumeService.downloadResume(resumeData);
    String fileName = resumeData.getHeaders().getCandidateName() + ".docx";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    headers.setContentDisposition(
        ContentDisposition.attachment().filename(fileName).build());
    headers.add("X-Filename", fileName);
    return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
  }

  @PostMapping("extract-skills")
  public ResponseEntity<SkillResponse> extractSkills(@RequestBody SkillRequest request) {
    return ResponseEntity.ok(resumeService.extractSkills(request));
  }
}
