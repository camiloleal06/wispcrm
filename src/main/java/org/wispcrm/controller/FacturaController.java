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
import org.wispcrm.daos.InterfacePagos;
import org.wispcrm.interfaces.ClienteInterface;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.Factura;
import org.wispcrm.modelo.Pago;
import org.wispcrm.modelo.PagoDTO;
import org.wispcrm.services.ClienteServiceImpl;
import org.wispcrm.services.EnviarSMS;
import org.wispcrm.services.FacturaReportService;
import org.wispcrm.services.FacturaServiceImpl;
import org.wispcrm.services.MailService;
import org.wispcrm.services.PagoService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@SessionAttributes("factura")
public class FacturaController {
    private static final String REDIRECT_LISTAR = "redirect:/listar";
    private static final int DIA_PAGO_INICIAL = 11;
    private static final int DIA_PAGO_FINAL = 25;
    private static final String LLEGO_TU_FACTURA_DE_INTERNET = "Llegó tu factura de Internet!";
    private static final String SE_HA_GENERADO_UNA_NUEVA_FACTURA_DE_SU_SERVICIO_DE_INTERNET = " se ha generado una nueva factura de su servicio de Internet ";
    private static final String EMAIL_TO = "sysredcartagena@gmail.com";
    private static final String ESTIMADO_A_CLIENTE = "Estimado(a) Cliente ";
    private static final String SE_HA_GENERADO_UNA_NUEVA_FACTURA_A_SU_NOMBRE_GRACIAS_POR_SU_PREFERENCIA = " se ha generado una nueva factura a su nombre Gracias por su Preferencia";
    private static final String ESTIMADO_A = "Estimado(a) ";
    private static final String REDIRECT_LISTARFACTURA = "redirect:/listarfactura";
    @Autowired
    private PagoService pagosDAO;
    @Autowired
    private InterfacePagos pagosD;

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    ClienteInterface clienteDao;

    @Autowired
    FacturaReportService reporte;

    @Autowired
    private MailService mailService;

    @Autowired
    private FacturaServiceImpl facturaDao;

    @Autowired
    private EnviarSMS smsService;

    Calendar fechaactual = Calendar.getInstance();
    Calendar fechavencimiento = Calendar.getInstance();
    int diaactual = fechaactual.get(Calendar.DAY_OF_MONTH);

    private static final String VER_FORMULARIO_FACTURA = "factura/formFactura";
    private static final String LISTAR_CLIENTE = "cliente/listaCliente";
    private static final String LISTAR_FACTURA = "factura/listaFactura";
    private static final String LISTAR_PAGO = "factura/listaPago";

    /**
     * 
     * @param clienteID
     * @param modelo
     * @param flash
     * @return
     */
    @RequestMapping(value = "/factura")
    public String crear(@RequestParam(name = "clienteID") Integer clienteID, Model modelo, RedirectAttributes flash) {
        Cliente cliente = clienteService.findOne(clienteID);
        if (cliente == null) {
            flash.addFlashAttribute("error", "El cliente no existe en la Base de datos");
            return LISTAR_CLIENTE;
        }
        Factura factura = new Factura();
        factura.setCliente(cliente);
        modelo.addAttribute("listaplan", cliente.getPlanes());
        modelo.addAttribute("factura", factura);
        modelo.addAttribute("titulo", "Nueva Factura");
        return VER_FORMULARIO_FACTURA;
    }

    /**
     * 
     * @param modelo
     * @return
     */
    @RequestMapping(value = "/listarfactura")
    public String listarfactura(Model modelo) {
        modelo.addAttribute("listafactura", facturaDao.listadoFacturas());
        return LISTAR_FACTURA;
    }

    /**
     * 
     * @param id
     * @param model
     * @return
     */
    @GetMapping(value = "/listarfacturaid")
    public String verfac(@RequestParam(name = "id") Integer id, Model model) {
        model.addAttribute("facturasid", facturaDao.findFacturabyid(id));
        return LISTAR_FACTURA;

    }

    /**
     * 
     * @param id
     * @param flash
     * @return
     */
    @GetMapping("/pagar/{id}")
    public String pagar(@PathVariable("id") int id, RedirectAttributes flash) {
        Factura factura = facturaDao.findFacturabyid(id);
        Pago pago = new Pago();
        pago.setPago(factura.getValor());
        pago.setSaldo(0);
        pago.setFactura(factura);
        factura.setEstado(false);
        pagosDAO.save(pago);
        facturaDao.save(factura);
        try {
            reporte.pagoPdfReport(factura.getId(), pago.getId() + "_" + factura.getCliente().getNombres() + ".pdf");

        } catch (JRException e) {
            e.printStackTrace();
        }
        smsService.enviarSMS(factura.getCliente().getTelefono(),
                ESTIMADO_A + factura.getCliente().getNombres() + " hemos recibido su pago ");
        flash.addFlashAttribute("info", "Pago agregado correctamente");
        return REDIRECT_LISTARFACTURA;
    }

    @GetMapping("/pagarmultiple")
    public String pagarMultiple(@RequestParam(name = "present", defaultValue = "0") List<String> values,
            RedirectAttributes flash) {

        Pago pago = new Pago();

        values.parallelStream().forEach(item -> {
            Factura factura = facturaDao.findFacturabyid(Integer.valueOf(item));
            pago.setPago(factura.getValor());
            pago.setSaldo(0);
            pago.setFactura(factura);
            factura.setEstado(false);
            // pagosDAO.save(pago);
            // facturaDao.save(factura);
            System.out.println(factura.toString());
        });

        flash.addFlashAttribute("info", " Pago agregado correctamente " + values.size());
        return REDIRECT_LISTARFACTURA;
    }

    /*
     * 
     * 
     * try { reporte.pagoPdfReport(factura.getId(), pago.getId() + "_" +
     * factura.getCliente().getNombres() + ".pdf");
     * 
     * } catch (JRException e) { e.printStackTrace(); }
     * smsService.enviarSMS(factura.getCliente().getTelefono(), ESTIMADO_A +
     * factura.getCliente().getNombres() + " hemos recibido su pago ");
     * flash.addFlashAttribute("info", "Pago agregado correctamente"); return
     * REDIRECT_LISTARFACTURA;
     */

    /// @RequestParam("present") List<String> values

    /**
     * 
     * @param id
     * @param status
     * @param modelo
     * @param flash
     * @return
     */
    @GetMapping("/recordar/{id}")
    public String recordar(@PathVariable("id") int id, SessionStatus status, Model modelo, RedirectAttributes flash) {
        Factura factura = facturaDao.findFacturabyid(id);
        String telefono = factura.getCliente().getTelefono();
        if (telefono.length() != 10) {
            flash.addFlashAttribute("error", "No se ha enviado el mensaje el numero de telefono es invalido ");
            status.setComplete();
        } else {
            factura.setNotificacion(factura.getNotificacion() + 1);
            facturaDao.save(factura);
            smsService.enviarSMS(telefono, "No hemos recibido su pago del mes de Internet actual.");
            flash.addFlashAttribute("info", "El mensaje ha sido enviado a : " + telefono);
            status.setComplete();
        }
        return REDIRECT_LISTARFACTURA;
    }

    /**
     * 
     * @param id
     * @param status
     * @param modelo
     * @param flash
     * @return
     */
    @GetMapping("/avisocorte/{id}")
    public String avisocorte(@PathVariable("id") int id, SessionStatus status, Model modelo, RedirectAttributes flash) {
        Factura factura = facturaDao.findFacturabyid(id);
        factura.setEstado(true);
        facturaDao.save(factura);
        smsService.enviarSMS(factura.getCliente().getTelefono(), ESTIMADO_A + factura.getCliente().getNombres()
                + " Usted cuenta con dos facturas vencidas, " + "su servicio de internet será suspendido Att. SYSRED");
        flash.addFlashAttribute("info", "El mensaje ha sido enviado a : " + factura.getCliente().getTelefono());
        status.setComplete();
        return REDIRECT_LISTARFACTURA;
    }

    /**
     * 
     * @param id
     * @param status
     * @param modelo
     * @param flash
     * @return
     */
    @GetMapping("/eliminarfactura/{id}")
    public String eliminarfactura(@PathVariable("id") int id, SessionStatus status, Model modelo,
            RedirectAttributes flash) {
        Factura f = facturaDao.findFacturabyid(id);
        f.setEstado(false);
        facturaDao.delete(id);
        flash.addFlashAttribute("warning", "Cliente Eliminado con exito");
        status.setComplete();
        return REDIRECT_LISTARFACTURA;
    }

    /**
     * 
     * @param factura
     * @param flash
     * @param result
     * @return
     */
    @PostMapping("/savefactura")
    public String crearFacturaAndSendSms(@Validated Factura factura, RedirectAttributes flash, BindingResult result) {
        fechavencimiento.setTime(new Date());
        fechavencimiento.set(Calendar.DAY_OF_MONTH, factura.getCliente().getDiapago());
        factura.setFechapago(fechavencimiento.getTime());
        factura.setFechavencimiento(fechavencimiento.getTime());
        factura.setValor(factura.getCliente().getPlanes().getPrecio());
        factura.setNotificacion(0);
        factura.setPeriodo(LocalDate.now().getMonthValue() + 1);
        clienteService.saveFactura(factura);
        sendSms(factura.getCliente().getTelefono(), factura.getCliente().getNombres(), factura.getId());
        flash.addFlashAttribute("info", "Se ha generado una factura a " + factura.getCliente().getNombres() + " "
                + factura.getCliente().getApellidos() + " correctamente");
        return REDIRECT_LISTAR;
    }

    /**
     * 
     * @param flash
     * @param modelo
     * @return
     */
    @GetMapping("/facturar")
    public String facturarEnLote(RedirectAttributes flash, Model modelo) {
        List<Cliente> cliente = clienteDao.findAll();
        int x = 0;
        int sum = 0;
        while (x < cliente.size()) {
            Factura factura = new Factura();
            fechavencimiento.setTime(new Date());
            if (cliente.get(x).getDiapago() < DIA_PAGO_INICIAL && diaactual > DIA_PAGO_FINAL) {
                int diapago = cliente.get(x).getDiapago();
                save(factura, diapago, cliente.get(x), 1);
                sum = sum + 1;
                send(factura);
            } else if (cliente.get(x).getDiapago() >= DIA_PAGO_INICIAL && diaactual < DIA_PAGO_FINAL) {
                int diapago = cliente.get(x).getDiapago();
                save(factura, diapago, cliente.get(x), 0);
                sum = sum + 1;
                send(factura);
            }
            x++;
        }

        flash.addFlashAttribute("info", "Se han generado : " + sum + " facturas");
        List<Cliente> clientes = clienteDao.findAll();
        modelo.addAttribute("cliente", clientes);
        return REDIRECT_LISTAR;
    }

    /**
     * 
     * @param flash
     * @param id
     * @return
     */
    @GetMapping("/facturar/{id}")
    public String facturarUno(RedirectAttributes flash, @PathVariable("id") int id) {
        Cliente cliente = clienteDao.findById(id);
        Factura factura = new Factura();
        fechavencimiento.setTime(new Date());
        int diapago = cliente.getDiapago();
        save(factura, diapago, cliente, 1);
        smsService.enviarSMS(cliente.getTelefono(), "Se ha generado su factura de internet");
        flash.addFlashAttribute("info", "Se han generado la Factura");

        return REDIRECT_LISTAR;
    }

    /**
     * 
     * @param id
     * @param flash
     * @param response
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     */
    @GetMapping("/descargarfactura/{id}")
    public void descargarfactura(@PathVariable(value = "id") Integer id, RedirectAttributes flash,
            HttpServletResponse response) throws IOException, JRException, SQLException {
        Factura factura = facturaDao.findFacturabyid(id);
        if (factura != null) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=" + factura.getId() + "_"
                    + factura.getCliente().getNombres() + "_" + factura.getCliente().getApellidos() + ".pdf");
            OutputStream out = response.getOutputStream();
            JasperPrint jasperPrint = reporte.descargarPdfFile(factura.getId());
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        }
    }

    /**
     * 
     * @param id
     * @param flash
     * @param response
     * @throws IOException
     * @throws JRException
     */
    @GetMapping("/descargarpago/{id}")
    public void descargarpago(@PathVariable(value = "id") Integer id, RedirectAttributes flash,
            HttpServletResponse response) throws IOException, JRException {
        Factura factura = facturaDao.findFacturabyid(id);
        if (factura != null) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + factura.getId() + "_" + factura.getCliente().getNombres() + ".pdf");
            OutputStream out = response.getOutputStream();
            JasperPrint jasperPrint = reporte.descargarPagoFile(factura.getId());
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
        }
    }

    /**
     * 
     * @param modelo
     * @return
     */
    @RequestMapping(value = "/listarpago")
    public String listarpago(Model modelo) {
        List<PagoDTO> pago = pagosD.lista();
        modelo.addAttribute("listapagos", pago);
        return LISTAR_PAGO;
    }

    /**
     * 
     * @param factura
     * @param diapago
     * @param cliente
     * @param periodo
     * @return
     */
    private Factura save(Factura factura, int diapago, Cliente cliente, int periodo) {
        fechavencimiento.add(Calendar.MONTH, 1);
        fechavencimiento.set(Calendar.DAY_OF_MONTH, diapago);
        factura.setCliente(cliente);
        factura.setFechapago(fechavencimiento.getTime());
        factura.setFechavencimiento(fechavencimiento.getTime());
        factura.setValor(cliente.getPlanes().getPrecio());
        factura.setNotificacion(0);
        factura.setPeriodo(LocalDate.now().getMonthValue() + periodo);
        Factura facturaSend = facturaDao.save(factura);
        sendEmail(facturaSend.getId(), cliente);
        return facturaSend;
    }

    /**
     * 
     * @param factura
     */
    private void send(Factura factura) {
        int id = factura.getId();
        String client = factura.getCliente().getIdentificacion();
        try {
            reporte.createPdfReport(id, client + ".pdf");

        } catch (JRException e) {
            e.printStackTrace();
        }
        String nombres = factura.getCliente().getNombres() + ' ' + factura.getCliente().getApellidos();
        String body = ESTIMADO_A_CLIENTE + nombres
                + SE_HA_GENERADO_UNA_NUEVA_FACTURA_A_SU_NOMBRE_GRACIAS_POR_SU_PREFERENCIA;
        String email = factura.getCliente().getEmail();
        mailService.sendEmailAttachment(LLEGO_TU_FACTURA_DE_INTERNET, body, EMAIL_TO, email, true,
                new File(client + ".pdf"));
    }

    /**
     * 
     * @param telefonoCliente
     * @param nombresCliente
     * @param facturaId
     */
    private void sendSms(String telefonoCliente, String nombresCliente, int facturaId) {
        smsService.enviarSMS(telefonoCliente,
                ESTIMADO_A + nombresCliente + SE_HA_GENERADO_UNA_NUEVA_FACTURA_DE_SU_SERVICIO_DE_INTERNET
                        + "http://sysredcartagena.duckdns.org:8081/descargarfactura/" + facturaId);
    }

    /**
     * 
     * @param facturaId
     * @param cliente
     */
    private void sendEmail(int facturaId, Cliente cliente) {
        String nombres = cliente.getNombres() + ' ' + cliente.getApellidos();
        String email = cliente.getEmail();
        String client = cliente.getIdentificacion();
        String body = ESTIMADO_A_CLIENTE + nombres
                + SE_HA_GENERADO_UNA_NUEVA_FACTURA_A_SU_NOMBRE_GRACIAS_POR_SU_PREFERENCIA;
        try {
            reporte.createPdfReport(facturaId, client + ".pdf");
            mailService.sendEmailAttachment(LLEGO_TU_FACTURA_DE_INTERNET, body, EMAIL_TO, email, true,
                    new File(client + ".pdf"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
