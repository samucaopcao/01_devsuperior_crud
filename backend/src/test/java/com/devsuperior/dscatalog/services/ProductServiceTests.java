package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.FactoryProduct;

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
	// ATENÇÃO quando criamos um @Mock devemos configurar o
	// comportamento simulado dele

	@Mock
	private ProductRepository repository;

	private long existId;
	private long nonExistingId;
	private long dependentId;
	// Esse é um tipo concreto que representa uma página
	private PageImpl<Product> page;
	Product product;

	@BeforeEach
	void setUp() throws Exception {

		// Declaro os atributos e objetos que serão usados no teste
		// para que não precise repetir esses atributos a cada teste usamos a
		// anotation @BeforeEach

		existId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		product = FactoryProduct.createProduct();
		page = new PageImpl<>(List.of(product));

		// Quando o metodo tem um RETORNO o WHEN vem primeiro
		// Neste exemplo quando chamar o findAll do Repository
		// passando um objeto qualquer usando ArgumentMatchers.any()
		// do tipo Pageable deverá retornar
		// um page que é uma lista de páginas

		Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

		// Quando chamar o repository.savepassando um objeto qualquer usando
		// ArgumentMatchers.any()
		// retorne um produto

		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		// Quando chamar o repository.findById com um id existente
		// retorne um Optional de produto, já se o id for inexistente
		// retorno um Optional vazio

		Mockito.when(repository.findById(existId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

		// Configurando o comportamento simulado do @Mock (linha 38)
		// no nosso caso ele nao retorna nada usamos o doNothing e quando
		// (when) usamos o mock( nosso repository) chamando o metodo deleteById
		// com um id existente ele não deve fazer nada

		Mockito.doNothing().when(repository).deleteById(existId);

		// Quando chamo um id não existente crio o comportamento para uma excessão

		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);

		// Nosso repositorio mostrará essa exceção quando tentar deletar um id que tem
		// outra entidade que depende dele

		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

	}

	// Como devemos testar a classe isolada nosso service
	// não poderá conversar com o repository ou seja o delete
	// não fará nada somente a chamada

	@Test
	public void deletShouldNothingWhenIdExists() {

		// Testamos o metodo delete e não deve retornar uma exceção

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existId);
		});

		// Neste ponto estamos fazendo o Assertion ou seja
		// vendo se nosso mock (repository da linha 38) esta configurado e
		// realizou a chamada no metodo proposto, posso também usar alguma
		// sobrecarga do Mockito como Mockito.times(2) nesse caso aqui sugere
		// que meu metodo deleteById deveria passar 1 vez
		// já o Mockito.never() quer dizer que o metodo nunca pode ser chamado

		Mockito.verify(repository, Mockito.times(1)).deleteById(existId);
	}

	@Test
	public void deletShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		// Se eu chamar um id que não existe o service chamara a exceção
		// ResourceNotFoundException

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deletShouldThrowDataBaseExceptionWhenDependentId() {

		// Se eu tentar excluir um id que outra entidade depende dele
		// o service chamara a exceção DataBaseException

		Assertions.assertThrows(DataBaseException.class, () -> {
			service.delete(dependentId);
		});

		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}

	@Test
	public void fidAllPagedShouldReturnPage() {

		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductDTO> result = service.findAllPaged(pageable);

		// Observamos que o findAll já está simulado na linha 78
		// deste modo o meu objeto mockado vai retornar uma página mesmo
		// não sendo assim nula

		Assertions.assertNotNull(result);

		// E esse verify dara verdadeiro pois la no meu service
		// realmente estou chamando o findAll

		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);

	}

}
