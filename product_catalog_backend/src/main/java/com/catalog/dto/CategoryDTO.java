package com.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class CategoryDTO {
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Long id;

  @NotBlank private String name;

  public CategoryDTO() {}

  public CategoryDTO(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
