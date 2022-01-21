package org.wispcrm.services;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wispcrm.utils.Conexion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FacturaReportService {

	@Value("${invoice.logo.path}")
	private String logo_path;

	@Value("${invoice.template.path}")
	private String invoice_template;

	@Value("${recibo.template.path}")
	private String recibo_template;
	
	@Autowired
	@Qualifier("jdbcTemplate")
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public JasperPrint DescargarPdfFile(Integer id) throws JRException, IOException {
		InputStream stream = this.getClass().getResourceAsStream(invoice_template);
		JasperReport report = JasperCompileManager.compileReport(stream);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("factura_id", id);

		try (Connection connection = jdbcTemplate.getDataSource().getConnection();) {
			JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
			return print;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	@Transactional
	public JasperPrint DescargarPagoFile(Integer id) throws JRException, IOException {
		InputStream stream = this.getClass().getResourceAsStream(recibo_template);
		JasperReport report = JasperCompileManager.compileReport(stream);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("factura_id", id);

		try (Connection connection = jdbcTemplate.getDataSource().getConnection();) {
			JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);
			return print;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	

	public void createPdfReport(Integer id, String cliente) throws JRException {
		final InputStream stream = this.getClass().getResourceAsStream(invoice_template);

		JasperReport report = JasperCompileManager.compileReport(stream);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("factura_id", id);

		try (Connection connection = jdbcTemplate.getDataSource().getConnection();) {

			JasperPrint print;
			print = JasperFillManager.fillReport(report, parameters, connection);
			JasperExportManager.exportReportToPdfFile(print, cliente);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void PagoPdfReport(Integer id, String cliente) throws JRException {
		final InputStream stream = this.getClass().getResourceAsStream(recibo_template);

		JasperReport report = JasperCompileManager.compileReport(stream);
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("factura_id", id);

		try (Connection connection = jdbcTemplate.getDataSource().getConnection();) {

			JasperPrint print;
			print = JasperFillManager.fillReport(report, parameters, connection);
			JasperExportManager.exportReportToPdfFile(print, cliente);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void generateInvoiceFor(Integer id) throws IOException {

		File pdfFile = File.createTempFile("my-invoice", ".pdf");

		/// logger.info(String.format("Invoice pdf path : %s",
		/// pdfFile.getAbsolutePath()));

		try (FileOutputStream pos = new FileOutputStream(pdfFile)) {
			// Load invoice JRXML template.
			final JasperReport report = loadTemplate();

			// Fill parameters map.
			final Map<String, Object> parameters = parameters(id);

			// Create an empty datasource.

			// JRBeanCollectionDataSource dataSource= new
			// JRBeanCollectionDataSource(FacturaCliente);
			// JRBeanCollectionDataSource dataSource = new
			// JRBeanCollectionDataSource(Collections.singletonList("Invoice"));
			final JasperPrint print = JasperFillManager.fillReport(report, parameters, new Conexion().conectar());
			// JasperReportsUtils.as
			JasperExportManager.exportReportToPdfFile(print, "Factura.pdf");

			// Render the invoice as a PDF file."
			// final JasperPrint print = JasperFillManager.fillReport(report, parameters,
			// new Conexion().conectar(),pos);

			// return file.
			// return pdfFile;
		} catch (final Exception e) {
			// logger.error(String.format("An error occured during PDF creation: %s", e));
			throw new RuntimeException(e);
		}
	}

	private Map<String, Object> parameters(Integer id) {
		final Map<String, Object> parameters = new HashMap<>();
		// parameters.put("logo", getClass().getResourceAsStream(logo_path));
		parameters.put("factura_id", id);
		// parameters.put("REPORT_LOCALE", locale);
		return parameters;
	}

	private JasperReport loadTemplate() throws JRException {

		// logger.info(String.format("Invoice template path : %s", invoice_template));

		final InputStream reportInputStream = getClass().getResourceAsStream(invoice_template);
		final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);

		return JasperCompileManager.compileReport(jasperDesign);
	}
}
