package com.example.ExampleApiDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDetail {

	@JsonProperty("key")
	private String key;

	@JsonProperty("value")
	private Object value;

}
