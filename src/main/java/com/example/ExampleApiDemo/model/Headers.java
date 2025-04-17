package com.example.ExampleApiDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Headers {

  @JsonProperty("candidateName")
  private String candidateName;

  @JsonProperty("candidatePosition")
  private String candidatePosition;
}
