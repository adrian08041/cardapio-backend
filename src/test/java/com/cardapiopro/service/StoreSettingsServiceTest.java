package com.cardapiopro.service;

import com.cardapiopro.dto.request.UpdateStoreSettingsRequest;
import com.cardapiopro.entity.StoreSettings;
import com.cardapiopro.entity.enums.PixKeyType;
import com.cardapiopro.repository.StoreSettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreSettingsService Tests")
class StoreSettingsServiceTest {

    @Mock
    private StoreSettingsRepository repository;

    @InjectMocks
    private StoreSettingsService service;

    private StoreSettings testSettings;

    @BeforeEach
    void setUp() {
        testSettings = StoreSettings.builder()
                .id(UUID.randomUUID())
                .storeName("Minha Loja")
                .isOpen(true)
                .deliveryEnabled(true)
                .pickupEnabled(true)
                .deliveryFee(new BigDecimal("5.00"))
                .minOrderValue(new BigDecimal("20.00"))
                .deliveryTimeMin(30)
                .deliveryTimeMax(50)
                .freeDeliveryThreshold(new BigDecimal("100.00"))
                .pixKey("email@example.com")
                .pixKeyType(PixKeyType.EMAIL)
                .pixDiscountPercent(new BigDecimal("5.00"))
                .build();
    }

    @Nested
    @DisplayName("getSettings")
    class GetSettings {

        @Test
        @DisplayName("Deve retornar configurações existentes")
        void shouldReturnExistingSettings() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

            StoreSettings result = service.getSettings();

            assertThat(result).isNotNull();
            assertThat(result.getStoreName()).isEqualTo("Minha Loja");
            assertThat(result.getIsOpen()).isTrue();
            verify(repository).findFirstByOrderByUpdatedAtDesc();
        }

        @Test
        @DisplayName("Deve criar configurações padrão quando não existir")
        void shouldCreateDefaultSettingsWhenNotExists() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.empty());
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            StoreSettings result = service.getSettings();

            assertThat(result).isNotNull();
            assertThat(result.getStoreName()).isEqualTo("Minha Loja");
            assertThat(result.getIsOpen()).isTrue();
            assertThat(result.getDeliveryEnabled()).isTrue();
            verify(repository).save(any(StoreSettings.class));
        }
    }

    @Nested
    @DisplayName("updateSettings")
    class UpdateSettings {

        @Test
        @DisplayName("Deve atualizar nome da loja")
        void shouldUpdateStoreName() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UpdateStoreSettingsRequest request = new UpdateStoreSettingsRequest(
                    "Nova Loja", null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);

            StoreSettings result = service.updateSettings(request);

            assertThat(result.getStoreName()).isEqualTo("Nova Loja");
            verify(repository).save(any(StoreSettings.class));
        }

        @Test
        @DisplayName("Deve atualizar taxa de entrega")
        void shouldUpdateDeliveryFee() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UpdateStoreSettingsRequest request = new UpdateStoreSettingsRequest(
                    null, null, null, null, null, null, null, null,
                    new BigDecimal("10.00"), null, null, null, null, null, null, null, null);

            StoreSettings result = service.updateSettings(request);

            assertThat(result.getDeliveryFee()).isEqualByComparingTo(new BigDecimal("10.00"));
        }

        @Test
        @DisplayName("Deve manter valores não informados")
        void shouldKeepUnchangedValues() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UpdateStoreSettingsRequest request = new UpdateStoreSettingsRequest(
                    "Nova Loja", null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);

            StoreSettings result = service.updateSettings(request);

            assertThat(result.getDeliveryFee()).isEqualByComparingTo(new BigDecimal("5.00"));
            assertThat(result.getIsOpen()).isTrue();
        }
    }

    @Nested
    @DisplayName("toggleOpen")
    class ToggleOpen {

        @Test
        @DisplayName("Deve fechar a loja")
        void shouldCloseStore() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            StoreSettings result = service.toggleOpen(false);

            assertThat(result.getIsOpen()).isFalse();
            verify(repository).save(any(StoreSettings.class));
        }

        @Test
        @DisplayName("Deve abrir a loja")
        void shouldOpenStore() {
            testSettings.setIsOpen(false);
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            StoreSettings result = service.toggleOpen(true);

            assertThat(result.getIsOpen()).isTrue();
        }
    }

    @Nested
    @DisplayName("toggleDelivery")
    class ToggleDelivery {

        @Test
        @DisplayName("Deve desabilitar delivery")
        void shouldDisableDelivery() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            StoreSettings result = service.toggleDelivery(false);

            assertThat(result.getDeliveryEnabled()).isFalse();
        }

        @Test
        @DisplayName("Deve habilitar delivery")
        void shouldEnableDelivery() {
            testSettings.setDeliveryEnabled(false);
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));
            when(repository.save(any(StoreSettings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            StoreSettings result = service.toggleDelivery(true);

            assertThat(result.getDeliveryEnabled()).isTrue();
        }
    }

    @Nested
    @DisplayName("calculateDeliveryFee")
    class CalculateDeliveryFee {

        @Test
        @DisplayName("Deve retornar taxa de entrega normal")
        void shouldReturnNormalDeliveryFee() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

            BigDecimal fee = service.calculateDeliveryFee(new BigDecimal("50.00"));

            assertThat(fee).isEqualByComparingTo(new BigDecimal("5.00"));
        }

        @Test
        @DisplayName("Deve retornar zero acima do limite de frete grátis")
        void shouldReturnZeroAboveThreshold() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

            BigDecimal fee = service.calculateDeliveryFee(new BigDecimal("150.00"));

            assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Deve retornar zero exatamente no limite de frete grátis")
        void shouldReturnZeroAtExactThreshold() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

            BigDecimal fee = service.calculateDeliveryFee(new BigDecimal("100.00"));

            assertThat(fee).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    @Nested
    @DisplayName("calculatePixDiscount")
    class CalculatePixDiscount {

        @Test
        @DisplayName("Deve calcular desconto PIX de 5%")
        void shouldCalculate5PercentDiscount() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

            BigDecimal discount = service.calculatePixDiscount(new BigDecimal("100.00"));

            assertThat(discount).isEqualByComparingTo(new BigDecimal("5.00"));
        }

        @Test
        @DisplayName("Deve calcular desconto PIX para valores diferentes")
        void shouldCalculateDiscountForDifferentValues() {
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

            BigDecimal discount = service.calculatePixDiscount(new BigDecimal("200.00"));

            assertThat(discount).isEqualByComparingTo(new BigDecimal("10.00"));
        }

        @Test
        @DisplayName("Deve retornar zero quando desconto PIX é nulo")
        void shouldReturnZeroWhenPixDiscountIsNull() {
            testSettings.setPixDiscountPercent(null);
            when(repository.findFirstByOrderByUpdatedAtDesc()).thenReturn(Optional.of(testSettings));

            BigDecimal discount = service.calculatePixDiscount(new BigDecimal("100.00"));

            assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}
