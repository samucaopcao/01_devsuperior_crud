package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DataBaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.FactoryProduct;
import com.fasterxml.jackson.databind.ObjectMapper;

// Como iremos testar um controlador utilizamos 
// essa anotação que carrega a camada web
@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockyMvc;

	@MockBean
	// Como o controller conversa com a service precisamos simular o service
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ProductDTO productDTO;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;

	// Usamos PageImpl para instanciar um objeto concreto e poderemos usar nele o
	// NEW
	private PageImpl<ProductDTO> page;

	// Simularemos o comportamento do service, pois testaremos o
	// findAll que neste caso vem paginado do service com o findAllPaged

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;

		productDTO = FactoryProduct.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));

		// Quando chamarmos no service o FindAllPaged com qualquer argumento
		// irá retornar um objeto page que é do tipo productDTO
		when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

		// Quando no meu service eu chamar o findById passando um id existente então
		// retorne
		// um productDTO, caso não exista retorna uma excessão
		when(service.findById(existingId)).thenReturn(productDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		// Simulando o upDate
		when(service.update(eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
		when(service.update(eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

		// Simulando o Delete, lembrando que tem 3 coportamentos de acordo com o Service
		// Não faça nada quando eu chamar no service o metodo de delete para id existente
		doNothing().when(service).delete(existingId);
		// Traga a exceção ResourceNotFoundException quando tentar excluir um id que não existe
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		// Traga a exceção DataBaseException quando tentar excluir um id que depende de outro
		doThrow(DataBaseException.class).when(service).delete(dependentId);
		
	}

	@Test
	// Agora sim conseguiremos testar o controller depois de simular o service
	// usaremos o mockyMvc
	public void findAllShouldReturnPage() throws Exception {

		// O perform é usado para simular uma requisição em seguida usamos o metodo Http
		// que desejarmos (nesse caso o Get) e podemos concatenar com .andExpect por
		// exemplo
		// que faz o papel do assertions , neste caso esperando que o status seja ok ou
		// seja 200
		mockyMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	public void findByIdShouldReturnProductThenIdExists() throws Exception {

		// Como precisaremos de um Id usando o Mock devemos fazer como abaixo
		ResultActions result = mockyMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

		// O jsonPath analisa o corpo da resposta e verifica
		// se nesse corpo existe um campo ID, name
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());

	}

	@Test
	public void findByIdShouldReturnNotFoundThenIdNotExists() throws Exception {

		// Similar ao anterior mas agora com um id não existente
		ResultActions result = mockyMvc
				.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

		// Retornará uma excessão NotFound
		result.andExpect(status().isNotFound());
	}
	
	@Test
	// Como o Put é uma requisição que deve ter corpo mas na requisição deve ser um
	// tipo de objeto JSON, deste modo temos que converter o objeto java para JSON usando
	// o ObjectMapper
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		
		// Convertendo um objeto java em String
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockyMvc.perform(put("/products/{id}", existingId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}

	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
			
			// Convertendo um objeto java em String
			String jsonBody = objectMapper.writeValueAsString(productDTO);
			
			ResultActions result = mockyMvc.perform(put("/products/{id}", nonExistingId)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
			
			result.andExpect(status().isNotFound());
		
	}

}
