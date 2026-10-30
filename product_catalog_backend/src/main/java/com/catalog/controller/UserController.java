package com.catalog.controller;

import com.catalog.dto.AuthDtos.*;
import com.catalog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }
    @GetMapping public Page<UserResponse> list(@PageableDefault(size = 10, sort = "fullName") Pageable pageable) {
        return service.list(pageable);
    }
    @PutMapping("/{id}") public UserResponse update(Authentication auth, @PathVariable Long id,
        @Valid @RequestBody UpdateUserRequest request) {
        return service.update(id, request, auth.getName());
    }
}
