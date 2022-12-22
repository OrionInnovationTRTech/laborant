package tr.com.orioninc.laborant.security.config;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
@Log4j2
@EnableWebSecurity(debug = true)
@CrossOrigin(origins = "http://localhost:3000")
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private UserDetailsService userDetailsService;


    private static final String ADMIN = "ADMIN";
    private static final String USER = "USER";

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setMaxAge(Duration.ofMinutes(10));
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider =
                new ActiveDirectoryLdapAuthenticationProvider("eu.ad.mycompany.local", "ldap://10.254.156.152:389");
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        return provider;
    }

    @Bean
    @CrossOrigin(origins = "http://localhost:3000")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors();
        http

                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).and()
                .authorizeRequests()
                .antMatchers("/", "/index", "/css/*", "/js/*").permitAll()
                .antMatchers(HttpMethod.POST).hasAuthority(ADMIN)
                .antMatchers(HttpMethod.PUT).hasAuthority(ADMIN)
                .antMatchers(HttpMethod.DELETE).hasAuthority(ADMIN)
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.GET).authenticated()
                // .antMatchers("/users/test").hasAuthority("ADMIN")
//                .antMatchers("/", "index", "/css/*", "/js/*").hasAnyRole(ADMIN.name(), USER.name())
//                .antMatchers("/v1/**").hasRole(USER.name())
//                .antMatchers("/v1/**").hasRole(ADMIN.name())
//                .antMatchers(HttpMethod.POST,"/users/**").permitAll()
//                .antMatchers(HttpMethod.GET, "/v1/**").hasAuthority(UserPermission.LAB_READ.getPermission())
//                .antMatchers(HttpMethod.POST, "/**").hasAuthority(UserPermission.LAB_ADD.getPermission())
//                .antMatchers(HttpMethod.PUT, "/**").hasAuthority(UserPermission.LAB_UPDATE.getPermission())
//                .antMatchers(HttpMethod.DELETE, "/**").hasAuthority(UserPermission.LAB_DELETE.getPermission())
                .anyRequest().authenticated()
                .and()
                .httpBasic();


        http.headers().frameOptions().sameOrigin();

        return http.build();
    }

    @Autowired
    public void authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth
                //.authenticationProvider(activeDirectoryLdapAuthenticationProvider())
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}




