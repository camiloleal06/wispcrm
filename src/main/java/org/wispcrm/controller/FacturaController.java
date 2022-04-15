package org.wispcrm.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.wispcrm.interfaceService.InterfaceClienteService;
import org.wispcrm.interfaceService.InterfacePlanService;
import org.wispcrm.interfaces.InterfaceFacturas;
import org.wispcrm.interfaces.InterfacePagos;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.Factura;
import org.wispcrm.modelo.Pago;
import org.wispcrm.modelo.PagoDTO;
import org.wispcrm.services.ClienteServices;
import org.wispcrm.services.EnviarSMS;
import org.wispcrm.services.FacturaReportService;
import org.wispcrm.services.FacturaServices;
import org.wispcrm.services.MailService;
import org.wispcrm.services.PagoService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@SessionAttributes("factura")
public class FacturaController extends Thread {

    @Autowired
    private PagoService pagosDAO;
    @Autowired
    private InterfacePagos pagosD;

    @Autowired
    private ClienteServices dataCliente;

    @Autowired
    InterfaceClienteService clienteDao;

    @Autowired
    FacturaReportService reporte;

    @Autowired
    private MailService mailService;

    @Autowired
    private FacturaServices FacturaDao;

    @Autowired
    private EnviarSMS smsService;

    @Autowired
    private InterfaceFacturas facturaD;

    @Autowired
    private InterfacePlanService PlanDao;

    private static final String VER_FORMULARIO_FACTURA = "factura/formFactura";
    private static final String LISTAR_CLIENTE = "cliente/listaCliente";
    private static final String LISTAR_FACTURA = "factura/listaFactura";
    private static final String LISTAR_PAGO = "factura/listaPago";

    @RequestMapping(value = "/factura")
    public String crear(@RequestParam(name = "clienteID") Integer clienteID, Model modelo, RedirectAttributes flash) {
        Cliente cliente = dataCliente.findOne(clienteID);
        if (cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en la Base de datos");
            return LISTAR_CLIENTE;
        }
        Factura factura = new Factura();
        factura.setCliente(cliente);
        modelo.addAttribute("listaplan", PlanDao.findOne(clienteID));
        modelo.addAttribute("factura", factura);
        modelo.addAttribute("titulo", "Nueva Factura");
        return VER_FORMULARIO_FACTURA;

    }

    @RequestMapping(value = "/listarfactura")
    public String listarfactura(Model modelo) {
        List<Factura> factura = facturaD.findFacturaByEstado(true);
        modelo.addAttribute("listafactura", factura);
        return LISTAR_FACTURA;
    }

    @GetMapping(value = "/listarfacturaid")
    public String verfac(@RequestParam(name = "id") Integer id, Model model) {
        model.addAttribute("facturasid", FacturaDao.findFacturabyid(id));
        return LISTAR_FACTURA;

    }

    @GetMapping("/pagar/{id}")
    public String pagar(@PathVariable("id") int id, SessionStatus status, Model modelo, RedirectAttributes flash) {
        Factura factura = FacturaDao.findFacturabyid(id);
        Pago pago = new Pago();
        pago.setPago(factura.getValor());
        pago.setSaldo(0);
        pago.setFactura(factura);
        factura.setEstado(false);
        pagosDAO.save(pago);
        facturaD.save(factura);
        try {
            reporte.PagoPdfReport(factura.getId(), pago.getId() + "_" + factura.getCliente().getNombres() + ".pdf");

        } catch (JRException e) {
            e.printStackTrace();
        }
        smsService.enviarSMS(factura.getCliente().getTelefono(),
                "Estimado(a) " + factura.getCliente().getNombres() + " hemos recibido su pago "
                        + " http://sysredcartagena.duckdns.org:8888/descargarpago/" + factura.getId());
        flash.addFlashAttribute("info", "Pago agregado correctamente");
        status.setComplete();
        return "redirect:/listarfactura";
    }

    @GetMapping("/recordar/{id}")
    public String recordar(@PathVariable("id") int id, SessionStatus status, Model modelo, RedirectAttributes flash) {
        Factura factura = FacturaDao.findFacturabyid(id);
        String telefono = factura.getCliente().getTelefono();
        if (telefono.length() != 10) {
            flash.addFlashAttribute("error", "No se ha enviado el mensaje el numero de telefono es invalido ");
            status.setComplete();
        }

        else {
            factura.setNotificacion(factura.getNotificacion() + 1);
            facturaD.save(factura);
            smsService.enviarSMS(telefono, "No hemos recibido su pago del mes de Internet actual.");
            flash.addFlashAttribute("info", "El mensaje ha sido enviado a : " + telefono);
            status.setComplete();
        }
        return "redirect:/listarfactura";
    }

    @GetMapping("/avisocorte/{id}")
    public String avisocorte(@PathVariable("id") int id, SessionStatus status, Model modelo, RedirectAttributes flash) {
        Factura factura = FacturaDao.findFacturabyid(id);
        factura.setEstado(true);
        facturaD.save(factura);
        smsService.enviarSMS(factura.getCliente().getTelefono(), "Estimado(a) " + factura.getCliente().getNombres()
                + " Usted cuenta con dos facturas vencidas, " + "su servicio de internet será suspendido Att. SYSRED");
        flash.addFlashAttribute("info", "El mensaje ha sido enviado a : " + factura.getCliente().getTelefono());
        status.setComplete();
        return "redirect:/listarfactura";
    }

    @GetMapping("/eliminarfactura/{id}")
    public String eliminarfactura(@PathVariable("id") int id, SessionStatus status, Model modelo,
            RedirectAttributes flash) {
        Factura f = FacturaDao.findFacturabyid(id);
        f.setEstado(false);
        facturaD.deleteById(id);
        flash.addFlashAttribute("warning", "Cliente Eliminado con exito");
        status.setComplete();
        return "redirect:/listarfactura";
    }

    @PostMapping("/savefactura")
    public String facturar(@Validated Factura factura, RedirectAttributes flash, BindingResult result) {
        Calendar fechavencimiento = Calendar.getInstance();
        fechavencimiento.setTime(new Date());
        int diapago = factura.getCliente().getDiapago();
        fechavencimiento.set(Calendar.DAY_OF_MONTH, diapago);
        String client = factura.getCliente().getIdentificacion();
        factura.setFechapago(fechavencimiento.getTime());
        factura.setFechavencimiento(fechavencimiento.getTime());
        factura.setValor(factura.getCliente().getPlanes().getPrecio());
        factura.setNotificacion(0);
        factura.setPeriodo(LocalDate.now().getMonthValue() + 1);

        dataCliente.saveFactura(factura);
        String nombres = factura.getCliente().getNombres() + ' ' + factura.getCliente().getApellidos();
        String body = "Estimado(a) Cliente " + nombres
                + " se ha generado una nueva factura a su nombre Gracias por su Preferencia";
        String email = factura.getCliente().getEmail();
        int id = factura.getId();
        try {
            reporte.createPdfReport(id, client + ".pdf");

        } catch (JRException e) {
            e.printStackTrace();
        }

        smsService.enviarSMS(factura.getCliente().getTelefono(),
                "Estimado(a) " + factura.getCliente().getNombres()
                        + " se ha generado una nueva factura de su servicio de Internet "
                        + " http://sysredcartagena.duckdns.org:8081/descargarfactura/" + factura.getId());
        mailService.sendEmailAttachment("Llegó tu factura de Internet!", body, "administracion@tecnowisp.com.co", email,
                true, new File(client + ".pdf"));
        flash.addFlashAttribute("info", "Se ha generado una factura a " + factura.getCliente().getNombres() + " "
                + factura.getCliente().getApellidos() + " correctamente");
        return "redirect:/listar";
    }

    @GetMapping("/facturar")
    public String facturarEnLote(@Validated Factura facturas, RedirectAttributes flash, BindingResult result,
            Model modelo) {
        Calendar fechaactual = Calendar.getInstance();
        Calendar fechavencimiento = Calendar.getInstance();
        int diaactual = fechaactual.get(Calendar.DAY_OF_MONTH);
        List<Cliente> cliente = clienteDao.findAll();
        int x = 0;
        int sum = 0;
        while (x < cliente.size()) {
            Factura factura = new Factura();
            fechavencimiento.setTime(new Date());
            if (cliente.get(x).getDiapago() < 11 && diaactual > 25) {
                int diapago = cliente.get(x).getDiapago();
                fechavencimiento.add(Calendar.MONTH, 1);
                fechavencimiento.set(Calendar.DAY_OF_MONTH, diapago);
                factura.setCliente(cliente.get(x));
                factura.setFechapago(fechavencimiento.getTime());
                factura.setFechavencimiento(fechavencimiento.getTime());
                factura.setValor(cliente.get(x).getPlanes().getPrecio());
                factura.setNotificacion(0);
                factura.setPeriodo(LocalDate.now().getMonthValue() + 1);
                facturaD.save(factura);
                sum = sum + 1;
                int id = factura.getId();
                String client = factura.getCliente().getIdentificacion();

                try {
                    reporte.createPdfReport(id, client + ".pdf");

                } catch (JRException e) {
                    e.printStackTrace();
                }
                String nombres = factura.getCliente().getNombres() + ' ' + factura.getCliente().getApellidos();
                String body = "Estimado(a) Cliente " + nombres
                        + " se ha generado una nueva factura a su nombre Gracias por su Preferencia";
                String email = factura.getCliente().getEmail();
                String tel = factura.getCliente().getTelefono();
                mailService.sendEmailAttachment("Llegó tu factura de Internet!", body,
                        "administracion@tecnowisp.com.co", email, true, new File(client + ".pdf"));
                if (tel.length() == 10) {
                    smsService.enviarSMS(factura.getCliente().getTelefono(),
                            "Estimado(a) " + factura.getCliente().getNombres()
                                    + " se ha generado una nueva factura de su servicio de Internet "
                                    + "http://sysredcartagena.duckdns.org:8081/descargarfactura/" + factura.getId());
                }
            } else if (cliente.get(x).getDiapago() >= 11 && diaactual < 25) {
                int diapago = cliente.get(x).getDiapago();
                fechavencimiento.set(Calendar.DAY_OF_MONTH, diapago);
                factura.setCliente(cliente.get(x));
                factura.setFechapago(fechavencimiento.getTime());
                factura.setFechavencimiento(fechavencimiento.getTime());
                factura.setValor(cliente.get(x).getPlanes().getPrecio());
                factura.setNotificacion(0);
                factura.setPeriodo(LocalDate.now().getMonthValue());
                facturaD.save(factura);
                sum = sum + 1;
                int id = factura.getId();
                String client = factura.getCliente().getIdentificacion();
                try {
                    reporte.createPdfReport(id, client + ".pdf");

                } catch (JRException e) {
                    e.printStackTrace();
                }
                String nombres = factura.getCliente().getNombres() + ' ' + factura.getCliente().getApellidos();
                String body = "Estimado(a) Cliente " + nombres
                        + " se ha generado una nueva factura a su nombre Gracias por su Preferencia";
                String email = factura.getCliente().getEmail();
                String tel = factura.getCliente().getTelefono();
                mailService.sendEmailAttachment("Llegó tu factura de Internet!", body,
                        "administracion@tecnowisp.com.co", email, true, new File(client + ".pdf"));

                if (tel.length() == 10) {
                    smsService.enviarSMS(factura.getCliente().getTelefono(),
                            "Estimado(a) " + factura.getCliente().getNombres()
                                    + " se ha generado una nueva factura de su servicio de Internet "
                                    + " descargar -->: http://sysredcartagena.duckdns.org:8081/descargarfactura/"
                                    + factura.getId());
                }
            }

            x++;
        }

        flash.addFlashAttribute("info", "Se han generado : " + sum + " facturas");
        List<Cliente> clientes = clienteDao.findAll();
        modelo.addAttribute("cliente", clientes);
        return "redirect:/listar";
    }

    @GetMapping("/descargarfactura/{id}")
    public void descargarfactura(@PathVariable(value = "id") Integer id, RedirectAttributes flash,
            HttpServletResponse response) throws IOException, JRException {
        Factura factura = FacturaDao.findFacturabyid(id);
        if (factura != null) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", String.format("attachment; filename=" + factura.getId() + "_"
                    + factura.getCliente().getNombres() + "_" + factura.getCliente().getApellidos() + ".pdf"));
            OutputStream out = response.getOutputStream();
            JasperPrint jasperPrint = reporte.DescargarPdfFile(factura.getId());
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        }

    }

    @GetMapping("/descargarpago/{id}")
    public void descargarpago(@PathVariable(value = "id") Integer id, RedirectAttributes flash,
            HttpServletResponse response) throws IOException, SQLException, JRException {
        Factura factura = FacturaDao.findFacturabyid(id);
        if (factura != null) {
            JasperPrint jasperPrint = null;
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", String.format(
                    "attachment; filename=" + factura.getId() + "_" + factura.getCliente().getNombres() + ".pdf"));
            OutputStream out = response.getOutputStream();
            jasperPrint = reporte.DescargarPagoFile(factura.getId());
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        }

    }

    @RequestMapping(value = "/listarpago")
    public String listarpago(Model modelo) {
        List<PagoDTO> pago = pagosD.lista();
        modelo.addAttribute("listapagos", pago);
        return LISTAR_PAGO;
    }
}
