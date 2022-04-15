package org.wispcrm.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.wispcrm.interfaceService.InterfaceClienteService;
import org.wispcrm.interfaces.ClienteRepository;
import org.wispcrm.interfaces.InterfaceClientes;
import org.wispcrm.interfaces.InterfaceFacturas;
import org.wispcrm.modelo.Cliente;
import org.wispcrm.modelo.ClienteDTO;
import org.wispcrm.modelo.Factura;

@Service
public class ClienteServices implements InterfaceClienteService {

    @Autowired
    private InterfaceClientes clienteDao;

    @Autowired
    private ClienteRepository clienteRepo;

    @Autowired
    private InterfaceFacturas facturaDao;

    @Override
    public List<Cliente> findAll() {
        return (List<Cliente>) clienteDao.findAll();
    }

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public void save(Cliente cliente) {
        clienteDao.save(cliente);

    }

    @Override
    public Cliente findOne(Integer id) {
        return clienteDao.findById(id).orElse(null);
    }

    @Override
    public void delete(Integer id) {
        clienteDao.deleteById(id);
    }

    @Override
    public void saveFactura(Factura factura) {

        facturaDao.save(factura);

    }

    @Override
    public List<ClienteDTO> listaClientes() {

        return clienteRepo.lista();

    }

    @Override
    public Page<ClienteDTO> lista(Pageable pageable) {
        return clienteRepo.lista(pageable);
    }

}
