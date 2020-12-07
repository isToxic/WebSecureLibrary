package is.toxic.config;

import io.vavr.control.Try;
import is.toxic.service.UserDetailsServiceImpl;
import is.toxic.settings.MappingPermissions;
import is.toxic.settings.SecureSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;
    private final DataSource dataSource;
    private final SecureSettings settings;

    @Value("${token-lifetime-seconds}")
    private Integer tokenLifeTime;

    @Value("${login-processing-url}")
    private String loginProcessingUrl;

    @Value("${login-mapping}")
    private String loginMapping;

    @Value("${login-mapping-error}")
    private String loginMappingError;

    @Value("${login-param-username}")
    private String loginParamUsername;

    @Value("${login-param-password}")
    private String loginParamPassword;

    @Value("${success-forward-mapping}")
    private String successForwardMapping;

    @Value("${success-logout-mapping}")
    private String successLogoutMapping;

    @Value("${forbidden-mapping}")
    private String forbiddenMapping;

    @Value("${logout-mapping}")
    private String logoutMapping;

    @Autowired
    public final void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<MappingPermissions> mappings = settings.getMappingPermissions();
        http.csrf().disable();


        mappings.forEach(permissions -> {
                    if (permissions.getRoles().contains("permitAll")) {
                        Try.of(http::authorizeRequests).get()
                                .antMatchers(permissions.getMapping())
                                .permitAll();
                    } else {
                        Try.of(http::authorizeRequests).get()
                                .antMatchers(permissions.getMapping())
                                .hasAnyRole(permissions.getRoles().toArray(String[]::new));
                    }
                }
        );
        http.authorizeRequests().and().exceptionHandling().accessDeniedPage(forbiddenMapping);

        http.authorizeRequests()
                .and().formLogin().loginPage(loginMapping)
                .loginProcessingUrl(loginProcessingUrl)
                .defaultSuccessUrl(successForwardMapping)
                .failureUrl(loginMappingError)
                .usernameParameter(loginParamUsername)
                .passwordParameter(loginParamPassword)
                .and().logout().logoutUrl(logoutMapping).
                logoutSuccessUrl(successLogoutMapping);

        http.authorizeRequests().and().rememberMe()
                .tokenRepository(this.persistentTokenRepository())
                .tokenValiditySeconds(tokenLifeTime);
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

}
