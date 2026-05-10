package api.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank
        @Size(min = 4, max = 16, message = "The username must be between 4 and 16 characters long")
        String username,

        @NotBlank
        @Size(min = 8, max = 16, message = "The password must be between 8 and 16 characters long")
        String password
) {}
