package org.wispcrm.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wispcrm.interfaceservice.InterfacePagoService;
import org.wispcrm.interfaces.InterfacePagos;
import org.wispcrm.modelo.Pago;

import javax.persistence.NoResultException;

@Service
public class PagoService implements InterfacePagoService {

	
	@Autowired
	InterfacePagos pagosDAO;
	
	@Override
	public List<Pago> findAll() {
		return (List<Pago>) pagosDAO.findAll();
	}

	@Override
	public void save(Pago pago) {
     pagosDAO.save(pago);
	}

	@Override
	public Pago findOne(Integer id) {
   	return pagosDAO.findById(id).orElseThrow(NoResultException::new);
	}

	@Override
	public void delete(Integer id) {
		pagosDAO.deleteById(id);
	}

}
