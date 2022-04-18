package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.FactoryProduct;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	private long existingId;
	private long nonExistingId;
	private long countTotalProduct = 25L;

	@BeforeEach
	void setUp() throws Exception {

		// Declaro os atributos e objetos que serão usados no teste
		// para que n precise repetir esses atributos a cada teste usamos a
		// anotation @BeforeEach
		existingId = 1L;
		nonExistingId = 1000L;

	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

		Product product = FactoryProduct.createProduct();
		product.setId(null);

		product = repository.save(product);

		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProduct + 1, product.getId());

	}

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		// Realizo a ação desejada
		repository.deleteById(existingId);

		// Neste ponto estou fazendo uma consulta no repositorio para
		// ver se existe o elemento com o ID que pedi para deletar
		Optional<Product> result = repository.findById(existingId);

		// Confiro se o resultado foi o esperado

		// Neste ponto estou confirmando se dentro da variavel result
		// tem algum valor , não deveria ter pois o exclui na linha
		// 25
		Assertions.assertFalse(result.isPresent());

	}

	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});

	}

	@Test
	public void findByIdShouldTurnBackOptionalProductNonEmptyWhenIdExist() {

		Optional<Product> productOpt = repository.findById(existingId);

		Assertions.assertTrue(productOpt.isPresent());

	}

	@Test
	public void findByIdShouldTurnBackOptionalProductEmptyWhenIdNotExist() {

		Optional<Product> productOpt = repository.findById(nonExistingId);

		Assertions.assertFalse(productOpt.isPresent());

	}

}
