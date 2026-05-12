package com.franchise.management;

import com.franchise.management.infrastructure.config.AppCorsProperties;
import com.franchise.management.infrastructure.security.AppSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppCorsProperties.class, AppSecurityProperties.class})
public class FranchiseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(FranchiseManagementApplication.class, args);
    }
}
