package com.example.zzserver.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.example.zzserver.member.repository.jpa",
                "com.example.zzserver.accommodation.repository"
        },
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASPECTJ,
                pattern = "com.example.zzserver.member.repository.redis.*"
        )
)
public class JpaConfig {
}
