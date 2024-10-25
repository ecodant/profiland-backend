package co.profiland.co.components;

import lombok.Data;

@Data
public class LoginRequest {
    private String password;
    private String email;
}
