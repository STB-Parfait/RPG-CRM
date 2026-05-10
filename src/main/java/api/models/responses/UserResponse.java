package api.models.responses;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UserResponse(
        @NotBlank
        UUID id,

        @NotBlank
        String username,

        @NotBlank
        boolean verified
) {}
