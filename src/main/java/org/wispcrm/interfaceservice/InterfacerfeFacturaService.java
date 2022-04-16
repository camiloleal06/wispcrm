package org.wispcrm.interfaceservice;

import java.util.List;

import org.wispcrm.modelo.Factura;

public interface InterfacerfeFacturaService {

	public List<Factura> findAll();
	public Factura findFacturabyid(Integer id);

}
