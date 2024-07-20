package io.hypr.identityservice.dto.request;

import io.hypr.identityservice.entity.User;
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
