package com.example.authservice;

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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.security.Principal;
import java.util.Optional;
import java.util.stream.Stream;

@EnableResourceServer
@SpringBootApplication
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}
}

class PrincipalRestController{

	@RequestMapping("/user")
	Principal principal(Principal principal){
		return principal;
	}

}

@Configuration
@EnableAuthorizationServer
class OAuthConfiguration extends AuthorizationServerConfigurerAdapter{

	private final AuthenticationManager authenticationManager;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().withClient("daotuan").secret("password")
				.authorizedGrantTypes("password").scopes("openid");
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager);
	}
}

class AccountCLR implements CommandLineRunner{

	private final AccountRepository accountRepository;

	@Override
	public void run(String... args) throws Exception {
		Stream.of("abc,abc", "zxc,zxc", "qwe,123").map(account -> {
			account.split(",")
		}).forEach(tuple -> {
			this.accountRepository.save(new Account(tuple[0], tuple[1], true)));
		});
	}
}

@Service
class AccountUserDetailService implements UserDetailsService {

	private final AccountRepository accountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.accountRepository.findByUsername(username)
				.map(account -> {
					return new User(account.getUsername(),
							account.getPassword(),
							account.isActive(),
							account.isActive(),
							account.isActive(),
							account.isActive(),
							AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER"));
				}).orElseThrow(() -> new UsernameNotFoundException("no username " + username));
	}
}

interface AccountRepository extends JpaRepository<Account, Long>{

	Optional<Account> findByUsername(String username);
}

@Entity
class Account{

	@Id
	@GeneratedValue
	private Long id;

	private String username;
	private String password;
	private boolean active;

	public Account() {
	}

	public Account(String username, String password, boolean active) {
		this.username = username;
		this.password = password;
		this.active = active;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isActive() {
		return active;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
