package br.com.fatec.catalogo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http

                .authorizeHttpRequests(auth -> auth

                        // ROTAS ADMIN
                        .requestMatchers("/categorias/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/usuarios/**")
                        .hasRole("ADMIN")

                        .requestMatchers(
                                "/produtos/novo",
                                "/produtos/salvar",
                                "/produtos/editar/**",
                                "/produtos/excluir/**",
                                "/produtos/auditoria"
                        )
                        .hasRole("ADMIN")

                        // ROTAS PÚBLICAS
                        .requestMatchers(
                                "/login",
                                "/produtos",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        )
                        .permitAll()

                        .anyRequest()
                        .authenticated()
                )

                .formLogin(form -> form

                        .loginPage("/login")

                        .defaultSuccessUrl("/produtos", true)

                        .permitAll()
                )

                .logout(logout -> logout

                        .logoutSuccessUrl("/produtos")
                        .permitAll()
                );

        return http.build();
    }
}