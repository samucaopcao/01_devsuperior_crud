package com.devsuperior.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.resources.exceptions.FieldMessage;

// Essa classe que implementará a lógica da minha validação 
// ela implementa o ConstraintValidator que é uma interface do BeansValidator
// ela é um Generics e tenho que parametrizar qual o tipo do meu anotation que criei no caso
// será o UserInsertValid e qual o tipo da classe que receberá esseanotation nesse caso UserInsertDTO
public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDTO> {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public void initialize(UserInsertValid ann) {
	}

	
	// Esse método testa se meu objeto UserInsertDTO é false ou true, 
	// se retornar false é porque houve pelo menos um erro
	@Override
	public boolean isValid(UserInsertDTO dto, ConstraintValidatorContext context) {

		List<FieldMessage> list = new ArrayList<>();

		// Coloque aqui seus testes de validação, acrescentando objetos FieldMessage à
		// lista
		// Colocarei vários ifs quantos forem necessários adicionando possíveis erros na minha lista

		
		//IF verificando se email já existe
		User user = userRepository.findByEmail(dto.getEmail());
		if(user != null) {
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
