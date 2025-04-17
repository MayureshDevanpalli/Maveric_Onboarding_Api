package com.example.ExampleApiDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkExperience {
  private String role;
  private String company;
  private String from_date;
  private String to_date;
  private String location;
  private List<String> description;
}
