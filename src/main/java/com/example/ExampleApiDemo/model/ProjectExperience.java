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

  @JsonProperty("client")
  private String client;

  @JsonProperty("project")
  private String project;

  @JsonProperty("role")
  private String role;

  @JsonProperty("location")
  private String location;

  @JsonProperty("duration")
  private String duration;

  @JsonProperty("tools")
  private List<String> tools;

  @JsonProperty("description")
  private String description;

  @JsonProperty("responsibilities")
  private List<String> responsibilities;
}
