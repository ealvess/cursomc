package com.evertonalves.cursomc.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.evertonalves.cursomc.domain.Cliente;
import com.evertonalves.cursomc.dto.ClienteDTO;
import com.evertonalves.cursomc.dto.ClienteNewDTO;
import com.evertonalves.cursomc.services.ClienteService;

@RestController
@RequestMapping(value="/clientes")
public class ClienteResource {
	
	@Autowired
	private ClienteService service;
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<Cliente> find(@PathVariable Integer id) {
		
		Cliente obj = service.buscar(id);
		
		return ResponseEntity.ok().body(obj);
	}
	
	//INSERINDO UMA NOVA CATEGORIA
		@RequestMapping(method = RequestMethod.POST)
		public ResponseEntity<Void> insert(@Valid @RequestBody ClienteNewDTO objDTO) {
			Cliente obj =  service.fromDTO(objDTO);
			
			obj = service.insert(obj);
			
			URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
					.buildAndExpand(obj.getId()).toUri();
			return ResponseEntity.created(uri).build();
		}
		
		//ATUALIZANDO UMA CATEGORIA
		@RequestMapping(value="/{id}", method=RequestMethod.PUT)
		public ResponseEntity<Void> update(@Valid @RequestBody ClienteDTO objDTO, @PathVariable Integer id){
			Cliente obj =  service.fromDTO(objDTO);
			
			obj.setId(id);
			obj = service.update(obj);
			
			return ResponseEntity.noContent().build();
		}
		
		//DELETANDO UMA NOVA CATEGORIA
		@PreAuthorize("hasAnyRole('ADMIN')")
		@RequestMapping(value="/{id}", method=RequestMethod.DELETE)
		public ResponseEntity<Void> delete(@PathVariable Integer id) {
				
			service.delete(id);
			return ResponseEntity.noContent().build();
		}
		
		//BUSCANDO TODOS CLIENTES
		@PreAuthorize("hasAnyRole('ADMIN')")
		@RequestMapping(method=RequestMethod.GET)
		public ResponseEntity<List<ClienteDTO>> findAll() {
				
			List<Cliente> list = service.findAll();
			List<ClienteDTO> listDTO = list.stream().map(obj -> new ClienteDTO(obj)).collect(Collectors.toList());
			return ResponseEntity.ok().body(listDTO);
		}
		
		@RequestMapping(value="/page", method=RequestMethod.GET)
		public ResponseEntity<Page<ClienteDTO>> findPage(
				@RequestParam(value="page", defaultValue="0") Integer page, 
				@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage, 
				@RequestParam(value="orderBy", defaultValue="nome") String orderBy, 
				@RequestParam(value="direction", defaultValue="ASC") String direction) {
				
			Page<Cliente> list = service.findPage(page, linesPerPage, orderBy, direction);
			Page<ClienteDTO> listDTO = list.map(obj -> new ClienteDTO(obj));
			return ResponseEntity.ok().body(listDTO);
		}
	

		//ENVIANDO FOTO DE PERFIL DO USUÁRIO PARA O AMAZON S3
			@RequestMapping(value="/picture" , method = RequestMethod.POST)
			public ResponseEntity<Void> uploadProfilePicture(@RequestParam(name="file") MultipartFile file) {
				URI uri = service.uploadProfilePicture(file);
				return ResponseEntity.created(uri).build();
			}
}
