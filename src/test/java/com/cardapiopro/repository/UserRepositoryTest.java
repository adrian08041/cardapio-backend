package com.cardapiopro.repository;

import com.cardapiopro.entity.User;
import com.cardapiopro.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
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

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        @DisplayName("Deve encontrar usuário por email")
        void shouldFindUserByEmail() {
            User user = User.builder()
                    .name("Test User")
                    .email("test@example.com")
                    .password("encoded_password")
                    .role(UserRole.CUSTOMER)
                    .active(true)
                    .build();

            userRepository.save(user);

            Optional<User> found = userRepository.findByEmail("test@example.com");

            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("Test User");
            assertThat(found.get().getEmail()).isEqualTo("test@example.com");
            assertThat(found.get().getRole()).isEqualTo(UserRole.CUSTOMER);
        }

        @Test
        @DisplayName("Deve retornar vazio para email inexistente")
        void shouldReturnEmptyForNonExistentEmail() {
            Optional<User> found = userRepository.findByEmail("notfound@example.com");

            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("Deve buscar por email case-sensitive")
        void shouldBeCaseSensitive() {
            User user = User.builder()
                    .name("Test User")
                    .email("Test@Example.com")
                    .password("encoded_password")
                    .role(UserRole.CUSTOMER)
                    .active(true)
                    .build();

            userRepository.save(user);

            Optional<User> foundExact = userRepository.findByEmail("Test@Example.com");
            Optional<User> foundLower = userRepository.findByEmail("test@example.com");

            assertThat(foundExact).isPresent();
            assertThat(foundLower).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        @DisplayName("Deve retornar true quando email existe")
        void shouldReturnTrueWhenEmailExists() {
            userRepository.save(User.builder()
                    .name("Test User")
                    .email("exists@example.com")
                    .password("encoded_password")
                    .role(UserRole.CUSTOMER)
                    .active(true)
                    .build());

            assertThat(userRepository.existsByEmail("exists@example.com")).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando email não existe")
        void shouldReturnFalseWhenEmailDoesNotExist() {
            assertThat(userRepository.existsByEmail("notexists@example.com")).isFalse();
        }
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("Deve salvar usuário com todas as propriedades")
        void shouldSaveUserWithAllProperties() {
            User user = User.builder()
                    .name("Complete User")
                    .email("complete@example.com")
                    .password("encoded_password")
                    .role(UserRole.ADMIN)
                    .active(true)
                    .build();

            User saved = userRepository.save(user);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getName()).isEqualTo("Complete User");
            assertThat(saved.getRole()).isEqualTo(UserRole.ADMIN);
        }

        @Test
        @DisplayName("Deve gerar ID automaticamente")
        void shouldGenerateIdAutomatically() {
            User user = User.builder()
                    .name("Test User")
                    .email("autoid@example.com")
                    .password("encoded_password")
                    .role(UserRole.CUSTOMER)
                    .active(true)
                    .build();

            assertThat(user.getId()).isNull();

            User saved = userRepository.save(user);

            assertThat(saved.getId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("Deve retornar todos os usuários")
        void shouldReturnAllUsers() {
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

            List<User> users = userRepository.findAll();

            assertThat(users).hasSize(2);
        }

        @Test
        @DisplayName("Deve filtrar por role")
        void shouldFilterByRole() {
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

            long adminCount = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == UserRole.ADMIN)
                    .count();

            assertThat(adminCount).isEqualTo(1);
        }
    }
}
