package com.gdsc.solutionchallenge.global.jwt;

import com.gdsc.solutionchallenge.user.dto.Token;
import com.gdsc.solutionchallenge.user.entity.RefreshToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    // 유효기간 7일
    private long accessTokenValidTime = 7 * 24 * 60 * 60 * 1000L;
    // 유효기간 31일
    private long refreshTokenValidTime = 31 * 24 * 60 * 60 * 1000L;

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret-key}")
    private String secretKey ;
    private Key key;

    // 객체 초기화, secretKey를 Base64로 인코딩
    @PostConstruct
    protected void init(){
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public Token createToken(String email) {
        // Claims claims = Jwts.claims().setSubject(email); // JWT payload 에 저장되는 정보단위
        Date now = new Date();
        String accessToken = getToken(email, now, accessTokenValidTime);
        String refreshToken = getToken(email, now, refreshTokenValidTime);
        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .key(email)
                .build();
    }

    private String getToken(String email, Date currentTime, long tokenValidTime){
        return Jwts.builder()
                .setSubject(email) // 정보 저장
                .setIssuedAt(currentTime) // 토큰 발행시간 정보
                .setExpiration(new Date(currentTime.getTime() + tokenValidTime)) // 만료시간
                .signWith(SignatureAlgorithm.HS256, key) // 암호화 알고리즘
                .compact();
    }

    // 인증 정보 조회
    public Authentication getAuthentication(String token){
        validationAuthorizationHeader(token);
        String available_token = extractToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmailFromToken(available_token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }


    // Request Header에서 token 값 가져오기
    public String getAccessToken(HttpServletRequest request) { return request.getHeader("Authorization");}

    private String extractToken(String authorizationHeader){
        return authorizationHeader.substring("Bearer ".length()).trim();
    }

    private void validationAuthorizationHeader(String header){
        if(header == null || !header.startsWith("Bearer ")){
            throw new IllegalArgumentException();
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(ServletRequest request, String jwtToken){
        try {
            validationAuthorizationHeader(jwtToken);
            String token = extractToken(jwtToken);
            userDetailsService.loadUserByUsername(this.getEmailFromToken(token));
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SignatureException e) {
            e.printStackTrace();
            request.setAttribute("exception", "ForbidddenException");
        } catch (MalformedJwtException e) {
                e.printStackTrace();
                request.setAttribute("exception", "MalformedJwtException");
        } catch (ExpiredJwtException e) {
                //토큰 만료시
                e.printStackTrace();
                request.setAttribute("exception", "ExpiredJwtException");
        } catch (UnsupportedJwtException e) {
                e.printStackTrace();
                request.setAttribute("exception", "UnsupportedJwtException");
        } catch (IllegalArgumentException e) {
                e.printStackTrace();
                request.setAttribute("exception", "IllegalArgumentException");
        }
            return false;
    }

    // RefreshToken 유효성 검사
    public String validateRefreshToken(RefreshToken refreshtoken){
        String refreshToken = refreshtoken.getRefreshToken();
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(refreshToken);
            return recreateAccessToken(claims.getBody().get("sub").toString(), claims.getBody().get("roles"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    // AccessToken 새로 발급
    private String recreateAccessToken(String email, Object roles){

       // Claims claims = Jwts.claims().setSubject(email);
        //claims.put("roles", roles);
        Date currentTime = new Date();
        return Jwts.builder()
                .setSubject(email) // 정보 저장
                .claim("roles", roles)
                .setIssuedAt(currentTime) // 토큰 발행시간 정보
                .setExpiration(new Date(currentTime.getTime() + accessTokenValidTime)) // 만료시간
                .signWith(SignatureAlgorithm.HS256, key) // 암호화 알고리즘
                .compact();
    }


}
