package com.github.ogaltsov.amzscouttesttask.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Configuration
@ConfigurationProperties("quote.limit")
@Validated
@Getter
@Setter
public class QuoteLimitConfig {

    @NotNull @Positive
    private Integer requestCount;

    @NotNull @Positive
    private Integer timeInMinutes;

}
