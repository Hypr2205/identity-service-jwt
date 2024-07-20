package io.hypr.identityservice.service;

import io.hypr.identityservice.dto.request.CreateUserRequest;
import io.hypr.identityservice.dto.request.UpdateUserRequest;
import io.hypr.identityservice.entity.User;

import java.util.List;

public interface UserService {
    User createUserRequest(CreateUserRequest request);

    List<User> getAllUsers();

    User getUserById(String id);

    User updateUser(String id, UpdateUserRequest request);

    void deleteUser(String id);
}
