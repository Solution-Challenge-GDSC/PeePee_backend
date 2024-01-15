package com.gdsc.solutionchallenge.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", unique = true, nullable = false)
    private Long refreshTokenId;

    @Column(name = "refresh_token", unique = true)
    private String refreshToken;

    @Column(name = "key_id")
    private String keyId;


    @Builder
    public RefreshToken(String refreshToken, String keyId, String userAgent) {
        this.refreshToken = refreshToken;
        this.keyId = keyId;
    }

    public void update(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}