package org.falcon.progressionservice.configuration;

import feign.codec.ErrorDecoder;
import org.falcon.progressionservice.exception.CustomContentServiceErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignExceptionConfig {
    @Bean
    public ErrorDecoder contentServiceErrorDecoder(){
        return new CustomContentServiceErrorDecoder();
    }
}
