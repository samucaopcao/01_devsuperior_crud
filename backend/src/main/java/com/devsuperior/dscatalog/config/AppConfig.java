package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

	// A anotação Bean é como qualquer outra, por exemplo
	// @Service,@Entity mas com a diferença de ser usada em métodos
	// e esses métodos passam a ser gerenciados pelo Spring de modo
	// que possamos injetá-los em outro local com @Autowired
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
