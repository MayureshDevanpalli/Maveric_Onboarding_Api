package com.example.ExampleApiDemo.model;

import lombok.Data;
import java.util.List;

@Data
public class ProjectExperience {
  private String client;
  private String project;
  private String role;
  private String location;
  private String duration;
  private List<String> tools;
  private String description;
  private List<String> responsibilities;
}
