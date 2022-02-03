package org.wispcrm.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="Clientes")
public class Cliente implements Serializable {
/**
	 * 
	 */
private static final long serialVersionUID = 1L;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int id;

private String identificacion;

private int diapago;

private String nombres;

private String apellidos;

private String email;

private String telefono;

private String direccion;

private String ipaddres;

@Column(name = "create_at")
@Temporal(TemporalType.DATE)
@DateTimeFormat(pattern="yyyy-MM-dd")
private Date CreateAt;

@OneToMany(mappedBy = "cliente", fetch = FetchType.EAGER,cascade = CascadeType.ALL)
private List<Factura> facturas;

@ManyToOne(fetch = FetchType.EAGER)
private Plan planes;

public Plan getPlanes() {
	return planes;
}

public void setPlanes(Plan planes) {
	this.planes = planes;
}

public String getIpaddres() {
	return ipaddres;
}

public void setIpaddres(String ipaddres) {
	this.ipaddres = ipaddres;
}

public Cliente() {

facturas= new ArrayList<Factura>();
	
}

@PrePersist
public void prePersist() {
	CreateAt=new Date();
}


public List<Factura> getFacturas() {
	return facturas;
}

public void setFacturas(List<Factura> facturas) {
	this.facturas = facturas;
}

public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getNombres() {
	return nombres;
}

public void setNombres(String nombres) {
	this.nombres = nombres;
}

public String getApellidos() {
	return apellidos;
}

public void setApellidos(String apellidos) {
	this.apellidos = apellidos;
}

public String getEmail() {
	return email;
}

public void setEmail(String email) {
	this.email = email;
}

public String getTelefono() {
	return telefono;
}

public void setTelefono(String telefono) {
	this.telefono = telefono;
}

public String getDireccion() {
	return direccion;
}

public void setDireccion(String direccion) {
	this.direccion = direccion;
}

public Date getCreateAt() {
	return CreateAt;
}

public void setCreateAt(Date createAt) {
	CreateAt = createAt;
}

public void addFactura(Factura factura) {
	this.addFactura(factura);
}

public String getIdentificacion() {
	return identificacion;
}

public void setIdentificacion(String identificacion) {
	this.identificacion = identificacion;
}

public int getDiapago() {
	return diapago;
}

public void setDiapago(int diapago) {
	this.diapago = diapago;
}


}
