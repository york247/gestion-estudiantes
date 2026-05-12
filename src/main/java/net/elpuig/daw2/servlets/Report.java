package net.elpuig.daw2.servlets;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.HashMap;

public class Report extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Guardamos los parámetros ANTES de entrar al hilo async
        // porque el request puede no estar disponible después
        String tipoInforme = request.getParameter("optInformes");
        if (tipoInforme == null || tipoInforme.isBlank()) tipoInforme = "application/pdf";
        final String tipoFinal = tipoInforme;

        final AsyncContext ctx = request.startAsync(request, response);
        ctx.setTimeout(30_000);

        ctx.start(() -> {
            try {
                // Compilar JRXML
                JasperReport jasperReport;
                try (InputStream jrxml = getServletContext()
                        .getResourceAsStream("/WEB-INF/informes/alumnos/Alumnos.jrxml")) {
                    if (jrxml == null)
                        throw new IllegalStateException("No se encuentra Alumnos.jrxml");
                    jasperReport = JasperCompileManager.compileReport(jrxml);
                }

                // Rellenar con BD
                JasperPrint jasperPrint;
                try (Connection cn = ConsultaServlet.getConexion()) {
                    jasperPrint = JasperFillManager.fillReport(
                            jasperReport,
                            new HashMap<String, Object>(),
                            cn
                    );
                }

                // Obtener el response del contexto async
                HttpServletResponse resp = (HttpServletResponse) ctx.getResponse();
                resp.setContentType(tipoFinal);

                if ("application/pdf".equals(tipoFinal)) {
                    resp.setHeader("Content-Disposition", "inline; filename=informe.pdf");
                    try (OutputStream os = resp.getOutputStream()) {
                        JRPdfExporter exporter = new JRPdfExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
                        exporter.setConfiguration(new SimplePdfExporterConfiguration());
                        exporter.exportReport();
                    }

                } else if ("application/vnd.ms-excel".equals(tipoFinal)) {
                    resp.setHeader("Content-Disposition", "inline; filename=informe.xls");
                    try (OutputStream os = resp.getOutputStream()) {
                        JRXlsExporter exporter = new JRXlsExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
                        exporter.setConfiguration(new SimpleXlsReportConfiguration());
                        exporter.exportReport();
                    }

                } else if ("application/msword".equals(tipoFinal)) {
                    resp.setHeader("Content-Disposition", "inline; filename=informe.doc");
                    try (OutputStream os = resp.getOutputStream()) {
                        JRDocxExporter exporter = new JRDocxExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
                        exporter.exportReport();
                    }

                } else {
                    // HTML
                    HttpServletRequest req = (HttpServletRequest) ctx.getRequest();
                    req.getSession().setAttribute(
                            ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE,
                            jasperPrint
                    );
                    try (OutputStream os = resp.getOutputStream()) {
                        HtmlExporter exporter = new HtmlExporter();
                        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                        SimpleHtmlExporterOutput out = new SimpleHtmlExporterOutput(os);
                        out.setImageHandler(new WebHtmlResourceHandler("image?image={0}"));
                        exporter.setExporterOutput(out);
                        exporter.exportReport();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    HttpServletResponse resp = (HttpServletResponse) ctx.getResponse();
                    if (!resp.isCommitted()) {
                        resp.sendError(500, "Error generando informe: " + e.getMessage());
                    }
                } catch (IOException ignored) {}
            } finally {
                ctx.complete();
            }
        });
    }
}