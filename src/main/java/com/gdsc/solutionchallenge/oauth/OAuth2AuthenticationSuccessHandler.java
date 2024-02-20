package com.gdsc.solutionchallenge.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import com.gdsc.solutionchallenge.global.jwt.JwtTokenUtil;
import com.gdsc.solutionchallenge.user.dto.Token;
import com.gdsc.solutionchallenge.user.entity.RefreshToken;
import com.gdsc.solutionchallenge.user.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.gdsc.solutionchallenge.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

//
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//
//        ApiResponse<Token> apiResponse = generateApiResponse(authentication);
//
//        // 클라이언트에게 응답을 반환 (JSON 형식)
//        response.setCharacterEncoding("UTF-8");
//        response.setContentType("application/json; charset=UTF-8");
//        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
//        response.getWriter().flush();
//
//    }
//
//    private ApiResponse<Token> generateApiResponse(Authentication authentication) {
//        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
//
//            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
//            String email = oAuth2User.getAttribute("email");
//
//            Token token = jwtTokenUtil.createToken(email);
//
//            RefreshToken refreshToken = RefreshToken.builder()
//                    .keyId(token.getKey())
//                    .refreshToken(token.getRefreshToken())
//                    .build();
//            Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByKeyId(email);
//
//            if (tokenOptional.isEmpty()) {
//                refreshTokenRepository.save(
//                        RefreshToken.builder()
//                                .keyId(token.getKey())
//                                .refreshToken(token.getRefreshToken()).build());
//            } else {
//                refreshToken.update(tokenOptional.get().getRefreshToken());
//            }
//
//            // ApiResponse 객체 생성
//            ApiResponse<Token> apiResponse = new ApiResponse<>(true, "요청에 성공하였습니다.", 200, token);
//            return apiResponse;
//        }
//
//        // 실패 응답 생성
//        return new ApiResponse<>(false, "Failed", 500, null);
//    }
//}
//
@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
    String targetUrl = determineTargetUrl(request, response, authentication);

    clearAuthenticationAttributes(request, response);
    response.sendRedirect(targetUrl);
}

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Token token = jwtTokenUtil.createToken(email);

        // DB에 리프레시 토큰 저장 또는 업데이트
        refreshTokenRepository.findByKeyId(email).ifPresentOrElse(
                existingToken -> existingToken.update(token.getRefreshToken()),
                () -> refreshTokenRepository.save(new RefreshToken(email, token.getRefreshToken()))
        );

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        return fromUriString(targetUrl)
                .queryParam("accessToken", token.getAccessToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getDefaultTargetUrl() {
        return "exp://172.30.1.57:8081/Main";
    }
}