package org.wispcrm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.wispcrm.daos.InterfaceFacturas;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.modelo.EditarClienteDTO;
import org.wispcrm.services.ClienteServiceImpl;
import org.wispcrm.services.EnviarSMS;
import org.wispcrm.services.PlanServiceImpl;

@Controller
public class ClienteController {
    private static final String CLASE = "clase";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String WARNING = "warning";
    private static final String REDIRECT_LISTAR = "redirect:/listar";
    private static final String REDIRECT_FORM = "redirect:/form";
    private static final String TITULO = "titulo";
    private static final String CLIENTE = "cliente";
    private static final String VER_LISTA_CLIENTE = "cliente/listaCliente";
    private static final String VER_FORM_CLIENTE = "cliente/formCliente";

    @Autowired
    private PlanServiceImpl planService;

    @Autowired
    private EnviarSMS smsService;

    @Autowired
    ClienteServiceImpl clienteService;

    @Autowired
    InterfaceFacturas daoFacturas;

    @Autowired
    PlanServiceImpl planDao;

    @GetMapping(value = "/vercliente")
    public String ver(@RequestParam(name = "id") Integer id, Map<String, Object> model) {
        Cliente cliente = clienteService.findOne(id);
        model.put(CLIENTE, cliente);
        model.put("facturas", daoFacturas.findByCliente(cliente));
        return "cliente/ver";
    }

    @GetMapping("/listar")
    public String listarClientes(Model modelo) {
        List<ClienteDTO> cliente = clienteService.listaClientes();
        modelo.addAttribute(CLIENTE, cliente);
        return VER_LISTA_CLIENTE;
    }

    @GetMapping("/form")
    public String crear(Model modelo) {
        modelo.addAttribute(CLIENTE, new Cliente());
        modelo.addAttribute("listaplan", planService.findAll());
        modelo.addAttribute(TITULO, "Nuevo Cliente");
        return VER_FORM_CLIENTE;
    }

    @PostMapping(value = "/save")
    public String save(@ModelAttribute @Validated EditarClienteDTO clienteDTO, Model modelo, RedirectAttributes flash,
            BindingResult result, @RequestParam(name = "id") Integer id, SessionStatus status) {
        modelo.addAttribute(TITULO, "Nuevo Cliente");

        if (result.hasErrors()) {
            return VER_FORM_CLIENTE;
        }

        if (clienteService.findOne(id) != null) {
            clienteService.save(clienteService.findOne(id));
            status.setComplete();
            flash.addFlashAttribute(SUCCESS, "Cliente actualizado correctamente").addFlashAttribute(CLASE, SUCCESS);
            return REDIRECT_LISTAR;

        } else {
            if (clienteService.findFirstClienteByIdentificacion(clienteDTO.getIdentificacion()) != null) {
                flash.addFlashAttribute(ERROR, "Ya existe un cliente con esta Identificacion").addFlashAttribute(CLASE,
                        WARNING);
                return REDIRECT_FORM;
            }

            else if (clienteService.findFirstClienteByTelefono(clienteDTO.getTelefono()) != null) {
                flash.addFlashAttribute(ERROR, "Ya existe un cliente con este Telefonos").addFlashAttribute(CLASE,
                        WARNING);
                return REDIRECT_FORM;
            }

            else if (clienteService.findFirstClienteByEmail(clienteDTO.getEmail()) != null) {
                flash.addFlashAttribute(ERROR, "Ya existe un cliente con este email").addFlashAttribute(CLASE, WARNING);
                return REDIRECT_FORM;
            }

            else {
                Cliente cliente = Cliente.builder().apellidos(clienteDTO.getApellidos())
                        .diaPago(clienteDTO.getDiaPago()).nombres(clienteDTO.getNombres()).email(clienteDTO.getEmail())
                        .identificacion(clienteDTO.getIdentificacion()).direccion(clienteDTO.getDireccion())
                        .planes(planDao.findOne(clienteDTO.getIdPago())).build();
                clienteService.save(cliente);
                status.setComplete();
                flash.addFlashAttribute(SUCCESS, "Agregado correctamente").addFlashAttribute(CLASE, SUCCESS);
                return REDIRECT_LISTAR;
            }

        }
    }

    @RequestMapping(value = "/editar")
    public String editar(@RequestParam(name = "id") Integer id, Model modelo) {
        /// (()) Cliente cliente = clienteService.findOne(id);
        modelo.addAttribute(CLIENTE, clienteService.editarCliente(id));
        modelo.addAttribute("listaplan", planService.findAll());
        modelo.addAttribute(TITULO, "Actualizar Cliente");
        return VER_FORM_CLIENTE;
    }

    @RequestMapping(value = "/smsPersonalizado/{id}")
    public String sendSmsPersonalizado(@PathVariable("id") int id, SessionStatus status, Model modelo,
            RedirectAttributes flash) {
        smsService.enviarSMS(clienteService.findOne(id).getTelefono(),
                "SYSRED INFORMA : En esta NAVIDAD aumentamos el ancho de banda de tu conexion de internet a 10 Mb, sin costo adicional");
        flash.addFlashAttribute("info", "El mensaje ha sido enviado ");
        status.setComplete();
        return REDIRECT_LISTAR;

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, SessionStatus status, Model modelo) {
        clienteService.delete(id);
        status.setComplete();
        return REDIRECT_LISTAR;
    }

}
