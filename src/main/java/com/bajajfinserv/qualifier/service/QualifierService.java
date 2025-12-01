package com.bajajfinserv.qualifier.service;

import com.bajajfinserv.qualifier.config.AppConfig;
import com.bajajfinserv.qualifier.model.SolutionRequest;
import com.bajajfinserv.qualifier.model.WebhookRequest;
import com.bajajfinserv.qualifier.model.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualifierService {

    private final WebClient webClient;
    private final AppConfig appConfig;

    /**
     * Step 1: Generate webhook and get access token
     */
    public WebhookResponse generateWebhook() {
        log.info("Generating webhook for user: {}", appConfig.getUser().getName());
        
        String url = appConfig.getApi().getBaseUrl() + appConfig.getApi().getWebhookPath();
        
        WebhookRequest request = new WebhookRequest(
                appConfig.getUser().getName(),
                appConfig.getUser().getRegno(),
                appConfig.getUser().getEmail()
        );
        
        log.debug("Sending POST request to: {}", url);
        log.debug("Request body: {}", request);
        
        try {
            WebhookResponse response = webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(request), WebhookRequest.class)
                    .retrieve()
                    .bodyToMono(WebhookResponse.class)
                    .block();
            
            log.info("Webhook generated successfully");
            log.debug("Webhook URL: {}", response.getWebhook());
            log.debug("Access Token received: {}", response.getAccessToken());
            
            return response;
        } catch (Exception e) {
            log.error("Error generating webhook", e);
            throw new RuntimeException("Failed to generate webhook", e);
        }
    }

    /**
     * Step 2: Get SQL query based on registration number
     * Registration number ending in 88 (even) -> Question 2
     */
    public String getSqlQuery() {
        // Registration number: 22BCE20488
        // Last two digits: 88 (even number)
        // Therefore, we need to solve Question 2
        
        log.info("Determining SQL query based on registration number: {}", appConfig.getUser().getRegno());
        
        String regNo = appConfig.getUser().getRegno();
        int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));
        
        log.info("Last two digits: {} - {}", lastTwoDigits, (lastTwoDigits % 2 == 0 ? "EVEN" : "ODD"));
        
        // Since the registration number ends in 88 (even), we use Question 2
        // You need to replace this with the actual SQL query from Question 2
        String sqlQuery = getSqlQueryForQuestion2();
        
        log.debug("SQL Query: {}", sqlQuery);
        return sqlQuery;
    }

    /**
     * SQL Query for Question 2
     * Problem: For every department, calculate the average age of individuals with salaries
     * exceeding ₹70,000, and produce a concatenated string containing at most 10 of their names.
     * 
     * Solution breakdown:
     * 1. Calculate age for each employee
     * 2. Find employees with any payment > 70000
     * 3. Calculate average age per department for these high earners
     * 4. Create comma-separated list of up to 10 names per department
     * 5. Order by department ID descending
     */
    private String getSqlQueryForQuestion2() {
        return "WITH EmployeeAges AS ( " +
                "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DEPARTMENT, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE " +
                "FROM EMPLOYEE e " +
                "), " +
                "HighEarners AS ( " +
                "SELECT DISTINCT ea.EMP_ID, ea.FIRST_NAME, ea.LAST_NAME, ea.DEPARTMENT, ea.AGE " +
                "FROM EmployeeAges ea " +
                "INNER JOIN PAYMENTS p ON ea.EMP_ID = p.EMP_ID " +
                "WHERE p.AMOUNT > 70000 " +
                "), " +
                "DepartmentAverage AS ( " +
                "SELECT he.DEPARTMENT, AVG(he.AGE) AS AVERAGE_AGE " +
                "FROM HighEarners he " +
                "GROUP BY he.DEPARTMENT " +
                "), " +
                "RankedEmployees AS ( " +
                "SELECT he.DEPARTMENT, he.FIRST_NAME, he.LAST_NAME, " +
                "ROW_NUMBER() OVER (PARTITION BY he.DEPARTMENT ORDER BY he.EMP_ID) AS RN " +
                "FROM HighEarners he " +
                ") " +
                "SELECT d.DEPARTMENT_NAME, " +
                "ROUND(da.AVERAGE_AGE, 2) AS AVERAGE_AGE, " +
                "GROUP_CONCAT(CONCAT(re.FIRST_NAME, ' ', re.LAST_NAME) " +
                "ORDER BY re.RN SEPARATOR ', ') AS EMPLOYEE_LIST " +
                "FROM DEPARTMENT d " +
                "INNER JOIN DepartmentAverage da ON d.DEPARTMENT_ID = da.DEPARTMENT " +
                "INNER JOIN RankedEmployees re ON d.DEPARTMENT_ID = re.DEPARTMENT AND re.RN <= 10 " +
                "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME, da.AVERAGE_AGE " +
                "ORDER BY d.DEPARTMENT_ID DESC";
    }

    /**
     * Step 3: Submit the solution to the webhook URL
     */
    public void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        log.info("Submitting solution to webhook: {}", webhookUrl);
        
        SolutionRequest request = new SolutionRequest(sqlQuery);
        
        log.debug("Solution request: {}", request);
        log.debug("Using access token: {}", accessToken);
        
        try {
            String response = webClient.post()
                    .uri(webhookUrl)
                    .header("Authorization", accessToken)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(request), SolutionRequest.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            log.info("Solution submitted successfully!");
            log.info("Response: {}", response);
        } catch (Exception e) {
            log.error("Error submitting solution", e);
            throw new RuntimeException("Failed to submit solution", e);
        }
    }

    /**
     * Main execution flow
     */
    public void executeQualifierFlow() {
        try {
            log.info("=== Starting Bajaj Finserv Qualifier Flow ===");
            
            // Step 1: Generate webhook
            WebhookResponse webhookResponse = generateWebhook();
            
            // Step 2: Get SQL query based on registration number
            String sqlQuery = getSqlQuery();
            
            // Step 3: Submit solution
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            
            log.info("=== Qualifier Flow Completed Successfully ===");
        } catch (Exception e) {
            log.error("=== Qualifier Flow Failed ===", e);
            throw e;
        }
    }
}
