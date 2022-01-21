package org.wispcrm.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wispcrm.interfaceService.InterfaceClienteService;
import org.wispcrm.interfaces.InterfaceFacturas;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.Factura;
import org.wispcrm.services.ClienteServices;
import org.wispcrm.services.EnviarSMS;
import org.wispcrm.services.FacturaReportService;
@Component


public class FacturasAutomaticas {
	@Autowired
	private EnviarSMS smsService;
	@Autowired
	FacturaReportService reporte;
	@Autowired
	private ClienteServices datacliente;
	@Autowired
	InterfaceClienteService ClienteDao;
	@Autowired
	InterfaceFacturas FacturaD;
	
//	@Scheduled(cron = "0 30 16 9,26 * *")
	public void facturarporlote(){
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	Calendar fechaactual = Calendar.getInstance(); 
	Calendar fechavencimiento = Calendar.getInstance();
    int diaactual = fechaactual.get(Calendar.DAY_OF_MONTH);
    int ultimodia=fechaactual.getActualMaximum(Calendar.DAY_OF_MONTH);
		List<Cliente> cliente = ClienteDao.findAll();
		int x=0;int sum=0;
		while(x<cliente.size()){
	       	Factura factura = new Factura();
	    	fechavencimiento.setTime(new Date());
		    if(cliente.get(x).getDiapago()<11 && diaactual>20){
			    int diapago=cliente.get(x).getDiapago(); 
			    fechavencimiento.add(Calendar.DAY_OF_YEAR,(ultimodia-diaactual+diapago));
		  		factura.setCliente(cliente.get(x));
		  	    factura.setFechapago(fechavencimiento.getTime());
				factura.setFechavencimiento(fechavencimiento.getTime());
				factura.setValor(cliente.get(x).getPlanes().getPrecio());
				factura.setNotificacion(0);
				FacturaD.save(factura);
				sum=sum+1;
		/*		int id = factura.getId();
				String client = factura.getCliente().getIdentificacion();

				try {
					reporte.createPdfReport(id, client + ".pdf");

				} catch (JRException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String nombres = factura.getCliente().getNombres() + ' ' + factura.getCliente().getApellidos();
				String body = "Estimado(a) Cliente " + nombres
						+ " se ha generado una nueva factura a su nombre Gracias por su Preferencia";
				String email = factura.getCliente().getEmail();
			    mailService.sendEmailAttachment("Llegó tu factura de Internet!", body, "administracion@tecnowisp.com.co",
						email, true, new File(client + ".pdf"));
			smsService.enviarSMS(factura.getCliente().getTelefono(), "Estimado(a) " + factura.getCliente().getNombres()
						+ " se ha generado una nueva factura de su servicio de Internet por : " + factura.getValor()
						+ " fecha de pago : " + dateFormat.format(factura.getFechavencimiento()) + " Att. SYSRED WISP");
			*/	
			}
		    
		    else if (cliente.get(x).getDiapago()>=11 && diaactual<20){
		    	int diapago=cliente.get(x).getDiapago(); 
			    fechavencimiento.add(Calendar.DAY_OF_YEAR,(diapago-diaactual));
		  		factura.setCliente(cliente.get(x));
		  	    factura.setFechapago(fechavencimiento.getTime());
				factura.setFechavencimiento(fechavencimiento.getTime());
				factura.setValor(cliente.get(x).getPlanes().getPrecio());
				factura.setNotificacion(0);
				FacturaD.save(factura);
				
				sum=sum+1;
				
	/*			int id = factura.getId();
				String client = factura.getCliente().getIdentificacion();

				try {
					reporte.createPdfReport(id, client + ".pdf");

				} catch (JRException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String nombres = factura.getCliente().getNombres() + ' ' + factura.getCliente().getApellidos();
				String body = "Estimado(a) Cliente " + nombres
						+ " se ha generado una nueva factura a su nombre Gracias por su Preferencia";
				String email = factura.getCliente().getEmail();
			    mailService.sendEmailAttachment("Llegó tu factura de Internet!", body, "administracion@tecnowisp.com.co",
						email, true, new File(client + ".pdf"));
			    
			smsService.enviarSMS(factura.getCliente().getTelefono(), "Estimado(a) " + factura.getCliente().getNombres()
						+ " se ha generado una nueva factura de su servicio de Internet por : " + factura.getValor()
						+ " fecha de pago : " + dateFormat.format(factura.getFechavencimiento()) + " Att. SYSRED WISP");
				
			*/ 
		    }
			x++;


		}
		
	/*	flash.addFlashAttribute("info", "Se han generado : "+sum+" facturas");
		List<Cliente> clientes = ClienteDao.findAll();
		modelo.addAttribute("cliente", clientes);
		return "redirect:/listar";*/
		smsService.enviarSMS("3225996394","Se han generado "+sum+" facturas");
	}

	


	
	


}
