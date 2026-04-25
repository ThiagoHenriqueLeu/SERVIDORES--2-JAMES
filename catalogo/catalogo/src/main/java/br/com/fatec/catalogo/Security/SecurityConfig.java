package br.com.fatec.catalogo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 🔥 DESABILITA CSRF (ESSENCIAL PARA O FETCH)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/produtos").permitAll()

                        // categorias liberadas
                        .requestMatchers("/categorias/**").permitAll()

                        // 🔥 CORRIGIDO AQUI
                        .requestMatchers(
                                "/produtos/novo",
                                "/produtos/editar/*",
                                "/produtos/excluir/*"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/produtos", true)
                        .permitAll()
                )

                .logout(logout -> logout.logoutSuccessUrl("/produtos"));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails user = User.builder()
                .username("aluno")
                .password("{noop}12345")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}12345")
                .roles("ADMIN", "USER")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}