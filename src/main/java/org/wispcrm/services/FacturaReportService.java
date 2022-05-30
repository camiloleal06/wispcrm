package org.wispcrm.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.wispcrm.utils.Conexion;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
public class FacturaReportService {

    private static final String FACTURA_ID = "factura_id";

     @Value("${invoice.template.path}")
    private String invoiceTemplate;

    @Value("${recibo.template.path}")
    private String reciboTemplate;

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
        report(id, cliente);
    }

    public void generateInvoiceFor(Integer id) throws IOException {
        File pdfFile = File.createTempFile("my-invoice", ".pdf");
        try (FileOutputStream pos = new FileOutputStream(pdfFile)) {
            final JasperReport report = loadTemplate();
            final Map<String, Object> parameters = parameters(id);
            final JasperPrint print = JasperFillManager.fillReport(report, parameters, new Conexion().conectar());
            JasperExportManager.exportReportToPdfFile(print, "Factura.pdf");
        } catch (final Exception e) {
            e.printStackTrace();

        }
    }

    private Map<String, Object> parameters(Integer id) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(FACTURA_ID, id);
        return parameters;
    }

    private JasperReport loadTemplate() throws JRException {
        final InputStream reportInputStream = getClass().getResourceAsStream(invoiceTemplate);
        final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private void report(Integer id, String cliente) throws JRException {
        final InputStream stream = this.getClass().getResourceAsStream(invoiceTemplate);
        JasperReport report = JasperCompileManager.compileReport(stream);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(FACTURA_ID, id);
        DataSource dataSource = jdbcTemplate.getDataSource();
            try (Connection connection = dataSource.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
                JasperExportManager.exportReportToPdfFile(print, cliente);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}
