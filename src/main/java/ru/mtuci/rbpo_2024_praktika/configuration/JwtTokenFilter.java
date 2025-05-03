package ru.mtuci.rbpo_2024_praktika.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.mtuci.rbpo_2024_praktika.repository.SessionRepository;
import ru.mtuci.rbpo_2024_praktika.exception.JwtTokenException;
import ru.mtuci.rbpo_2024_praktika.exception.InvalidSessionException;
import ru.mtuci.rbpo_2024_praktika.exception.MissingTokenTypeException;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final SessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        String deviceId = request.getHeader("Device-Id");

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String tokenType = jwtTokenProvider.getTokenType(token);

            // Проверяем, что токен действительно содержит тип
            if (tokenType == null) {
                throw new MissingTokenTypeException("Token type is missing.");
            }

            // Обрабатываем только access токен
            if ("access".equals(tokenType)) {
                String username = jwtTokenProvider.getUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Проверяем существование сессии с таким токеном и deviceId
                boolean isSessionValid = sessionRepository.existsByAccessTokenAndDeviceId(token, deviceId);
                if (!isSessionValid) {
                    throw new InvalidSessionException("Invalid session.");
                }

                // Устанавливаем аутентификацию
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Отклоняем запрос, если токен не access
                throw new JwtTokenException("Invalid token type.");
            }
        }

        filterChain.doFilter(request, response);
    }

    // Метод для извлечения токена из заголовка Authorization
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Убираем "Bearer " и возвращаем сам токен
        }
        return null;
    }
}
