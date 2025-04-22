package com.example.ExampleApiDemo.model;

import lombok.Data;

import java.util.List;

@Data
public class SkillResponse {
  private List<String> resumeSkill;
  private List<String> requiredSkills;
  private List<String> matchedSkills;
}
