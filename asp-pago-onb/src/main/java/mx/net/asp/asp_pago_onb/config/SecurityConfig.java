package mx.net.asp.asp_pago_onb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import mx.net.asp.asp_pago_onb.filters.PrintRequestContentFilter;
import mx.net.asp.asp_pago_onb.filters.TraceIdFilter;

/**
 * Clase para configurar la seguridad del adpatador web
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;

    private final TraceIdFilter traceIdFilter;
    private final PrintRequestContentFilter printRequestContentFilter;

    public SecurityConfig(PrintRequestContentFilter printRequestContentFilter, TraceIdFilter traceIdFilter) {
        this.traceIdFilter = traceIdFilter;
        this.printRequestContentFilter = printRequestContentFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF si no es necesario
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/doc/**",                  // Swagger UI
                                "/v3/api-docs/**",          // Documentación OpenAPI
                                "/swagger-ui/**",           // Swagger UI
                                "/swagger-resources/**",    // Recursos Swagger
                                "/webjars/**"               // Archivos estáticos de Swagger
                        ).permitAll() // Permitir acceso sin autenticación
                        .anyRequest().authenticated() // Cualquier otra ruta requiere autenticación
                ).httpBasic(customizer -> customizer.realmName("asp-pago-onb")) // Autenticación básica
                .exceptionHandling(exception -> exception.
                        authenticationEntryPoint(new AuthException()) // Manejo de excepciones personalizado
                )
                .addFilterBefore(traceIdFilter, BasicAuthenticationFilter.class)
                .addFilterBefore(printRequestContentFilter, BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder().encode(password)) // Contraseña codificada
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}