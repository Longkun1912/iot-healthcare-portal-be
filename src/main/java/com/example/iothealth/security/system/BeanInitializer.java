package com.example.iothealth.security.system;

import com.cloudinary.Cloudinary;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BeanInitializer {
    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create("http://188.166.206.175:8080");
    }

    @Bean
    public Cloudinary cloudinaryConfig() {
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", "dokyaftrm");
        config.put("api_key", "353822867621548");
        config.put("api_secret", "zFx02Or9PKTXsZAz6VljSaWkTRQ");
        cloudinary = new Cloudinary(config);
        return cloudinary;
    }
}
