package org.wispcrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wispcrm.interfaceService.InterfaceClienteService;
import org.wispcrm.modelo.Cliente;

@RestController
public class getFactura {
	
	@Autowired
	InterfaceClienteService ClienteDao;

	@GetMapping("/getFactura")
	public Cliente getFact(@RequestParam(name="id") Integer id) {
		return ClienteDao.findOne(id);
	}
}
