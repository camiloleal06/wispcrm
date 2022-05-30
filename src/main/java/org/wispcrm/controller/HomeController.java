package org.wispcrm.controller;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.wispcrm.daos.ClienteDao;
import org.wispcrm.daos.InterfaceFacturas;
import org.wispcrm.daos.InterfacePagos;

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
        modelo.addAttribute("numeroclientes", clienteDao.count());
        modelo.addAttribute("totalpendientemes", formatearMoneda(facturaDao.totalFacturasPendientesMes()));
        modelo.addAttribute("cantidadfacturaspendientesmes", facturaDao.totalCantidadFacturasMes());
        modelo.addAttribute("totalpagadasmes", formatearMoneda(facturaDao.totalFacturasPagadasMes()));

        modelo.addAttribute("numeroclientes", clienteDao.count());
        modelo.addAttribute("totalpendientehistorico", formatearMoneda(facturaDao.totalFacturasPendientesHistorico()));
        modelo.addAttribute("cantidadfacturaspendienteshistorico", facturaDao.totalCantidadFacturasHistorico());
        modelo.addAttribute("totalpagadashistorico", formatearMoneda(facturaDao.totalFacturasPagadasHistorico()));

        return "home";
    }

    private Object formatearMoneda(Long numero) {
        Locale locale = new Locale("es", "CO");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        if (null != numero) {
            return currencyFormatter.format(numero.doubleValue());
        } else {
            return currencyFormatter.format(0.0);
        }
    }
}
