package com.example.foot.config;


import com.siot.IamportRestClient.IamportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    String apiKey = "1721518235770524";
    String secretKey = "Xf95NEo1wnIjBYd4i14EveVMzduXlBMKoVoNpNLmKco69slP7XutJmaqyyfg8zKGyqTckWU4Age1c6bw";

    @Bean
    public IamportClient iamportClient(){
        return new IamportClient(apiKey,secretKey);
    }
}
