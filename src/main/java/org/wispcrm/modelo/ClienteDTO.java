package org.wispcrm.modelo;

public class ClienteDTO {
	private int id;
	private String identificacion;
	private String nombres;
	private String email;
	private String telefono;
	private double valor;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}
	public String getNombres() {
		return nombres;
	}
	public void setNombres(String nombres) {
		this.nombres = nombres;
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
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public ClienteDTO(int id, String identificacion, String nombres, String email, String telefono, double valor) {
		super();
		this.id = id;
		this.identificacion = identificacion;
		this.nombres = nombres;
		this.email = email;
		this.telefono = telefono;
		this.valor = valor;
	}
	
	
	
	
}
