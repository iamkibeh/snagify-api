package tech.kibetimmanuel.snagifyapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.kibetimmanuel.snagifyapi.dto.UserResponse;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public UserResponse mapUserToResponse(User currentUser) {
        return UserResponse.builder()
                .id(currentUser.getId())
                .name(currentUser.getName())
                .email(currentUser.getEmail())
                .roles(currentUser.getAuthorities())
                .enabled(currentUser.isEnabled())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }
}
