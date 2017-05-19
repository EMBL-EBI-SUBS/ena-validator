package uk.ac.ebi.subs.ena.security;

/*
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import uk.ac.ebi.ena.authentication.provider.EnaSessionlessAuthenticationProvider;
*/

/**
 * Created by neilg on 05/05/2017.
 */

public class ENAWebSecurityConfigurerAdapter {}
/*
@Configuration
@EnableWebSecurity
@ComponentScan("uk.ac.ebi.ena.authentication.provider")
@Profile("ena_auth")
public class ENAWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private EnaSessionlessAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll()
                .and()
                .httpBasic();
    }
}
*/