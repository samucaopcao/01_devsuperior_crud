package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.repositories.ProductRepository;

//Quando vamos testar componentes isolados 
//ou seja teste de unidade em service ou component
//usamos esta anotation 
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	// Não posso utilizar @AutoWired pois não vou
	// chamar o componente real
	@InjectMocks
	private ProductService service;

	// Usaremos um @Mock para
	// simular o comportamento do repository
	// considerando que esse teste não carrega o contexto
	// caso contrario usariamos @MockBean
	@Mock
	private ProductRepository repository;

}
