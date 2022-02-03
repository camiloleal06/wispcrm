package org.wispcrm.restcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wispcrm.services.ClienteServices;
import org.wispcrm.interfaces.InterfacePagos;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.modelo.PagoDTO;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/clientes")

public class ClienteRestController {

@Autowired
ClienteServices clienteservice;

@Autowired 
InterfacePagos interfacePago;

@GetMapping(value="/paginador")
public ResponseEntity<Map<String, Object>> obtenerTodos(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
	try {
          List<ClienteDTO> clientesDTO = new ArrayList<ClienteDTO>();
          Pageable paging = PageRequest.of(page, size);
          Page<ClienteDTO> pageTuts;
          pageTuts = clienteservice.lista(paging);
        
          clientesDTO = pageTuts.getContent();

          Map<String, Object> resultado = new HashMap<>();
          resultado.put("clientes", clientesDTO);
          resultado.put("currentPage", pageTuts.getNumber());
          resultado.put("totalItems", pageTuts.getTotalElements());
          resultado.put("totalPages", pageTuts.getTotalPages());
          return new ResponseEntity<>(resultado, HttpStatus.OK);
        } 
        
        catch (Exception e) {
          return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
	}



@GetMapping
public ResponseEntity<Page<ClienteDTO>> ListaClientes(
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "10") int size){
Page<ClienteDTO> clientes = clienteservice.lista(PageRequest.of(page, size));
try {
return new ResponseEntity<Page<ClienteDTO>>(clientes, HttpStatus.OK);
}
catch(Exception e) {
    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
}
}

@GetMapping(value="/pagos")
public ResponseEntity<Map<String, Object>> getAllPagos(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size
      ) {

    try {
      List<PagoDTO> pagosDTO = new ArrayList<PagoDTO>();
      Pageable paging = PageRequest.of(page, size);
      Page<PagoDTO> pageTuts;
      pageTuts = interfacePago.lista(paging);
    
      pagosDTO = pageTuts.getContent();

      Map<String, Object> resultado = new HashMap<>();
      resultado.put("pagos", pagosDTO);
      resultado.put("currentPage", pageTuts.getNumber());
      resultado.put("totalItems", pageTuts.getTotalElements());
      resultado.put("totalPages", pageTuts.getTotalPages());

      return new ResponseEntity<>(resultado, HttpStatus.OK);
    } 
    
    catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
	
}
