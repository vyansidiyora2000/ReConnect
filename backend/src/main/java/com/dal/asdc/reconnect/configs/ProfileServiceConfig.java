package com.dal.asdc.reconnect.configs;

import com.dal.asdc.reconnect.decorator.LoggingProfileServiceDecorator;
import com.dal.asdc.reconnect.decorator.ValidationProfileServiceDecorator;
import com.dal.asdc.reconnect.service.ProfileService;
import com.dal.asdc.reconnect.service.ProfileServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileServiceConfig {

    @Bean
    public ProfileService profileService(ProfileServiceImpl profileServiceImpl) {
        ProfileService validationDecorator = new ValidationProfileServiceDecorator(profileServiceImpl);
        return new LoggingProfileServiceDecorator(validationDecorator);
    }
}
