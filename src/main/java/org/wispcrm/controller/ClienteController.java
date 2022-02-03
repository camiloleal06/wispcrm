package org.wispcrm.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.wispcrm.interfaceService.InterfaceClienteService;
import org.wispcrm.interfaceService.InterfacePlanService;
import org.wispcrm.interfaces.InterfaceClientes;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.services.ClienteService;
import org.wispcrm.services.EnviarSMS;

@Controller

public class ClienteController {
	final private String VER_LISTA_CLIENTE = "cliente/listaCliente";
	final private String VER_LISTA_CLIENTE2 = "cliente/listaClientes";

	final private String VER_FORM_CLIENTE = "cliente/formCliente";
	@Autowired
	private InterfacePlanService PlanDao;

	@Autowired
	private EnviarSMS smsService;


	@Autowired
	InterfaceClienteService ClienteDao;
    
	@Autowired
	InterfaceClientes ic;
	
	@Autowired
	private ClienteService service;

	
	
	
	@RequestMapping("/listarclientes")
    public String viewHomePage(Model model, @Param("keyword") String keyword) {
        List<Cliente> cliente = service.listAll(keyword);
    	model.addAttribute("cliente", cliente);
        model.addAttribute("keyword", keyword);
         
        return VER_LISTA_CLIENTE2;
    }
     
	
    @GetMapping(value = "/vercliente")
	public String ver(@RequestParam(name = "id") Integer id, Map<String, Object> model, RedirectAttributes flash) throws Exception {

		Cliente cliente = ClienteDao.findOne(id);
		if (cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
			return "redirect:/listar";
		}
		
		model.put("cliente", cliente);
		model.put("titulo", "Detalle cliente: " + cliente.getNombres());
		///new Funciones().addlistsuspendidos(cliente.getIpaddres(),cliente.getNombres());

		return "cliente/ver";
	}

	@GetMapping("/listar")
	public String listarClientes(Model modelo) {
		List<ClienteDTO> cliente = ClienteDao.listaClientes();
		modelo.addAttribute("cliente", cliente);
		return VER_LISTA_CLIENTE;
	}

	@GetMapping("/form")
	public String crear(Model modelo) throws Exception {
		Cliente cliente = new Cliente();
		modelo.addAttribute("cliente", cliente);
		modelo.addAttribute("listaplan",PlanDao.findAll() );
		modelo.addAttribute("titulo", "Nuevo Cliente");

		return VER_FORM_CLIENTE;
	}

	@PostMapping(value = "/save")
	public String Save(@ModelAttribute @Validated Cliente cliente, 
			Model modelo,
			RedirectAttributes flash,
			BindingResult result,
			@RequestParam(name = "id") Integer id,
			SessionStatus status
			) {
		modelo.addAttribute("titulo", "Nuevo Cliente");

		if(result.hasErrors()) {
			return VER_FORM_CLIENTE;
		}
		
	if(ClienteDao.findOne(id)!=null) {
		cliente.setCreateAt(new Date());
		ClienteDao.save(cliente);
		status.setComplete();
		flash
		 .addFlashAttribute("success", "Cliente actualizado correctamente")
         .addFlashAttribute("clase", "success");
		 return "redirect:/listar";
		
	}
	else {
	if (ic.findFirstClienteByIdentificacion(cliente.getIdentificacion()) != null ) {
			  System.out.println(ic.findFirstClienteByIdentificacion(cliente.getIdentificacion()).getIdentificacion());
		        flash
		        .addFlashAttribute("error", "Ya existe un cliente con esta Identificacion")
		        .addFlashAttribute("clase", "warning");
				return "redirect:/form";


		    }
		 
		 else if (ic.findFirstClienteByTelefono(cliente.getTelefono()) != null ) {
				  System.out.println(ic.findFirstClienteByTelefono(cliente.getTelefono()).getTelefono());
			        flash
			        .addFlashAttribute("error", "Ya existe un cliente con este Telefonos")
			        .addFlashAttribute("clase", "warning");
					return "redirect:/form";
			    }
	
		 else if (ic.findFirstClienteByEmail(cliente.getEmail()) != null) {
			  System.out.println(ic.findFirstClienteByEmail(cliente.getEmail()).getEmail());
		        flash
		        .addFlashAttribute("error", "Ya existe un cliente con este email")
		        .addFlashAttribute("clase", "warning");
				return "redirect:/form";

		    }
	   
	   else {
		ClienteDao.save(cliente);
		status.setComplete();
		flash
		 .addFlashAttribute("success", "Agregado correctamente")
         .addFlashAttribute("clase", "success");
			return "redirect:/listar";
	  }

	}
	}

	@RequestMapping(value = "/editar")
	public String editar(@RequestParam(name = "id") Integer id, Model modelo) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = ClienteDao.findOne(id);
		} 
		else {
			return "redirect:/listar";
		}
		modelo.addAttribute("cliente", cliente);
		modelo.addAttribute("listaplan",PlanDao.findAll() );
		modelo.addAttribute("titulo", "Actualizar Cliente");
		return VER_FORM_CLIENTE;

	}
	
	@RequestMapping(value = "/smsPersonalizado/{id}")
	public String SendSmsPersonalizado(@PathVariable("id") int id, SessionStatus status, Model modelo, RedirectAttributes flash) {
	  smsService.enviarSMS(ClienteDao.findOne(id).getTelefono(),
			  "SYSRED INFORMA : En esta NAVIDAD aumentamos el ancho de banda de tu conexion de internet a 10 Mb, sin costo adicional");
			flash.addFlashAttribute("info", "El mensaje ha sido enviado ");
			status.setComplete();
			return "redirect:/listar";
		

	}
	
	@RequestMapping(value = "/eliminarw")
	public String eliminar2(@RequestParam(name = "id") Integer id, 
			Model modelo, 
			SessionStatus status
) {
		ClienteDao.delete(id);
		status.setComplete();
		List<Cliente> listacliente = ClienteDao.findAll();
		modelo.addAttribute("cliente", listacliente);
		return "redirect:/listar";

	}
	
	 @GetMapping("/eliminar/{id}")
	   public String eliminar(@PathVariable int id,
			   SessionStatus status, 
			   Model modelo) {
		 ClienteDao.delete(id);
			status.setComplete();
			   return "redirect:/listar";
		}
		
}
