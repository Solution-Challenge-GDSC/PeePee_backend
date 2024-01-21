package com.gdsc.solutionchallenge.user.service;


import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponseStatus;
import com.gdsc.solutionchallenge.global.jwt.JwtTokenUtil;
import com.gdsc.solutionchallenge.user.dto.Token;
import com.gdsc.solutionchallenge.user.dto.UserReq;
import com.gdsc.solutionchallenge.user.dto.UserRes;
import com.gdsc.solutionchallenge.user.entity.RefreshToken;
import com.gdsc.solutionchallenge.user.entity.User;
import com.gdsc.solutionchallenge.user.repository.RefreshTokenRepository;
import com.gdsc.solutionchallenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    private final BCryptPasswordEncoder encoder;

    @Transactional
    public UserRes.SignUpRes signup(UserReq.SignUpReq signUpReq){
        userRepository.findByEmail(signUpReq.getEmail()).ifPresent(e -> {
            throw new ApiException(ApiResponseStatus.ALREADY_REGISTERED_USER);
        });

        User user = User.builder()
                .email(signUpReq.getEmail())
                .password(encoder.encode(signUpReq.getPassword()))
                .nickname(signUpReq.getNickname())
                .role("ROLE_USER")
                .registrationId("") // 소셜 로그인이 아닌 자체 로그인이기 때문
                .build();
        User newUser = userRepository.save(user);
        return UserRes.SignUpRes.builder().message("signup success!").build();
    }


    @Transactional
    public Token login(UserReq.LoginReq loginReq) {
        User user = userRepository.findByEmail(loginReq.getEmail()).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.NONE_EXIST_USER);
        });

        if(!encoder.matches(loginReq.getPassword(), user.getPassword())) {
            throw new ApiException(ApiResponseStatus.PASSWORD_NOT_FOUND_EXCEPTION);
        }


       Token token = jwtTokenUtil.createToken(loginReq.getEmail());

       // refreshToken을 DB에 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .keyId(token.getKey())
                .refreshToken(token.getRefreshToken())
                .build();
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByKeyId(loginReq.getEmail());

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
        return token;
    }

}
