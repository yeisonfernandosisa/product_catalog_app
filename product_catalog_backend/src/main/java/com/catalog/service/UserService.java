package com.catalog.service;

import com.catalog.dto.AuthDtos.*;
import com.catalog.model.AppUser;
import com.catalog.model.Role;
import com.catalog.repository.UserRepository;
import com.catalog.security.JwtService;
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
    public UserService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users; this.encoder = encoder; this.jwt = jwt;
    }
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByEmail(request.email().toLowerCase())) throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        AppUser user = users.save(new AppUser(request.email().toLowerCase(), request.fullName().trim(),
            encoder.encode(request.password()), Role.USER));
        return authResponse(user);
    }
    public AuthResponse login(LoginRequest request) {
        AppUser user = users.findByEmail(request.email().toLowerCase())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
        if (!encoder.matches(request.password(), user.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos");
        return authResponse(user);
    }
    @Transactional(readOnly = true)
    public Page<UserResponse> list(Pageable pageable) {
        return users.findAll(pageable).map(this::response);
    }
    @Transactional(readOnly = true)
    public UserResponse profile(String email) {
        return response(users.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")));
    }
    @Transactional
    public AuthResponse updateProfile(String email, UpdateProfileRequest request, String actor) {
        AppUser user = users.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        user.setFullName(request.fullName().trim());
        user.setUpdatedBy(actor);
        if (request.password() != null && !request.password().isBlank())
            user.setPassword(encoder.encode(request.password()));
        return authResponse(user);
    }
    @Transactional
    public UserResponse update(Long id, UpdateUserRequest request, String actor) {
        AppUser user = users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        String email = request.email().toLowerCase();
        if (!user.getEmail().equals(email) && users.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo ya está registrado");
        user.setEmail(email);
        user.setFullName(request.fullName().trim());
        user.setUpdatedBy(actor);
        if (request.role() != null) user.setRole(request.role());
        if (request.password() != null && !request.password().isBlank()) user.setPassword(encoder.encode(request.password()));
        return response(user);
    }
    private AuthResponse authResponse(AppUser user) {
        return new AuthResponse(jwt.generate(user), user.getEmail(), user.getFullName(), user.getRole());
    }
    private UserResponse response(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
