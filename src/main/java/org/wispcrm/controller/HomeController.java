package org.wispcrm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.wispcrm.daos.ClienteDao;
import org.wispcrm.daos.InterfaceFacturas;
import org.wispcrm.daos.InterfacePagos;
import org.wispcrm.utils.Util;

@Controller
public class HomeController {
    @Autowired
    private InterfaceFacturas facturaDao;

    @Autowired
    ClienteDao clienteDao;

    @Autowired
    InterfacePagos pagosDao;

    @GetMapping("/")
    public String home(Model modelo) {
        modelo.addAttribute("numeroclientes", clienteDao.totalClientesActivos());
        modelo.addAttribute("totalpendientemes", Util.formatearMoneda(facturaDao.totalFacturasPendientesMes()));
        modelo.addAttribute("cantidadfacturaspendientesmes", facturaDao.totalCantidadFacturasMes());
        modelo.addAttribute("totalpagadasmes", Util.formatearMoneda(facturaDao.totalFacturasPagadasMes()));
         modelo.addAttribute("totalpendientehistorico",
                Util.formatearMoneda(facturaDao.totalFacturasPendientesHistorico()));
        modelo.addAttribute("cantidadfacturaspendienteshistorico", facturaDao.totalCantidadFacturasHistorico());
        modelo.addAttribute("totalpagadashistorico", Util.formatearMoneda(facturaDao.totalFacturasPagadasHistorico()));
        return "home";
    }

}
