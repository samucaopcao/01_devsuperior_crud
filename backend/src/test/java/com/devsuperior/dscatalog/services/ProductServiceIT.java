package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;


// Usamos a anotattion transactional para que o BD retorne ao seu 
// estado inicial a cada teste
@SpringBootTest
@Transactional
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
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() {
		
		// Como o metodo do service findAllPaged precisa de uma página instanciamos uma 
		// conforme abaixo
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		// Como sabemos que no nosso BD existem 25 produtos então a página
		// 0 deve retornar 10 objetos, por isso verificamos inicialmente se não está vazia
		Assertions.assertFalse(result.isEmpty());
		
		// Verifico se a página 0 é a zero mesmo
		Assertions.assertEquals(0, result.getNumber());
		
		// Verifico se na pagina 0 virão 10 objetos mesmo
		Assertions.assertEquals(10, result.getSize());
		
		// Verifico se existem 25 objetos no total mesmo
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
		
		
	}

	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
		
		// Como o metodo do service findAllPaged precisa de uma página instanciamos uma 
		// conforme abaixo
		PageRequest pageRequest = PageRequest.of(50, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		// Como sabemos que no nosso BD existem 25 produtos então a página
		// 50 deve retornar vazia
		Assertions.assertTrue(result.isEmpty());
		
	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenPageSortByName() {
		
		// Ordenaremos a página por nome por isso o pageRequest tem um terceiro
		// atributo sendo o primeiro a página, o segundo a quantidade de elementos
		// e o terceiro a ordenação por nome
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> result = service.findAllPaged(pageRequest);
		
		// Verificando se não esta vazio
		Assertions.assertFalse(result.isEmpty());
		
		// Verifico se o nome do primeiro elemento é "Macbook Pro"
		Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
		
	}

	
}
