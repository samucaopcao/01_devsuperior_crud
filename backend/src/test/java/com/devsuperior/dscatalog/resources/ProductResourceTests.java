package com.devsuperior.dscatalog.resources;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.tests.FactoryProduct;

// Como iremos testar um controlador utilizamos 
// essa anotação que carrega a camada web
@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockyMvc;

	@MockBean
	// Como o controller conversa com a service precisamos simular o service
	private ProductService service;
	private ProductDTO productDTO;
	
	// Usamos PageImpl para instanciar um objeto concreto e poderemos usar nele o NEW
	private PageImpl<ProductDTO> page;
	
	// Simularemos o comportamento do service, pois testaremos o 
	// findAll que neste caso vem paginado do service com o findAllPaged
	
	@BeforeEach
	void setUp() throws Exception {
		
		productDTO = FactoryProduct.createProductDTO();
		page = new PageImpl<>(List.of(productDTO));
		
		// Quando chamarmos no service o FindAllPaged com qualquer argumento
		// irá retornar um objeto page que é do tipo productDTO
		when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
	}
	
	@Test
	// Agora sim conseguiremos testar o controller depois de simular o service
	// usaremos o mockyMvc
	public void findAllShouldReturnPage() throws Exception {
		
		// O perform é usado para simular uma requisição em seguida usamos o metodo Http 
		// que desejarmos (nesse caso o Get) e podemos concatenar com .andExpect por exemplo
		// que faz o papel do assertions , neste caso esperando que o status seja ok ou seja 200 
		mockyMvc.perform(get("/products")).andExpect(status().isOk());
		
	}
	
}
