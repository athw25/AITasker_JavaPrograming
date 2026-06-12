package com.aitasker.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final ObjectProvider<UserDetailsService> userDetailsServiceProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("\n========== JWT FILTER ==========");
        System.out.println("URI: " + request.getRequestURI());

        UserDetailsService userDetailsService = userDetailsServiceProvider.getIfAvailable();

        if (userDetailsService == null) {
            System.out.println("UserDetailsService NOT FOUND");
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        System.out.println("Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            System.out.println("No Bearer Token");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(BEARER_PREFIX.length());

            System.out.println("Token: " + token);

            String username = jwtService.extractUsername(token);

            System.out.println("Username from token: " + username);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                System.out.println("UserDetails username: "
                        + userDetails.getUsername());

                System.out.println("Authorities: "
                        + userDetails.getAuthorities());

                boolean valid =
                        jwtService.isTokenValid(token, userDetails);

                System.out.println("Token valid: " + valid);

                if (valid) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authentication);

                    System.out.println("Authentication SET SUCCESS");
                } else {
                    System.out.println("Token INVALID");
                }
            } else {
                System.out.println(
                        "Authentication already exists OR username null");
            }

        } catch (JwtException | IllegalArgumentException ex) {

            System.out.println("JWT Exception:");
            ex.printStackTrace();

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);

        System.out.println(
                "Authentication after filter: "
                        + SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        System.out.println("========== END JWT FILTER ==========\n");
    }
}