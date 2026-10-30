package com.catalog.config;

import com.catalog.model.AppUser;
import com.catalog.model.Role;
import com.catalog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {
  @Bean
  @SuppressWarnings("unused")
  CommandLineRunner seedAdmin(
      UserRepository users,
      PasswordEncoder encoder,
      @Value("${app.seed-admin.email:admin@example.com}") String email,
      @Value("${app.seed-admin.password:Admin123!}") String password) {
    return args -> {
      if (!users.existsByEmail(email.toLowerCase()))
        users.save(
            new AppUser(
                email.toLowerCase(), "Administrador", encoder.encode(password), Role.ADMIN));
    };
  }
}
