package org.wispcrm.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.modelo.EditarClienteDTO;
import org.wispcrm.modelo.Factura;

public interface ClienteInterface {

    List<Cliente> findAll();

    Page<Cliente> findAll(Pageable pageable);

    void save(Cliente cliente);

    Cliente findOne(Integer id);

    void delete(Integer id);

    void saveFactura(Factura factura);

    List<ClienteDTO> listaClientes();

    Page<ClienteDTO> listaPageable(Pageable pageable);

    Cliente findById(Integer id);

    Cliente findFirstClienteByIdentificacion(String identificacion);

    Cliente findFirstClienteByEmail(String email);

    Cliente findFirstClienteByTelefono(String telefono);

    Cliente findFirstClienteByDiapago(int diapago);

    EditarClienteDTO editarCliente(Integer id);

}
