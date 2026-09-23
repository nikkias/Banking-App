package bank.api;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @GetMapping("/me")
    public UserSessionResponse me(Authentication authentication) {
        List<String> roles = authentication.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
                .map(authority -> authority.replace("ROLE_", ""))
                .toList();
        return new UserSessionResponse(authentication.getName(), roles);
    }

    public record UserSessionResponse(String username, List<String> roles) {
    }
}