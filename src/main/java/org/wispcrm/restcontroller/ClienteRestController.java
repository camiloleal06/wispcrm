package org.wispcrm.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wispcrm.interfaces.InterfacePagos;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.modelo.PagoDTO;
import org.wispcrm.services.ClienteServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/clientes")

public class ClienteRestController {

    @Autowired
    ClienteServices clienteservice;

    @Autowired
    InterfacePagos interfacePago;

    @GetMapping(value = "/paginador")
    public ResponseEntity<Map<String, Object>> obtenerTodos(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {

        Pageable paging = PageRequest.of(page, size);
        Page<ClienteDTO> pageTuts;
        pageTuts = clienteservice.lista(paging);
        List<ClienteDTO> clientesDTO = pageTuts.getContent();
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("clientes", clientesDTO);
        resultado.put("currentPage", pageTuts.getNumber());
        resultado.put("totalItems", pageTuts.getTotalElements());
        resultado.put("totalPages", pageTuts.getTotalPages());
        return new ResponseEntity<>(resultado, HttpStatus.OK);

    }


    @GetMapping
    public ResponseEntity<Page<ClienteDTO>> listaClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(clienteservice.lista(PageRequest.of(page, size)), HttpStatus.OK);

    }

    @GetMapping(value = "/pagos")
    public ResponseEntity<Map<String, Object>> getAllPagos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {

        Pageable paging = PageRequest.of(page, size);
        Page<PagoDTO> pageTuts;
        pageTuts = interfacePago.lista(paging);
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("pagos", pageTuts.getContent());
        resultado.put("currentPage", pageTuts.getNumber());
        resultado.put("totalItems", pageTuts.getTotalElements());
        resultado.put("totalPages", pageTuts.getTotalPages());
        return new ResponseEntity<>(resultado, HttpStatus.OK);
    }


}
