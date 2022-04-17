package org.wispcrm.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wispcrm.interfaceservice.InterfacerfeFacturaService;
import org.wispcrm.interfaces.InterfaceFacturas;
import org.wispcrm.modelo.Factura;

@Service
public class FacturaServices implements InterfacerfeFacturaService {
	@Autowired
	private InterfaceFacturas facturaDao;

	@Override
	public List<Factura> findAll() {
	
		return  (List<Factura>) facturaDao.findAll();
	}

	@Override
	public Factura findFacturabyid(Integer id) {
			return facturaDao.findById(id).orElse(null);
	}
	

}
