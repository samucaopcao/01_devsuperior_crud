package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {

		// Declaro os atributos e objetos que serão usados no teste
		long existingId = 1L;

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

		long nonExistingId = 1000L;

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(nonExistingId);
		});

	}

}
