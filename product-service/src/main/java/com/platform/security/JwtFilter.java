// package com.platform.security;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.JwtException;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors;

// @Component
// public class JwtFilter extends OncePerRequestFilter {

//     @Value("${jwt.secret}")
//     private String jwtSecret;

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
//         String header = request.getHeader("Authorization");
//         if (header != null && header.startsWith("Bearer ")) {
//             String token = header.substring(7);
//             try {
//                 Claims claims = Jwts.parserBuilder()
//                         .setSigningKey(jwtSecret.getBytes())
//                         .build()
//                         .parseClaimsJws(token)
//                         .getBody();

//                 String username = claims.getSubject();
//                 String roles = (String) claims.get("roles");
//                 List<SimpleGrantedAuthority> authorities = Arrays.stream(roles.split(","))
//                         .map(SimpleGrantedAuthority::new)
//                         .collect(Collectors.toList());

//                 UsernamePasswordAuthenticationToken auth =
//                         new UsernamePasswordAuthenticationToken(username, null, authorities);
//                 SecurityContextHolder.getContext().setAuthentication(auth);

//             } catch (JwtException e) {
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                 return;
//             }
//         }
//         filterChain.doFilter(request, response);
//     }
// }
