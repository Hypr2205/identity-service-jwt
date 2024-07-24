package io.hypr.identityservice.service;

import io.hypr.identityservice.dto.request.CreateUserRequest;
import io.hypr.identityservice.dto.request.UpdateUserRequest;
import io.hypr.identityservice.dto.response.UserResponse;
import io.hypr.identityservice.entity.Role;
import io.hypr.identityservice.entity.RoleEnum;
import io.hypr.identityservice.entity.User;
import io.hypr.identityservice.exception.AppException;
import io.hypr.identityservice.exception.ErrorCode;
import io.hypr.identityservice.mapper.UserMapper;
import io.hypr.identityservice.repository.RoleRepository;
import io.hypr.identityservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    public UserResponse createUserRequest(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        var user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository
            .findById(RoleEnum.USER.name())
            .ifPresent(roles::add);
        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userRepository
            .findAll()
            .stream()
            .map(userMapper::toUserResponse)
            .toList();
    }


    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository
                                             .findById(id)
                                             .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }


    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        var existingUser = userRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUser(existingUser, request);
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        existingUser.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(existingUser));
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String id) {
        var existingUser = userRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(existingUser);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context
            .getAuthentication()
            .getName();
        User user = userRepository
            .findByUsername(name)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(user);
    }
}
