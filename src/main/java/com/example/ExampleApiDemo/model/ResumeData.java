package com.example.ExampleApiDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

import java.util.List;

import lombok.Data;
import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeData {

  @JsonProperty("headers")
  private Headers headers;

  @JsonProperty("professionalSummary")
  private String professionalSummary;

  @JsonProperty("professionalExperience")
  private List<String> professionalExperience;

  @JsonProperty("awards")
  private List<String> awards;

  @JsonProperty("certifications")
  private List<String> certifications;

  @JsonProperty("education")
  private List<String> education;

  @JsonProperty("credits")
  private List<Credit> credits;

  @JsonProperty("projectExperience")
  private List<ProjectExperience> projectExperience;
}
