package org.wispcrm.modelo;

import java.util.Date;

public class PagoDTO {
private int id;
private double pago;
private int facturaId;
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
public int getFacturaId() {
	return facturaId;
}
public void setFacturaId(int facturaId) {
	this.facturaId = facturaId;
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
		int facturaId,String nombre,
		Date fechapago,boolean estado) {
	super();
	this.id = id;
	this.pago = pago;
	this.facturaId = facturaId;
	this.nombre=nombre;
	this.fechapago=fechapago;
	this.estado=estado;
}


}
