package com.example.ExampleApiDemo.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Education {

  @JsonAlias({"degree", "qualification", "course"})//ST
  private String degree;

  @JsonAlias({"institution", "university", "college"})//ST
  private String university;
  private String from_date;
  private String to_date;
  private List<String> courses;

  @JsonAlias({"percentage", "score", "grade"}) //ST
  private String percentage;//ST
}
