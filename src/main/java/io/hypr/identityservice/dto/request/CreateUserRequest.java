package io.hypr.identityservice.dto.request;

import io.hypr.identityservice.validator.DateOfBirthConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequest {
    @NotBlank(message = "USERNAME_INVALID")
    @Size(min = 6, message = "USERNAME_INVALID")
    private String username;
    @Size(min = 8, message = "PASSWORD_INVALID")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "PASSWORD_INVALID"
    )
    private String password;
    private String firstname;
    private String lastname;
    @DateOfBirthConstraint(min = 15, message = "INVALID_DOB")
    private LocalDate dateOfBirth;
}
