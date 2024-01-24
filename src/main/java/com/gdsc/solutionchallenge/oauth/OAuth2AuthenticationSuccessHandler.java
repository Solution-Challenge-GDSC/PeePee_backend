package com.gdsc.solutionchallenge.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponse;
import com.gdsc.solutionchallenge.global.jwt.JwtTokenUtil;
import com.gdsc.solutionchallenge.user.dto.Token;
import com.gdsc.solutionchallenge.user.entity.RefreshToken;
import com.gdsc.solutionchallenge.user.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import jakarta.servlet.http.Cookie;

import static com.gdsc.solutionchallenge.global.exception.ApiResponseStatus.BAD_REQUEST;
import static com.gdsc.solutionchallenge.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        ApiResponse<Token> apiResponse = generateApiResponse(authentication);

        // 클라이언트에게 응답을 반환 (JSON 형식)
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
        response.getWriter().flush();

    }


    private ApiResponse<Token> generateApiResponse(Authentication authentication) {
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {

            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            Token token = jwtTokenUtil.createToken(email);

            RefreshToken refreshToken = RefreshToken.builder()
                    .keyId(token.getKey())
                    .refreshToken(token.getRefreshToken())
                    .build();
            Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByKeyId(email);

            if (tokenOptional.isEmpty()) {
                refreshTokenRepository.save(
                        RefreshToken.builder()
                                .keyId(token.getKey())
                                .refreshToken(token.getRefreshToken()).build());
            } else {
                refreshToken.update(tokenOptional.get().getRefreshToken());
            }

            // ApiResponse 객체 생성
            ApiResponse<Token> apiResponse = new ApiResponse<>(true, "요청에 성공하였습니다.", 200, token);
            return apiResponse;
        }

        // 실패 응답 생성
        return new ApiResponse<>(false, "Failed", 500, null);
    }
}

