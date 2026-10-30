package com.catalog.service;

import com.catalog.dto.AuthResponseDTO;
import com.catalog.dto.LoginRequestDTO;
import com.catalog.dto.RegisterRequestDTO;
import com.catalog.dto.UpdateProfileRequestDTO;
import com.catalog.dto.UpdateUserRequestDTO;
import com.catalog.dto.UserResponseDTO;
import com.catalog.model.AppUser;
import com.catalog.model.Role;
import com.catalog.repository.UserRepository;
import com.catalog.security.JwtService;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
  private final UserRepository users;
  private final PasswordEncoder encoder;
  private final JwtService jwt;
  private final CatalogOwnershipService catalogOwnership;

  public UserService(
      UserRepository users,
      PasswordEncoder encoder,
      JwtService jwt,
      CatalogOwnershipService catalogOwnership) {
    this.users = users;
    this.encoder = encoder;
    this.jwt = jwt;
    this.catalogOwnership = catalogOwnership;
  }

  @Transactional
  public AuthResponseDTO register(RegisterRequestDTO request) {
    String email = normalizeEmail(request.getEmail());
    if (users.existsByEmail(email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
    }

    AppUser user =
        users.save(
            new AppUser(
                email,
                request.getFullName().trim(),
                encoder.encode(request.getPassword()),
                Role.USER));
    return authResponse(user);
  }

  public AuthResponseDTO login(LoginRequestDTO request) {
    AppUser user =
        users
            .findByEmail(normalizeEmail(request.getEmail()))
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
    if (!encoder.matches(request.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
    }

    return authResponse(user);
  }

  @Transactional(readOnly = true)
  public Page<UserResponseDTO> list(Pageable pageable) {
    return users.findAll(pageable).map(this::response);
  }

  @Transactional(readOnly = true)
  public UserResponseDTO profile(String email) {
    return response(
        users
            .findByEmail(email)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));
  }

  @Transactional
  public AuthResponseDTO updateProfile(
      String email, UpdateProfileRequestDTO request, String actor) {
    AppUser user =
        users
            .findByEmail(email)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    user.setFullName(request.getFullName().trim());
    user.setUpdatedBy(actor);
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(encoder.encode(request.getPassword()));
    }

    return authResponse(user);
  }

  @Transactional
  public UserResponseDTO update(Long id, UpdateUserRequestDTO request, String actor) {
    AppUser user =
        users
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    String email = normalizeEmail(request.getEmail());
    if (!user.getEmail().equals(email) && users.existsByEmail(email)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
    }

    catalogOwnership.transferToNewEmail(user.getEmail(), email, actor);
    user.setEmail(email);
    user.setFullName(request.getFullName().trim());
    user.setUpdatedBy(actor);
    if (request.getRole() != null) {
      user.setRole(request.getRole());
    }
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(encoder.encode(request.getPassword()));
    }

    return response(user);
  }

  private String normalizeEmail(String email) {
    return email.trim().toLowerCase(Locale.ROOT);
  }

  private AuthResponseDTO authResponse(AppUser user) {
    return new AuthResponseDTO(
        jwt.generate(user), user.getEmail(), user.getFullName(), user.getRole());
  }

  private UserResponseDTO response(AppUser user) {
    return new UserResponseDTO(
        user.getId(), user.getEmail(), user.getFullName(), user.getRole());
  }
}
