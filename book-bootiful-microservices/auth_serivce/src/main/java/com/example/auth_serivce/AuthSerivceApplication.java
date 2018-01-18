package com.example.auth_serivce;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.security.Principal;
import java.util.Optional;
import java.util.stream.Stream;

@EnableResourceServer
@SpringBootApplication
public class AuthSerivceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthSerivceApplication.class, args);
    }
}


@RestController
class PrincipalRestController {


    @RequestMapping("/user")
    Principal principal(Principal principal) {
        return principal;
    }
}

@Configuration
@EnableAuthorizationServer
class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public OAuthConfiguration(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("acme").secret("acmesecret").authorizedGrantTypes("password").scopes("openid");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(this.authenticationManager);
    }
}

@Component
class AccountsCLR implements CommandLineRunner {
    @Override
    public void run(String... strings) throws Exception {
        Stream.of("abc,123", "dark,soul")
                .map(x -> x.split(","))
                .forEach(tuple -> this.accountRepository.save(new Account(null, tuple[0], tuple[1], true)));
        this.accountRepository.findAll().forEach(System.out::println);
    }

    private final AccountRepository accountRepository;

    @Autowired
    public AccountsCLR(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}

@Service
class AccountUserDetailService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                .map(account -> new User(
                        account.getUsername(),
                        account.getPassword(),
                        account.isActive(), account.isActive(), account.isActive(), account.isActive(),
                        AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")
                )).orElseThrow(() -> new UsernameNotFoundException("No Username:" + username + " found !!!"));
    }

    private final AccountRepository accountRepository;

    @Autowired
    public AccountUserDetailService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}

interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
class Account {

    @javax.persistence.Id
    @GeneratedValue
    private Long id;

    private String username, password;

    private boolean active;

}