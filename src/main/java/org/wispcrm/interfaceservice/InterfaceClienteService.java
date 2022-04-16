package org.wispcrm.interfaceservice;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.modelo.Factura;


public interface InterfaceClienteService {

	public List<Cliente> findAll();

	public Page<Cliente> findAll(Pageable pageable);

	public void save(Cliente cliente);

	public Cliente findOne(Integer id);

	public void delete(Integer id);

	public void saveFactura(Factura factura);
	
	public List<ClienteDTO> listaClientes();
	
	public Page<ClienteDTO> lista(Pageable pageable);
}
