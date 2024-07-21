package io.hypr.identityservice.service;

import io.hypr.identityservice.dto.request.CreateUserRequest;
import io.hypr.identityservice.dto.request.UpdateUserRequest;
import io.hypr.identityservice.entity.User;
import io.hypr.identityservice.exception.AppException;
import io.hypr.identityservice.exception.ErrorCode;
import io.hypr.identityservice.mapper.UserMapper;
import io.hypr.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User createUserRequest(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userRepository.save(userMapper.toEntity(request));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(String id) {
        return userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User updateUser(String id, UpdateUserRequest request) {
        var existingUser = getUserById(id);
        userMapper.updateUser(existingUser, request);
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(String id) {
        var existingUser = getUserById(id);
        userRepository.delete(existingUser);
    }
}
