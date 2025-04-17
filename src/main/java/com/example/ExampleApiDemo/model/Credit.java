package com.example.ExampleApiDemo.model;

import lombok.Data;
import java.util.List;

@Data
public class Credit {
  private String category;
  private List<String> items;
}
