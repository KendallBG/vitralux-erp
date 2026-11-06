package com.vitralux.config;

import com.vitralux.domain.Rol;
import com.vitralux.service.UsuarioService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filtroDeSeguridad(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(reglas -> reglas
                .requestMatchers("/login", "/error", "/css/**", "/favicon.ico").permitAll()
                .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/clientes/eliminar/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/ventas/*/pagos/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/ordenes", "/ordenes/**")
                    .hasAnyRole("ADMINISTRADOR", "PRODUCCION")
                .requestMatchers(HttpMethod.GET, "/ventas", "/ventas/{id}")
                    .hasAnyRole("ADMINISTRADOR", "VENDEDOR", "PRODUCCION")
                .requestMatchers("/clientes", "/clientes/**",
                                 "/productos", "/productos/**",
                                 "/cotizaciones", "/cotizaciones/**",
                                 "/ventas/**")
                    .hasAnyRole("ADMINISTRADOR", "VENDEDOR")
                .requestMatchers("/").authenticated()
                .anyRequest().denyAll()
        );

        http.formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
        );

        http.logout(salida -> salida.logoutSuccessUrl("/login?salida"));

        return http.build();
    }

    @Bean
    public ApplicationRunner crearAdministradorInicial(UsuarioService usuarioService,
                                                       Environment entorno) {
        return argumentos -> {
            String clave = entorno.getProperty("VITRALUX_ADMIN_PASSWORD");

            if (!usuarioService.hayAdministrador() && clave != null && !clave.isBlank()) {
                usuarioService.registrar(
                        "Administrador inicial",
                        "admin@vitralux.local",
                        clave,
                        Rol.ADMINISTRADOR);
            }
        };
    }
}