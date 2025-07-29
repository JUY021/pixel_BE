package project.game.pixel.filter;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import project.game.pixel.entity.RefreshToken;
import project.game.pixel.entity.User;
import project.game.pixel.provider.JwtTokenProvider;
import project.game.pixel.repository.RefreshTokenRepository;
import project.game.pixel.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = parseBearerToken(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtProvider.validateToken(token)) {
                String userId = jwtProvider.getUserIdFromToken(token);
                authenticateUser(userId, request);
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtProvider.getUserIdFromToken(token);
            Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);

            if (optionalRefreshToken.isPresent()) {
                RefreshToken refreshToken = optionalRefreshToken.get();

                if (jwtProvider.validateToken(refreshToken.getToken())) {
                    String newAccessToken = jwtProvider.createAccessToken(userId);
                    response.setHeader("Authorization", "Bearer " + newAccessToken);

                    authenticateUser(userId, request);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        filterChain.doFilter(request, response);


    }

    private String parseBearerToken(HttpServletRequest request) {

        String authorization = request.getHeader("Authorization");

        boolean hasAutjorization = StringUtils.hasText(authorization);
        if (!hasAutjorization) return null;

        boolean isBearer = authorization.startsWith("Bearer");
        if (!isBearer) return null;

        String token = authorization.substring(7);
        return token;

    }

    private void authenticateUser(String userId, HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByUserId(userId);
        if (optionalUser.isEmpty()) return;

        User user = optionalUser.get();
        String role = user.getRole();
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        AbstractAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userId, null, authorities);
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(context);

    }



}
