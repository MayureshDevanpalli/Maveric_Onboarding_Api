package com.example.ExampleApiDemo.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectExperience {

  @JsonProperty("projectDetails")
  private List<ProjectDetail> projectDetails;

  @JsonProperty("description")
  private String description;

  @JsonProperty("responsibilities")
  private List<String> responsibilities;
}
