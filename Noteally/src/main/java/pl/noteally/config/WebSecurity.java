package pl.noteally.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pl.noteally.services.UserService;

import java.io.IOException;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurity {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/css/**", "/images/**","/js/**").permitAll()
                        .requestMatchers("/register","/","/login").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/catalogs/createCatalog", "/catalogs/deleteCatalog", "/catalogs/editCatalog").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/catalogs/{catalogId}/*").hasAnyAuthority("ADMIN", "USER")
                        .anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .usernameParameter("login")
                                .passwordParameter("password")
                                .loginProcessingUrl("/login")
                                .permitAll()
                                .defaultSuccessUrl("/catalogs")
                                .failureHandler(new AuthenticationFailureHandler() {
                                    @Override
                                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                        HttpSession session = request.getSession();
                                        session.setAttribute("info","Incorrect Login or Password");
                                        response.sendRedirect("/login");
                                    }
                                })
                ).sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                                .invalidSessionUrl("/login")
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)

                ).rememberMe(
                        remember -> remember
                                .rememberMeParameter("remember-me")
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login")
                                .deleteCookies("JSESSIONID")
                                .invalidateHttpSession(true)
                );
        return http.build();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(bCryptPasswordEncoder);
    }
}
