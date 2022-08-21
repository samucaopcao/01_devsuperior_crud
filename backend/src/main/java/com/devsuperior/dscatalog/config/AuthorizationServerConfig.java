package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

// Essa classe que representa o Authorization Server do Oauth
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	
	@Value("${jwt.duration}")
	private Integer jwtDuration;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		
	}

	// Neste método configuraremos as credencias da aplicação e outros detalhes
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
		.withClient(clientId) // Deve informar o nome da aplicação 
		.secret(passwordEncoder.encode(clientSecret)) // Qual vai ser a senha da aplicação , não do usuário.
		.scopes("read","write") // Qual acesso: Leitura ou Escrita ?
		.authorizedGrantTypes("password") // Qual tipo de login : Neste caso por password
		.accessTokenValiditySeconds(jwtDuration); // Tempo de expiração do token (Aqui 24h)
		
	}

	// Aqui sei quem vai autorizar e qual vai ser o formato do Token
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		 endpoints.authenticationManager(authenticationManager)
		 .tokenStore(tokenStore)
		 .accessTokenConverter(accessTokenConverter);
	}

	
	
}
