package org.wispcrm.restcontroller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.wispcrm.daos.ClienteDao;
import org.wispcrm.daos.InterfacePagos;
import org.wispcrm.interfaces.ClienteInterface;
import org.wispcrm.interfaces.NotificacionInterface;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.EstadoCliente;
import org.wispcrm.modelo.Factura;
import org.wispcrm.modelo.Pago;
import org.wispcrm.modelo.PagoDTO;
import org.wispcrm.services.*;
import org.wispcrm.utils.ConstantMensaje;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import static org.wispcrm.utils.Util.currentUserName;

@Controller
@SessionAttributes("factura")
@Slf4j
public class FacturaController {
    private static final int DIA_TREINTI_UNO = 31;
    private static final int DIA_PAGO_UNO = 1;
    private static final int DIA_PAGO_VEINTI_UNO = 21;
    private static final int DIA_PAGO_DIEZ = 10;
    public static final String INFO = "info";
    private static final int DIA_PAGO_ONCE = 11;
    private static final int DIA_PAGO_VEINTE = 20;
    private static final String LLEGO_TU_FACTURA_DE_INTERNET = "Llegó tu factura de Internet!";
    private static final String SE_HA_GENERADO_UNA_NUEVA_FACTURA_DE_SU_SERVICIO_DE_INTERNET = " se ha generado una nueva factura de su servicio de Internet ";
    private static final String EMAIL_TO = "sysredcartagena@gmail.com";
    private static final String ESTIMADO_A_CLIENTE = "Estimado(a) Cliente ";
    private static final String SE_HA_GENERADO_UNA_NUEVA_FACTURA_A_SU_NOMBRE_GRACIAS_POR_SU_PREFERENCIA = " se ha generado una nueva factura a su nombre Gracias por su Preferencia";
    private static final String ESTIMADO_A = "Estimado(a) ";
    @Autowired
    private PagoService pagosDAO;
    @Autowired
    private InterfacePagos pagosD;

    @Autowired
    private NotificacionInterface notificacionInterface;

    @Autowired
    private ClienteServiceImpl clienteService;

    @Autowired
    ClienteInterface clienteDao;

    @Autowired
    ClienteDao clienteRepo;

    @Autowired
    FacturaReportService reporte;

    @Autowired
    private MailService mailService;

    @Autowired
    private FacturaServiceImpl facturaDao;

    @Autowired
    private EnviarSMS smsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private WhatsappMessageService whatsappMessageService;

    Calendar fechaactual = Calendar.getInstance();
    Calendar fechavencimiento = Calendar.getInstance();
    int diaactual = fechaactual.get(Calendar.DAY_OF_MONTH);

    private static final String VER_FORMULARIO_FACTURA = "factura/formFactura";
    private static final String LISTAR_CLIENTE = "cliente/listaCliente";
    private static final String LISTAR_FACTURA = "factura/listaFactura";
    private static final String REDIRECT_LISTARFACTURA = "redirect:/listarfactura";
    private static final String LISTAR_PAGO = "factura/listaPago";
    private static final String REDIRECT_LISTAR = "redirect:/listar";

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FacturaController.class);

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
        modelo.addAttribute("diaactual", diaactual);
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
    public String pagar(@PathVariable("id") int id, SessionStatus status, RedirectAttributes flash)
            throws IOException {
        Factura factura = facturaDao.findFacturabyid(id);
        Pago pago = new Pago();
        pago.setPago(factura.getValor());
        pago.setSaldo(0);
        pago.setFactura(factura);
        factura.setEstado(false);
        pagosDAO.save(pago);
        Factura facturaSaved = facturaDao.save(factura);
        enviarSmsPagoRecibido(facturaSaved);
        flash.addFlashAttribute(INFO, "Pago agregado correctamente");
        status.setComplete();
        return REDIRECT_LISTARFACTURA;
    }

    /**
     * 
     * @param values
     * @param flash
     * @return
     */
    @GetMapping("/pagarmultiple")
    public String pagarMultiple(@RequestParam(name = "present", defaultValue = "0") List<String> values,
            RedirectAttributes flash) {
        List<String> listaFacturas = new ArrayList<>();
        values.forEach(item -> {
            Factura factura = new Factura();
            factura = facturaDao.findFacturabyid(Integer.valueOf(item));
            Pago pago = Pago.builder().pago(factura.getValor()).saldo(0).factura(factura).build();
            factura.setEstado(false);
            pagosDAO.save(pago);
            Factura facturaSaved = facturaDao.save(factura);
            listaFacturas.add(facturaSaved.getCliente().getNombres());
            try {
                enviarSmsPagoRecibido(facturaSaved);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        flash.addFlashAttribute(INFO, "se han agregado correctamente " + values.size() + " pagos " + listaFacturas);
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
            enviarSms(factura, ConstantMensaje.RECORDATORIO_PAGO);
            flash.addFlashAttribute(INFO, "El mensaje ha sido enviado a : " + telefono);
            status.setComplete();
        }
        return REDIRECT_LISTARFACTURA;
    }

    /**
     * 
     * @param id
    * @param flash
     * @return
     */
    @GetMapping("/avisocorte/{id}")
    public String avisocorte(@PathVariable("id") int id, RedirectAttributes flash) {
        Factura factura = facturaDao.findFacturabyid(id);
        factura.setEstado(true);
        facturaDao.save(factura);
        smsService.enviarSMS(factura.getCliente().getTelefono(), ESTIMADO_A + factura.getCliente().getNombres()
                + " Usted cuenta con dos facturas vencidas, " + "su servicio de internet será suspendido Att. SYSRED");
        flash.addFlashAttribute(INFO, "El mensaje ha sido enviado a : " + factura.getCliente().getTelefono());
        return REDIRECT_LISTARFACTURA;
    }

    /**
     * 
     * @param id

     * @param flash
     * @return
     */
    @GetMapping("/eliminarfactura/{id}")
    public String eliminarfactura(@PathVariable("id") int id, RedirectAttributes flash) {
        Factura factura = facturaDao.findFacturabyid(id);
        if (factura != null) {
            facturaDao.delete(id);
            flash.addFlashAttribute("warning", "Factura : " + factura.getId() + " Eliminada con exito");
        } else {
            flash.addFlashAttribute("error", "No se ha podido eliminar");
        }

        return REDIRECT_LISTARFACTURA;
    }

    /**
     * 
     * @param factura
     * @param flash
        * @return
     */
    @PostMapping("/savefactura")
    public String crearFacturaAndSendSms(@Validated Factura factura, RedirectAttributes flash) {
        fechavencimiento.setTime(new Date());
        fechavencimiento.set(Calendar.DAY_OF_MONTH, factura.getCliente().getDiapago());
        factura.setFechapago(fechavencimiento.getTime());
        factura.setFechavencimiento(fechavencimiento.getTime());
        factura.setValor(factura.getCliente().getPlanes().getPrecio());
        factura.setNotificacion(ConstantMensaje.ZERO_INT);
        factura.setPeriodo(LocalDate.now().getMonthValue() + DIA_PAGO_UNO);
        clienteService.saveFactura(factura);
        sendSmsNuevaFacturaGenerada(factura);
        flash.addFlashAttribute(INFO, "Se ha generado una factura a " + factura.getCliente().getNombres() + " "
                + factura.getCliente().getApellidos() + " correctamente");
        return REDIRECT_LISTAR;
    }

    /**
     * 
     * @param flash
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/facturar")
    public String facturarEnLote(RedirectAttributes flash) throws InterruptedException {

        List<Cliente> listaClientes = new ArrayList<>();

        if (diaactual <= DIA_PAGO_DIEZ) {

            listaClientes = clienteRepo.findByDiapagoBetween(DIA_PAGO_UNO, DIA_PAGO_DIEZ).stream()
                    .filter(cliente -> cliente.getEstado() == EstadoCliente.ACTIVO).collect(Collectors.toList());
            createFacturasEntreDiaInicialDiaFinal(listaClientes);
        }


        if (diaactual >= DIA_PAGO_ONCE && diaactual <= DIA_PAGO_VEINTE) {

            listaClientes = clienteDao.findByDiaPagoBetween(DIA_PAGO_ONCE, DIA_PAGO_VEINTE).stream()
                    .filter(cliente -> cliente.getEstado() == EstadoCliente.ACTIVO).collect(Collectors.toList());
            createFacturasEntreDiaInicialDiaFinal(listaClientes);
        }

        if (diaactual >= DIA_PAGO_VEINTI_UNO) {

            listaClientes = clienteDao.findByDiaPagoBetween(DIA_PAGO_VEINTI_UNO, DIA_TREINTI_UNO).stream()
                    .filter(cliente -> cliente.getEstado() == EstadoCliente.ACTIVO).collect(Collectors.toList());
            createFacturasEntreDiaInicialDiaFinal(listaClientes);
        }

        flash.addFlashAttribute(INFO, "Se han generado : " + listaClientes.size() + " Facturas con exito ");
        return REDIRECT_LISTARFACTURA;

    }

    /**
     * 
     * @param listaClientes
     * @throws InterruptedException
     */
    @Async("threadPoolTaskExecutor")
    private void createFacturasEntreDiaInicialDiaFinal(List<Cliente> listaClientes) {
        listaClientes.forEach(this::saveAndSendEmailAndSendSms);
    }

    /**
     * 
         * @param cliente
      * @return
     * @throws MessagingException
     */
    private Factura saveAndSendEmailAndSendSms(Cliente cliente) {
        fechavencimiento.add(Calendar.MONTH, ConstantMensaje.ZERO_INT);
        fechavencimiento.set(Calendar.DAY_OF_MONTH, cliente.getDiapago());
        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setFechapago(fechavencimiento.getTime());
        factura.setFechavencimiento(fechavencimiento.getTime());
        factura.setValor(cliente.getPlanes().getPrecio());
        factura.setNotificacion(ConstantMensaje.ZERO_INT);
        factura.setPeriodo(LocalDate.now().getMonthValue());
        Factura facturaSend = facturaDao.save(factura);
        sendSmsNuevaFacturaGenerada(facturaSend);
        emailService.sendMail(facturaSend);
        return factura;
    }

    /**
     * 
     * @param flash
     * @param id
     * @return
     * @throws MessagingException
     */
    @GetMapping("/facturar/{id}")
    public String facturarUno(RedirectAttributes flash, @PathVariable("id") int id) throws MessagingException {
        Cliente cliente = clienteDao.findById(id);
        Factura factura = new Factura();
        int diapago = cliente.getDiapago();
        save(factura, diapago, cliente, 0, 0);
        sendSmsNuevaFacturaGenerada(factura);
        flash.addFlashAttribute(INFO, "Se han generado la Factura");
        return REDIRECT_LISTAR;
    }

    /**
     * 
     * @param factura
     * @param diapago
     * @param cliente
     * @param periodo
     * @param mes
     * @return
     * @throws MessagingException
     */

    private Factura save(Factura factura, int diapago, Cliente cliente, int periodo, int mes) {
        if(!facturaDao.existeFacturaPeriodo()) {
            fechavencimiento.add(Calendar.MONTH, mes);
            fechavencimiento.set(Calendar.DAY_OF_MONTH, diapago);
            factura.setCliente(cliente);
            factura.setFechapago(fechavencimiento.getTime());
            factura.setFechavencimiento(fechavencimiento.getTime());
            factura.setValor(cliente.getPlanes().getPrecio());
            factura.setNotificacion(ConstantMensaje.ZERO_INT);
            factura.setPeriodo(LocalDate.now().getMonthValue() + periodo);
            Factura facturaSend = facturaDao.save(factura);
            emailService.sendMail(factura);
        }
        // sendEmail(facturaSend.getId(), cliente);
        return factura;
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
        modelo.addAttribute("totalpagos", pago.stream().mapToDouble(PagoDTO::getPago).sum());
        return LISTAR_PAGO;
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

   /**
     * 
     * @param factura
     * @param tipoSms
     */
    private void enviarSms(Factura factura, String tipoSms) {
        var message = notificacionInterface.findByTipo(tipoSms).getMensaje();
        smsService.enviarSMS(factura.getCliente().getTelefono(),
                ConstantMensaje.ESTIMADO + factura.getCliente().getNombres() + message);
    }

    /**
     * 
     * @param factura
     */
    private void enviarSmsPagoRecibido(Factura factura) throws IOException {
        mailService.sendMail("sysredcartagena@gmail.com",
                "camilojesus1@gmail.com", "Pago recibido",
                "Pago recibido de : " + factura.getCliente()
                        .getNombres() + " " + factura.getCliente()
                        .getApellidos()+" "+ "cobrado por "+ currentUserName());
        try {
            reporte.pagoPdfReport(factura.getId(),
                    factura.getId() + "_pagada.pdf");
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(() -> {
                try {
                    whatsappMessageService.sendDocumentAndMessage(
                            factura.getCliente().getTelefono(),
                            ESTIMADO_A + factura.getCliente()
                                    .getNombres() + ConstantMensaje.HEMOS_RECIBIDO_SU_PAGO + factura.getId(),
                            factura.getId() + "_pagada.pdf",
                            ConstantMensaje.RUTA_DESCARGA_FACTURA_DOCS + factura.getId() + "_pagada.pdf");

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param factura
     */
    private void sendSmsNuevaFacturaGenerada(Factura factura) {
        try {
            reporte.createPdfReport(factura.getId(), factura.getId()+ ".pdf");
            //emailService.sendMailNotification(factura);
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.schedule(() -> {
                try {
                    whatsappMessageService.sendDocumentAndMessage(factura.getCliente().getTelefono(),
                            ESTIMADO_A + factura.getCliente().getNombres()
                                    + SE_HA_GENERADO_UNA_NUEVA_FACTURA_DE_SU_SERVICIO_DE_INTERNET,
                            factura.getId() + ".pdf",
                            ConstantMensaje.RUTA_DESCARGA_FACTURA_DOCS+factura.getId()+ ".pdf");
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }, 1, TimeUnit.SECONDS);

        } catch (JRException e) {
            log.error(e.getMessage());
        }
    }
    private  void copyFileUsingJava7Files(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
