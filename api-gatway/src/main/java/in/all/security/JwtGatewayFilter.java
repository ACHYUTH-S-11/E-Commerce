package in.all.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethodValue();

        // Allow login/register without auth
        if (path.startsWith("/auth")) {
            return chain.filter(exchange);
        }

        // Public GET endpoints for products
        if (path.matches("^/products($|/\\d+$|/category/.*)") && HttpMethod.GET.matches(method)) {
            return chain.filter(exchange);
        }

        // For all other /products endpoints, require ADMIN
        if (path.startsWith("/products")) {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String token = authHeader.substring(7);
            try {
                Jws<Claims> claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(token);

                Object rolesObj = claims.getBody().get("roles");
                if (rolesObj instanceof String roles) {
                    if (!roles.contains("ROLE_ADMIN")) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                } else if (rolesObj instanceof Collection<?> rolesList) {
                    if (!rolesList.contains("ROLE_ADMIN")) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                }

                // Optionally, pass user info to downstream services
                exchange = exchange.mutate()
                        .request(builder -> builder
                                .header("X-User-Name", claims.getBody().getSubject())
                                .header("X-User-Roles", rolesObj != null ? rolesObj.toString() : "")
                        ).build();

            } catch (JwtException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // For all /cart endpoints, require USER or ADMIN
        if (path.startsWith("/cart")) {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String token = authHeader.substring(7);
            try {
                Jws<Claims> claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(token);

                Object rolesObj = claims.getBody().get("roles");
                boolean isUser = false;
                if (rolesObj instanceof String roles) {
                    if (roles.contains("ROLE_USER") || roles.contains("ROLE_ADMIN")) {
                        isUser = true;
                    }
                } else if (rolesObj instanceof Collection<?> rolesList) {
                    if (rolesList.contains("ROLE_USER") || rolesList.contains("ROLE_ADMIN")) {
                        isUser = true;
                    }
                }
                if (!isUser) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                // Optionally, pass user info to downstream services
                exchange = exchange.mutate()
                        .request(builder -> builder
                                .header("X-User-Name", claims.getBody().getSubject())
                                .header("X-User-Roles", rolesObj != null ? rolesObj.toString() : "")
                        ).build();

            } catch (JwtException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        // For all /order endpoints, require USER or ADMIN
        if (path.startsWith("/order")) {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String token = authHeader.substring(7);
            try {
                Jws<Claims> claims = Jwts.parserBuilder()
                        .setSigningKey(jwtSecret.getBytes())
                        .build()
                        .parseClaimsJws(token);

                Object rolesObj = claims.getBody().get("roles");
                boolean isUser = false;
                if (rolesObj instanceof String roles) {
                    if (roles.contains("ROLE_USER") || roles.contains("ROLE_ADMIN")) {
                        isUser = true;
                    }
                } else if (rolesObj instanceof Collection<?> rolesList) {
                    if (rolesList.contains("ROLE_USER") || rolesList.contains("ROLE_ADMIN")) {
                        isUser = true;
                    }
                }
                if (!isUser) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                // Optionally, pass user info to downstream services
                exchange = exchange.mutate()
                        .request(builder -> builder
                                .header("X-User-Name", claims.getBody().getSubject())
                                .header("X-User-Roles", rolesObj != null ? rolesObj.toString() : "")
                        ).build();

            } catch (JwtException e) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Ensure this runs early
    }
}