package com.gdsc.solutionchallenge.global.image;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.grpc.Context;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class GCSConfig {

    @Bean
    public Storage storage() throws IOException {
        ClassPathResource resource = new ClassPathResource("babybaby.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setProjectId("neat-planet-409813")
                .setCredentials(credentials).build();
        Storage storage = storageOptions.getService();
        return storage;
    }
}
