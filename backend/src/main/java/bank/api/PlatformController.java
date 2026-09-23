package bank.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PlatformController {
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "name", "Bank24 Financial Services Platform",
                "status", "UP",
                "frontend", "http://localhost:4200",
                "health", "/actuator/health",
                "apiDocs", "/swagger-ui/index.html");
    }
}
