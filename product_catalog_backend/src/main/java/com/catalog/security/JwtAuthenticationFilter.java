package com.catalog.security;

import com.catalog.repository.UserRepository;
import com.catalog.service.TokenRevocationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository users;
  private final TokenRevocationService tokenRevocations;

  public JwtAuthenticationFilter(
      JwtService jwtService, UserRepository users, TokenRevocationService tokenRevocations) {
    this.jwtService = jwtService;
    this.users = users;
    this.tokenRevocations = tokenRevocations;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      if (jwtService.isValid(token) && !tokenRevocations.isRevoked(jwtService.tokenId(token))) {
        String email = jwtService.subject(token);
        users
            .findByEmail(email)
            .ifPresent(
                user -> {
                  var auth =
                      new UsernamePasswordAuthenticationToken(
                          email,
                          null,
                          List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                  SecurityContextHolder.getContext().setAuthentication(auth);
                });
      }
    }
    chain.doFilter(request, response);
  }
}
