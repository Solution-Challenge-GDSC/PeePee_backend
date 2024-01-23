package com.gdsc.solutionchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableJpaAuditing
@SpringBootApplication
public class SolutionChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolutionChallengeApplication.class, args);
    }

}
