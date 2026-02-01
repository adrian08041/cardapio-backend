package com.cardapiopro.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        private static final String SECURITY_SCHEME_NAME = "bearerAuth";

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Cardápio Pro API")
                                                .description("""
                                                                ## Backend RESTful para plataforma de Food Service

                                                                ### Funcionalidades:
                                                                - 🍔 **Catálogo**: Categorias, Produtos e Addons
                                                                - 🛒 **Pedidos**: Criação, acompanhamento e histórico
                                                                - 🎫 **Cupons**: Descontos percentuais e fixos
                                                                - ⭐ **Fidelidade**: Programa de pontos e tiers
                                                                - 🔐 **Autenticação**: JWT com refresh token
                                                                - ⚙️ **Configurações**: Horários, delivery e PIX

                                                                ### Autenticação:
                                                                Use o endpoint `/api/v1/auth/login` para obter o token JWT.
                                                                Depois, clique em "Authorize" e insira: `Bearer {seu_token}`
                                                                """)
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Cardápio Pro")
                                                                .email("suporte@cardapiopro.com")
                                                                .url("https://cardapiopro.com"))
                                                .license(new License()
                                                                .name("MIT License")
                                                                .url("https://opensource.org/licenses/MIT")))
                                .servers(List.of(
                                                new Server()
                                                                .url("http://localhost:8080")
                                                                .description("Desenvolvimento"),
                                                new Server()
                                                                .url("https://api.cardapiopro.com")
                                                                .description("Produção")))
                                .tags(List.of(
                                                new Tag().name("Autenticação").description(
                                                                "Endpoints de login, registro e refresh token"),
                                                new Tag().name("Categorias")
                                                                .description("Gerenciamento de categorias do cardápio"),
                                                new Tag().name("Produtos").description("Gerenciamento de produtos"),
                                                new Tag().name("Categorias de Addons")
                                                                .description("Grupos de complementos"),
                                                new Tag().name("Addons").description("Complementos de produtos"),
                                                new Tag().name("Pedidos")
                                                                .description("Criação e gerenciamento de pedidos"),
                                                new Tag().name("Cupons")
                                                                .description("Gerenciamento de cupons de desconto"),
                                                new Tag().name("Fidelidade")
                                                                .description("Programa de pontos e recompensas"),
                                                new Tag().name("Configurações")
                                                                .description("Configurações gerais da loja"),
                                                new Tag().name("Clientes").description("Gerenciamento de clientes")))
                                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                                .components(new Components()
                                                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                                                new SecurityScheme()
                                                                                .name(SECURITY_SCHEME_NAME)
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Insira o token JWT obtido no login")));
        }
}
