package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import com.devsuperior.dscatalog.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;


// Anotações para carregar o contexto da aplicação 
@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	// Como o controller conversa com a service precisamos simular o service
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	private ProductDTO productDTO;
	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private String username;
	private String password;
	
	// Usamos PageImpl para instanciar um objeto concreto e poderemos usar nele o
	// NEW
	private PageImpl<ProductDTO> page;

	// Simularemos o comportamento do service, pois testaremos o
	// findAll que neste caso vem paginado do service com o findAllPaged

	@BeforeEach
	void setUp() throws Exception {
		
		username = "maria@gmail.com";
		password = "123456";

		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;

		productDTO = FactoryProduct.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));

		// Quando chamarmos no service o FindAllPaged com qualquer argumento
		// irá retornar um objeto page que é do tipo productDTO
		when(service.findAllPaged(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any())).thenReturn(page);

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
		
		// Insert 
		when(service.insert(ArgumentMatchers.any())).thenReturn(productDTO);
		
	}
	//---EXERCICIO--
	
	@Test
	public void insertShouldReturnProductDTOCreated() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
		
	}
	
	@Test
	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldReturnNotFoundWhenIdNotExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions result = mockMvc
				.perform(delete("/products/{id}", nonExistingId)
						.header("Authorization", "Bearer " + accessToken)
						.accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
		
	}
	
	//---EXERCICIO--
	

	@Test
	// Agora sim conseguiremos testar o controller depois de simular o service
	// usaremos o mockMvc
	public void findAllShouldReturnPage() throws Exception {

		// O perform é usado para simular uma requisição em seguida usamos o metodo Http
		// que desejarmos (nesse caso o Get) e podemos concatenar com .andExpect por
		// exemplo
		// que faz o papel do assertions , neste caso esperando que o status seja ok ou
		// seja 200
		mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

	}

	@Test
	public void findByIdShouldReturnProductThenIdExists() throws Exception {

		// Como precisaremos de um Id usando o Mock devemos fazer como abaixo
		ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));

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
		ResultActions result = mockMvc
				.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));

		// Retornará uma excessão NotFound
		result.andExpect(status().isNotFound());
	}
	
	@Test
	// Como o Put é uma requisição que deve ter corpo mas na requisição deve ser um
	// tipo de objeto JSON, deste modo temos que converter o objeto java para JSON usando
	// o ObjectMapper
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		// Convertendo um objeto java em String
		String jsonBody = objectMapper.writeValueAsString(productDTO);
		
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
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
			
			String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
			// Convertendo um objeto java em String
			String jsonBody = objectMapper.writeValueAsString(productDTO);
			
			ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
					.header("Authorization", "Bearer " + accessToken)
					.content(jsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON));
			
			result.andExpect(status().isNotFound());
		
	}

}
