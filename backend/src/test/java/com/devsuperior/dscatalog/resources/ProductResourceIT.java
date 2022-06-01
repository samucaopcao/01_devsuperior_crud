package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.FactoryProduct;
import com.fasterxml.jackson.databind.ObjectMapper;

// Realizaremos o teste de integração e camada web por isso
// usaremos ambas anotation abaixo

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;
	private Long existingId;
	private Long nonExistingId;
	private Long countTotalProducts;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() throws Exception {

		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	// Testaremos se a resposta da minha requisição esta vidno ordenada
	// quando eu solicitar ordenação por nome
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {

		ResultActions result = mockMvc.perform(
				get("/products?page=0&size=12&sort=name,asc", nonExistingId).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		// Verifico se esse campo totalElements (que está no response do postman) é
		// igual a 25
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		// Verifico se esse campo content (que está no response do postman) existe pois
		// nele
		// que está a lista de elementos
		result.andExpect(jsonPath("$.content").exists());
		// Verifico se o elemento 1 da lista é o MacBook Pro e assim por diante
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}

	@Test
	// Como o Put é uma requisição que deve ter corpo mas na requisição deve ser um
	// tipo de objeto JSON, deste modo temos que converter o objeto java para JSON
	// usando
	// o ObjectMapper
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		ProductDTO productDTO = FactoryProduct.createProductDTO();
		// Convertendo um objeto java em String
		String jsonBody = objectMapper.writeValueAsString(productDTO);

		String expectedName = productDTO.getName();
		String expectedDescription = productDTO.getDescription();

		// Estou atualizando o valor aqui do produto do id existente
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		// Vejo se a resposta veio OK e com os valores esperados
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDescription));
	}

	@Test
	// Como o Put é uma requisição que deve ter corpo mas na requisição deve ser um
	// tipo de objeto JSON, deste modo temos que converter o objeto java para JSON
	// usando
	// o ObjectMapper
	public void updateShouldReturnProductDTOWhenIdDoesNotExist() throws Exception {

		ProductDTO productDTO = FactoryProduct.createProductDTO();
		// Convertendo um objeto java em String
		String jsonBody = objectMapper.writeValueAsString(productDTO);


		// Estou atualizando o valor aqui do produto do id não existente
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		// Vejo se a resposta veio como NotFound
		result.andExpect(status().isNotFound());
	}

}
