package bank.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"memory", "test"})
class BankControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    void rejectsCustomerCreatingCustomers() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(jsonMediaType())
                        .content("{\"firstName\":\"No\",\"lastName\":\"Access\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsAnonymousAccountAccess() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "employee", roles = "EMPLOYEE")
    void employeeCanCreateCustomerForCustomerOwner() throws Exception {
        String response = mockMvc.perform(post("/api/customers")
                        .contentType(jsonMediaType())
                        .content("{\"firstName\":\"Anna\",\"lastName\":\"Kunde\",\"ownerUsername\":\"customer\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode customer = objectMapper.readTree(response);
        assertThat(customer.get("ownerUsername").asText()).isEqualTo("customer");
    }

    @Test
    @WithMockUser(username = "customer", roles = "CUSTOMER")
    void customerSeesOnlyOwnedAccounts() throws Exception {
        String customerId = createCustomer("customer");
        String otherCustomerId = createCustomer("other");
        String accountId = createAccount(customerId, "40.00");
        createAccount(otherCustomerId, "90.00");

        String response = mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode accounts = objectMapper.readTree(response);
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).get("id").asText()).isEqualTo(accountId);
    }

    private String createCustomer(String ownerUsername) throws Exception {
        String response = mockMvc.perform(post("/api/customers")
                        .with(employeeUser())
                        .contentType(jsonMediaType())
                        .content("{\"firstName\":\"Demo\",\"lastName\":\"User\",\"ownerUsername\":\"" + ownerUsername + "\"}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String createAccount(String customerId, String openingBalance) throws Exception {
        String response = mockMvc.perform(post("/api/accounts")
                        .with(employeeUser())
                        .contentType(jsonMediaType())
                        .content("{\"customerId\":\"" + customerId + "\",\"openingBalance\":" + openingBalance + "}"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private MediaType jsonMediaType() {
        return MediaType.APPLICATION_JSON;
    }

    private RequestPostProcessor employeeUser() {
        return SecurityMockMvcRequestPostProcessors.user("employee").roles("EMPLOYEE");
    }
}