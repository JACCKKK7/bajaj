package com.bajajfinserv.qualifier.runner;

import com.bajajfinserv.qualifier.service.QualifierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final QualifierService qualifierService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Application started - Executing qualifier flow...");
        
        try {
            qualifierService.executeQualifierFlow();
        } catch (Exception e) {
            log.error("Failed to execute qualifier flow", e);
            // Don't throw the exception to prevent application from stopping
        }
    }
}
