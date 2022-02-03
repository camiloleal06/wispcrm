package org.wispcrm.modelo;

import java.util.Date;

public class PagoDTO {
private int id;
private double pago;
private int factura_id;
private String nombre;
private Date fechapago;
private boolean estado;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public double getPago() {
	return pago;
}
public void setPago(double pago) {
	this.pago = pago;
}
public int getFactura_id() {
	return factura_id;
}
public void setFactura_id(int factura_id) {
	this.factura_id = factura_id;
}


public String getNombre() {
	return nombre;
}
public void setNombre(String nombre) {
	this.nombre = nombre;
}


public Date getFechapago() {
	return fechapago;
}
public void setFechapago(Date fechapago) {
	this.fechapago = fechapago;
}


public boolean isEstado() {
	return estado;
}
public void setEstado(boolean estado) {
	this.estado = estado;
}
public PagoDTO(int id, double pago,
		int factura_id,String nombre,
		Date fechapago,boolean estado) {
	super();
	this.id = id;
	this.pago = pago;
	this.factura_id = factura_id;
	this.nombre=nombre;
	this.fechapago=fechapago;
	this.estado=estado;
}


}
