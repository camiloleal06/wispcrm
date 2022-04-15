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
    private static final String WARNING = "warning";
    private static final String REDIRECT_LISTAR = "redirect:/listar";
    private static final String REDIRECT_FORM = "redirect:/form";
    private static final String TITULO = "titulo";
    private static final String CLIENTE2 = "cliente";
    private static final String VER_LISTA_CLIENTE = "cliente/listaCliente";
    private static final String VER_LISTA_CLIENTE2 = "cliente/listaClientes";
    private static final String VER_FORM_CLIENTE = "cliente/formCliente";

    @Autowired
    private InterfacePlanService planDao;

    @Autowired
    private EnviarSMS smsService;

    @Autowired
    InterfaceClienteService clienteDao;

    @Autowired
    InterfaceClientes ic;

    @Autowired
    private ClienteService service;

    @RequestMapping("/listarclientes")
    public String viewHomePage(Model model, @Param("keyword") String keyword) {
        List<Cliente> cliente = service.listAll(keyword);
        model.addAttribute(CLIENTE2, cliente);
        model.addAttribute("keyword", keyword);

        return VER_LISTA_CLIENTE2;
    }

    @GetMapping(value = "/vercliente")
    public String ver(@RequestParam(name = "id") Integer id, Map<String, Object> model, RedirectAttributes flash)
            throws Exception {

        Cliente cliente = clienteDao.findOne(id);
        if (cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en la base de datos");
            return REDIRECT_LISTAR;
        }

        model.put(CLIENTE2, cliente);
        model.put(TITULO, "Detalle cliente: " + cliente.getNombres());

        return "cliente/ver";
    }

    @GetMapping("/listar")
    public String listarClientes(Model modelo) {
        List<ClienteDTO> cliente = clienteDao.listaClientes();
        modelo.addAttribute(CLIENTE2, cliente);
        return VER_LISTA_CLIENTE;
    }

    @GetMapping("/form")
    public String crear(Model modelo) throws Exception {
        Cliente cliente = new Cliente();
        modelo.addAttribute(CLIENTE2, cliente);
        modelo.addAttribute("listaplan", planDao.findAll());
        modelo.addAttribute(TITULO, "Nuevo Cliente");

        return VER_FORM_CLIENTE;
    }

    @PostMapping(value = "/save")
    public String save(@ModelAttribute @Validated Cliente cliente, Model modelo, RedirectAttributes flash,
            BindingResult result, @RequestParam(name = "id") Integer id, SessionStatus status) {
        modelo.addAttribute(TITULO, "Nuevo Cliente");

        if (result.hasErrors()) {
            return VER_FORM_CLIENTE;
        }

        if (clienteDao.findOne(id) != null) {
            cliente.setCreateAt(new Date());
            clienteDao.save(cliente);
            status.setComplete();
            flash.addFlashAttribute("success", "Cliente actualizado correctamente").addFlashAttribute("clase",
                    "success");
            return REDIRECT_LISTAR;

        } else {
            if (ic.findFirstClienteByIdentificacion(cliente.getIdentificacion()) != null) {
                flash.addFlashAttribute("error", "Ya existe un cliente con esta Identificacion")
                        .addFlashAttribute("clase", WARNING);
                return REDIRECT_FORM;

            }

            else if (ic.findFirstClienteByTelefono(cliente.getTelefono()) != null) {
                flash.addFlashAttribute("error", "Ya existe un cliente con este Telefonos").addFlashAttribute("clase",
                        WARNING);
                return REDIRECT_FORM;
            }

            else if (ic.findFirstClienteByEmail(cliente.getEmail()) != null) {
                flash.addFlashAttribute("error", "Ya existe un cliente con este email").addFlashAttribute("clase",
                        WARNING);
                return REDIRECT_FORM;

            }

            else {
                clienteDao.save(cliente);
                status.setComplete();
                flash.addFlashAttribute("success", "Agregado correctamente").addFlashAttribute("clase", "success");
                return REDIRECT_LISTAR;
            }

        }
    }

    @RequestMapping(value = "/editar")
    public String editar(@RequestParam(name = "id") Integer id, Model modelo) {
        Cliente cliente = null;
        if (id > 0) {
            cliente = clienteDao.findOne(id);
        } else {
            return REDIRECT_LISTAR;
        }
        modelo.addAttribute(CLIENTE2, cliente);
        modelo.addAttribute("listaplan", planDao.findAll());
        modelo.addAttribute(TITULO, "Actualizar Cliente");
        return VER_FORM_CLIENTE;

    }

    @RequestMapping(value = "/smsPersonalizado/{id}")
    public String sendSmsPersonalizado(@PathVariable("id") int id, SessionStatus status, Model modelo,
            RedirectAttributes flash) {
        smsService.enviarSMS(clienteDao.findOne(id).getTelefono(),
                "SYSRED INFORMA : En esta NAVIDAD aumentamos el ancho de banda de tu conexion de internet a 10 Mb, sin costo adicional");
        flash.addFlashAttribute("info", "El mensaje ha sido enviado ");
        status.setComplete();
        return REDIRECT_LISTAR;

    }

    @RequestMapping(value = "/eliminarw")
    public String eliminar2(@RequestParam(name = "id") Integer id, Model modelo, SessionStatus status) {
        clienteDao.delete(id);
        status.setComplete();
        List<Cliente> listacliente = clienteDao.findAll();
        modelo.addAttribute(CLIENTE2, listacliente);
        return REDIRECT_LISTAR;

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable int id, SessionStatus status, Model modelo) {
        clienteDao.delete(id);
        status.setComplete();
        return REDIRECT_LISTAR;
    }

}
