package com.gms.dto.request;

import com.gms.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotNull(message = "Role is required")
    private Role role;
    
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
}
