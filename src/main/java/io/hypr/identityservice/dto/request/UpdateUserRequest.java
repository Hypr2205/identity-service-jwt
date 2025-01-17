package io.hypr.identityservice.dto.request;

import io.hypr.identityservice.validator.DateOfBirthConstraint;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {
    private String password;
    @Size(min = 8, message = "Password must have a maximum of 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "PASSWORD_INVALID"
    )
    private String firstname;
    private String lastname;
    @DateOfBirthConstraint(min = 15, message = "INVALID_DOB")
    private LocalDate dateOfBirth;
    private List<String> roles;
}
