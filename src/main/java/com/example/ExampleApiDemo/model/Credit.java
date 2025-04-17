package com.example.ExampleApiDemo.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class Credit {

  @JsonProperty("category")
  private String category;

  @JsonProperty("items")
  private List<String> items;
}
