package org.wispcrm.services;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.wispcrm.utils.ConstantMensaje;

@Service
public class FacturaReportService {

    private static final String FACTURA_ID = "factura_id";
    private static final String ORDER_ID = "orden_id";

    @Value("${invoice.template.path}")
    private String invoiceTemplate;

    @Value("${recibo.template.path}")
    private String reciboTemplate;

    @Value("${orden.template.path}")
    private String ordenTemplate;

    @Qualifier("jdbcTemplate")
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public JasperPrint descargarPdfFile(Integer id) throws JRException, SQLException {
        InputStream stream = this.getClass().getResourceAsStream(invoiceTemplate);
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(FACTURA_ID, id);
        DataSource dataSource = jdbcTemplate.getDataSource();
        return JasperFillManager.fillReport(report, parameters, dataSource.getConnection());
    }

    @Transactional
    public JasperPrint ordenDeServicioPdfFile(Integer id) throws JRException, SQLException {
        InputStream stream = this.getClass().getResourceAsStream(ordenTemplate);
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ORDER_ID, id);
        DataSource dataSource = jdbcTemplate.getDataSource();
        return JasperFillManager.fillReport(report, parameters, dataSource.getConnection());
    }

    @Transactional
    public JasperPrint descargarPagoFile(Integer id) throws JRException {
        InputStream stream = this.getClass().getResourceAsStream(reciboTemplate);
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(FACTURA_ID, id);
        DataSource dataSource = jdbcTemplate.getDataSource();
        if (dataSource != null) {
            try (Connection connection = dataSource.getConnection()) {
                return JasperFillManager.fillReport(report, parameters, connection);
            } catch (SQLException e) {

                e.printStackTrace();
            }
        }
        return null;

    }

    public void createPdfReport(Integer id, String cliente) throws JRException {
        report(id, cliente);
    }

    public void pagoPdfReport(Integer id, String cliente) throws JRException {
        pagoReport(id, cliente);
    }

    public void ordenPdfReport(Integer id, String cliente, String parametro) throws JRException {
        report(id, cliente, parametro);
    }

    @Transactional
    private void report(Integer id, String cliente) throws JRException {
        final InputStream stream = this.getClass().getResourceAsStream(invoiceTemplate);
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(FACTURA_ID, id);
        DataSource dataSource = jdbcTemplate.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
            JasperExportManager.exportReportToPdfFile(print, ConstantMensaje.RUTA_SERVER_REPORT +cliente);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void pagoReport(Integer id, String cliente) throws JRException {
        final InputStream stream = this.getClass().getResourceAsStream(reciboTemplate);
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(FACTURA_ID, id);
        DataSource dataSource = jdbcTemplate.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
            JasperExportManager.exportReportToPdfFile(print, ConstantMensaje.RUTA_SERVER_REPORT +cliente);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    private void report(Integer id, String cliente, String parametro) throws JRException {
        final InputStream stream = this.getClass().getResourceAsStream(ordenTemplate);
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(parametro, id);
        DataSource dataSource = jdbcTemplate.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
            JasperExportManager.exportReportToPdfFile(print, ConstantMensaje.RUTA_SERVER_REPORT +cliente);
                } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
