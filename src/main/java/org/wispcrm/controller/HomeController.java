package org.wispcrm.controller;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.wispcrm.interfaceService.InterfaceClienteService;
import org.wispcrm.interfaces.InterfaceClientes;
import org.wispcrm.interfaces.InterfaceFacturas;
import org.wispcrm.interfaces.InterfacePagos;
import org.wispcrm.modelo.Cliente;

@Controller
public class HomeController {
	@Autowired
	InterfaceClienteService ClienteDao;

	@Autowired
	private InterfaceFacturas facturaD;
	
	@Autowired
	InterfaceClientes clientedao1;
	
	@Autowired
	InterfacePagos pagosDao;
	
	@GetMapping("/")
	public String home(Model modelo) {
	   List<Cliente> cliente=ClienteDao.findAll();

       modelo.addAttribute("numeroclientes",cliente.size());
	   modelo.addAttribute("numerofacturas",facturaD.findFacturaByEstado(true).size());
	   modelo.addAttribute("cantidad",FormatearMoneda(facturaD.Pendientes()));
	   modelo.addAttribute("pagadas",FormatearMoneda(facturaD.Pagadas()));
	   return "home";
	}

	private Object FormatearMoneda(Long Numero){
		Locale locale = new Locale("es", "CO");
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
		return currencyFormatter.format(Numero.doubleValue());
	}
}
