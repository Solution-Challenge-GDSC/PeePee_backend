package com.gdsc.solutionchallenge.oauth;

import com.gdsc.solutionchallenge.global.jwt.JwtTokenUtil;
import com.gdsc.solutionchallenge.user.dto.Token;
import com.gdsc.solutionchallenge.user.entity.RefreshToken;
import com.gdsc.solutionchallenge.user.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.http.Cookie;

import static com.gdsc.solutionchallenge.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        logger.info(authentication.getPrincipal().toString());
        logger.info("Principal Type: " + authentication.getPrincipal().getClass().getName());

        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            // 사용자의 OAuth2 속성에서 이메일 정보 획득
            String email = oAuth2User.getAttribute("email");
            logger.info("User Email: " + email);

            Token token = jwtTokenUtil.createToken(email);
            logger.info(token);

            RefreshToken refreshToken = RefreshToken.builder()
                    .keyId(token.getKey())
                    .refreshToken(token.getRefreshToken())
                    .build();
            Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByKeyId(email);

            // refreshToken이 없을때
            if(tokenOptional.isEmpty()) {
                refreshTokenRepository.save(
                        RefreshToken.builder()
                                .keyId(token.getKey())
                                .refreshToken(token.getRefreshToken()).build());
            } else {
                // refreshToken이 있을때
                refreshToken.update(tokenOptional.get().getRefreshToken());
            }


            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("token", token)
                    .build().toUriString();
        }

        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}