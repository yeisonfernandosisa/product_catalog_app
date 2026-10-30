package com.catalog.controller;

import com.catalog.dto.AuthDtos.UpdateUserRequest;
import com.catalog.dto.AuthDtos.UserResponse;
import com.catalog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserService service;

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping
  public Page<UserResponse> list(
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return service.list(pageable);
  }

  @PutMapping("/{id}")
  public UserResponse update(
      Authentication auth, @PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
    return service.update(id, request, auth.getName());
  }
}
