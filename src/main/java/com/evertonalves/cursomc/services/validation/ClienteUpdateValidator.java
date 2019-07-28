package com.evertonalves.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import com.evertonalves.cursomc.domain.Cliente;
import com.evertonalves.cursomc.domain.enums.TipoCliente;
import com.evertonalves.cursomc.dto.ClienteDTO;
import com.evertonalves.cursomc.repositories.ClienteRepository;
import com.evertonalves.cursomc.resources.exception.FieldMessage;
import com.evertonalves.cursomc.services.validation.utils.BR;

public class ClienteUpdateValidator implements ConstraintValidator<ClienteUpdate, ClienteDTO> {
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private HttpServletRequest request;
	
	@Override
	public void initialize(ClienteUpdate ann) {
	}

	@Override
	public boolean isValid(ClienteDTO objDto, ConstraintValidatorContext context) {
		
		//Capturando o ID para realizar a verificação de email se é ou não de um mesmo cliente para fazer alterações
		Map<String, String> map = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		Integer uriId = Integer.parseInt(map.get("id"));
		
		List<FieldMessage> list = new ArrayList<>();
		
		Cliente aux = repo.findByEmail(objDto.getEmail());
		
		//verificnado email, caso entre no if...
		if(aux != null && !aux.getId().equals(uriId)) {
			//se entrou signifca que este email é de um cliente, mas não é o mesmo cliente com id igual ao do email
			list.add(new FieldMessage("email", "Email já existente")); 
		}

		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}
