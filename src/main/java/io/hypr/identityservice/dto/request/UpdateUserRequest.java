package io.hypr.identityservice.dto.request;

import io.hypr.identityservice.entity.User;
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
public class UpdateUserRequest {
    private String password;
    @Size(min = 8, message = "Password must have a maximum of 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must have uppercase & lowercase characters, numbers and 1 special character"
    )
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;

    public static void mapToEntity(User existingUser, UpdateUserRequest request) {
        existingUser.setPassword(request.getPassword());
        existingUser.setFirstname(request.getFirstname());
        existingUser.setLastname(request.getLastname());
        existingUser.setDateOfBirth(request.getDateOfBirth());
    }
}
