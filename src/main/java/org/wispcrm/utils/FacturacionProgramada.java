package org.wispcrm.utils;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wispcrm.interfaces.InterfaceFacturas;
import org.wispcrm.interfaceservice.InterfaceClienteService;
import org.wispcrm.interfaceservice.InterfacePlanService;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.Factura;
import org.wispcrm.services.ClienteServices;
import org.wispcrm.services.FacturaReportService;
import org.wispcrm.services.MailService;

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component

public class FacturacionProgramada {
    @Autowired
    InterfaceClienteService clienteDao;
    @Autowired
    InterfacePlanService planesDao;
    @Autowired
    FacturaReportService reporte;
    @Autowired
    private ClienteServices datacliente;
    @Autowired
    private InterfaceFacturas facturaDao;

    @Autowired
    private MailService mailService;

    //   @Scheduled(fixedRate = 86400000)
    public void scheduleTaskWithFixedRate() throws SQLException {

        Calendar fechaactual = Calendar.getInstance();
        int diaactual = fechaactual.get(Calendar.DAY_OF_MONTH);
        Calendar fechavencimiento = Calendar.getInstance();

        fechavencimiento.setTime(new Date());
        fechavencimiento.add(Calendar.DAY_OF_YEAR, 10);

        List<Cliente> clientes = clienteDao.findAll();
        Cliente cl = new Cliente();

        for (int i = 0; i < clientes.size(); i++) {

            Factura factura = new Factura();
            cl.setId(clientes.get(i).getId());
            cl.setEmail(clientes.get(i).getEmail());
            cl.setIdentificacion(clientes.get(i).getIdentificacion());
            cl.setPlanes(clientes.get(i).getPlanes());
            factura.setFechapago(new Date());
            factura.setFechavencimiento(fechavencimiento.getTime());
            factura.setCliente(cl);
            if (diaactual == clientes.get(i).getDiapago() && facturaDao.findFirstFacturaByCliente(factura.getCliente()) == null) {
                datacliente.saveFactura(factura);
                String client = factura.getCliente().getIdentificacion();
                String nombres = factura.getCliente().getNombres() + ' ' + factura.getCliente().getApellidos();

                try {
                    reporte.createPdfReport(factura.getId(), client + ".pdf");
                    mailService.sendEmailAttachment("Llegó tu factura!!",
                            "Estimado(a) Cliente  se ha generado una factura a su nombre Gracias por su Preferencia",
                            "administracion@nacionaldemuebles.com.co", factura.getCliente().getEmail(), true, new File(client + ".pdf"));
                } catch (JRException e) {
                    e.printStackTrace();
                }


            }

        }
	    }


}
