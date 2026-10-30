package com.catalog.dto;

import jakarta.validation.constraints.NotBlank;

public final class CategoryDTO {
  private CategoryDTO() {}

  public static class CategoryRequestDTO {
    @NotBlank private String name;

    public CategoryRequestDTO() {}

    public CategoryRequestDTO(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class CategoryResponseDTO {
    private Long id;
    private String name;

    public CategoryResponseDTO() {}

    public CategoryResponseDTO(Long id, String name) {
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
}
