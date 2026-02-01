package com.cardapiopro.repository;

import com.cardapiopro.entity.User;
import com.cardapiopro.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save and find user by email")
    void findByEmail() {
        // Arrange
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .password("encoded_password")
                .role(UserRole.CUSTOMER)
                .active(true)
                .build();

        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test User");
        assertThat(found.get().getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("Should return empty for non-existent email")
    void findByEmail_NotFound() {
        // Act
        Optional<User> found = userRepository.findByEmail("notfound@example.com");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check email existence")
    void existsByEmail() {
        // Arrange
        userRepository.save(User.builder()
                .name("Test User")
                .email("exists@example.com")
                .password("encoded_password")
                .role(UserRole.CUSTOMER)
                .active(true)
                .build());

        // Act & Assert
        assertThat(userRepository.existsByEmail("exists@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("notexists@example.com")).isFalse();
    }

    @Test
    @DisplayName("Should find users by role")
    void findByRole() {
        // Arrange
        userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@example.com")
                .password("encoded_password")
                .role(UserRole.ADMIN)
                .active(true)
                .build());

        userRepository.save(User.builder()
                .name("Customer User")
                .email("customer@example.com")
                .password("encoded_password")
                .role(UserRole.CUSTOMER)
                .active(true)
                .build());

        // Act
        long adminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .count();

        // Assert
        assertThat(adminCount).isEqualTo(1);
    }
}
