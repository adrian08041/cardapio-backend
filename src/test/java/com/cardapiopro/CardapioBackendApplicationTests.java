package com.cardapiopro;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Application Context Tests")
class CardapioBackendApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Deve carregar o contexto da aplicação")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Deve ter os beans principais registrados")
    void shouldHaveMainBeansRegistered() {
        assertThat(applicationContext.containsBean("authController")).isTrue();
        assertThat(applicationContext.containsBean("categoryController")).isTrue();
        assertThat(applicationContext.containsBean("storeSettingsController")).isTrue();
    }
}
