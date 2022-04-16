package org.wispcrm.interfaceservice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wispcrm.modelo.Pago;

public interface InterfacePagoService {
	public List<Pago> findAll();

	public Page<Pago> findAll(Pageable pageable);

	public void save(Pago pago);

	public Pago findOne(Integer id);

	public void delete(Integer id);
}
