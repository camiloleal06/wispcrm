package org.wispcrm.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wispcrm.interfaceService.InterfacePagoService;
import org.wispcrm.interfaces.InterfacePagos;
import org.wispcrm.modelo.Pago;

@Service
public class PagoService implements InterfacePagoService {

	
	@Autowired
	InterfacePagos pagosDAO;
	
	@Override
	public List<Pago> findAll() {
		// TODO Auto-generated method stub
		return (List<Pago>) pagosDAO.findAll();
	}

	@Override
	public Page<Pago> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(Pago pago) {

     pagosDAO.save(pago);		
	}

	@Override
	public Pago findOne(Integer id) {
		// TODO Auto-generated method stub
		return pagosDAO.findById(id).orElse(null);
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		pagosDAO.deleteById(id);	
	}

}
