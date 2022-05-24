package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
public class ProductServiceIT {

	// Testes de integração são demorados pois acessam todas as camadas
	// até mesmo o BD, não sendo mais mockados os dados , usamos o
	// @AutoWired

	@Autowired
	private ProductService service;

	@Autowired
	private ProductRepository repository;

	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;

	}

	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {

		// Vamos testar o metodo delete do service
		service.delete(existingId);

		// Como sabemos que no BD existe 25 produtos podemos fazer o assertion
		// confirmando se o total diminuiu 1, após o delete
		Assertions.assertEquals(countTotalProducts - 1, repository.count());

	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

		// Se eu chamar um id que não existe o service chamara a exceção
		// ResourceNotFoundException

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});
	}

}
