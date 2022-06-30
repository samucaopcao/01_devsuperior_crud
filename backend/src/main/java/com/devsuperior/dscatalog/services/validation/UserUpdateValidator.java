package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

// Essa classe que implementará a lógica da minha validação 
// ela implementa o ConstraintValidator que é uma interface do BeansValidator
// ela é um Generics e tenho que parametrizar qual o tipo do meu anotation que criei no caso
// será o UserUpdateValid e qual o tipo da classe que receberá esseanotation nesse caso UserUpdateDTO
public class UserUpdateValidator implements ConstraintValidator<UserUpdateValid, UserUpdateDTO> {

	
	// Esse objeto guarda as informações da requisição
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void initialize(UserUpdateValid ann) {
	}

	
	// Esse método testa se meu objeto UserUpdateDTO é false ou true, 
	// se retornar false é porque houve pelo menos um erro
	@Override
	public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
		
		@SuppressWarnings("unchecked")
		// Esse ponto vai pegar um dicionário com os atributos da URL
		var uriVars = (Map<String,String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		
		Long userId = Long.parseLong(uriVars.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();

		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à
		// lista
		// Colocarei vários ifs quantos forem necessários adicionando possíveis erros na minha lista

		
		//IF verificando se email já existe e vendo detalhes do id
		User user = userRepository.findByEmail(dto.getEmail());
		if(user != null && userId != user.getId()) {
			list.add(new FieldMessage("email","Email já existe"));
		}
		
		// Aqui percorro a lista de FieldMessage para inserir na lista de Beans 
		// Validation esses erros
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		
		// No final, testo se alista está vazia , isso quer dizer que 
		// nenhum dos ifs entraram e não tem erros
		return list.isEmpty();
	}
}
