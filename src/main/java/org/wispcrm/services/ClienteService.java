package org.wispcrm.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wispcrm.interfaces.ClienteRepository;
import org.wispcrm.modelo.Cliente;

@Service
public class ClienteService {

	@Autowired
private ClienteRepository repo;

public List<Cliente> listAll(String keyword) {
if (keyword != null) {
return repo.search(keyword);
}
return repo.search("a").subList(0,10);
   
}

}