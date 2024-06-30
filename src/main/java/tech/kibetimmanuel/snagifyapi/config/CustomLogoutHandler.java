package tech.kibetimmanuel.snagifyapi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tech.kibetimmanuel.snagifyapi.entity.Token;
import tech.kibetimmanuel.snagifyapi.repository.TokenRepository;
import tech.kibetimmanuel.snagifyapi.service.JwtService;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserDetailsService userDetailsService;

    public CustomLogoutHandler(TokenRepository tokenRepository, JwtService jwtService, HandlerExceptionResolver handlerExceptionResolver, @Qualifier("userDetailsService") UserDetailsService userDetailsService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("No Bearer token found in the Authorization header.");
                response.getWriter().flush();
                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            final String token = authHeader.substring(7);
            final String email = jwtService.extractUsername(token);

            if (email != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (!jwtService.isTokenValid(token, userDetails)) {
                    try {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid or expired token.");
                        response.getWriter().flush();
                        return;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                Token storedToken = tokenRepository.findByToken(token).orElse(null);
                assert storedToken != null;
                UUID userId = storedToken.getUser().getId();
                tokenRepository.expireTokensForUser(userId);

                // Remove the HTTP-only refresh token cookie
                ResponseCookie cleanRefreshTokenCookie = jwtService.getCleanJwtRefreshTokenCookie();
                response.addHeader(HttpHeaders.SET_COOKIE, cleanRefreshTokenCookie.toString());
            } else {
                try {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid token. User not found");
                    response.getWriter().flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }

    }
}
