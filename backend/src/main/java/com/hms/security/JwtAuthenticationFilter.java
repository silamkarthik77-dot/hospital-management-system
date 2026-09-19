package com.hms.security;

import com.hms.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        System.out.println("AUTH HEADER = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);

        final String userEmail = jwtService.extractUsername(jwt);

        System.out.println("JWT EMAIL = " + userEmail);


        if (userEmail != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {


            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(userEmail);


            System.out.println("USER = " + userDetails.getUsername());
            System.out.println("AUTHORITIES = " + userDetails.getAuthorities());


            if (jwtService.isTokenValid(jwt, userDetails)) {


                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );


                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);


                System.out.println("AUTHENTICATION SUCCESS");
            }
            else {
                System.out.println("JWT INVALID");
            }
        }

        filterChain.doFilter(request, response);
    }
}