package com.example.ExampleApiDemo.controller;

import com.example.ExampleApiDemo.model.ResumeData;
import com.example.ExampleApiDemo.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")

public class ResumeController {

  @Autowired
  private final ResumeService resumeService;

  public ResumeController(ResumeService resumeService) {
    this.resumeService = resumeService;
  }

  // @PostMapping(value = "/parse", consumes =
  // MediaType.MULTIPART_FORM_DATA_VALUE)
  // public ResponseEntity<ResumeData> parseResume(@RequestParam("file")
  // MultipartFile file) throws IOException {
  // ResumeData resumeData = resumeService.extractResumeData(file);
  // return ResponseEntity.ok(resumeData);
  // }

  @PostMapping("/parse")
  public ResponseEntity<ResumeData> extract(@RequestParam("file") MultipartFile file) throws IOException {
    ResumeData data = resumeService.extractResumeData(file);
    return ResponseEntity.ok(data);

  }
}
