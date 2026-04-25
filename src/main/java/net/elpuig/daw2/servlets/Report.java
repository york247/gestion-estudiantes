package net.elpuig.daw2.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;

public class Report extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final AsyncContext ctxAsincrono = request.startAsync();
        ctxAsincrono.setTimeout(10_000); // 10 segundos como maximo para generar el report
        ctxAsincrono.addListener(new AsyncListener() {
            public void onComplete(AsyncEvent event) throws IOException {
                System.out.println("Informe generado");
            }

            public void onTimeout(AsyncEvent event) throws IOException {
                System.out.println("Se ha excedido el tiempo máximo para generar el informe");
            }

            public void onError(AsyncEvent event) throws IOException {
                System.out.println(
                        "Se ha producido un error al generar el informe: " + event.getThrowable().getMessage());
            }

            public void onStartAsync(AsyncEvent event) throws IOException {
            }
        });

        ctxAsincrono.start(new Runnable() {

            @Override
            public void run() {
                try {
                    // Cargar el informe compilado desde el WAR
                    InputStream is = getServletContext()
                            .getResourceAsStream("/WEB-INF/informes/alumnos/Alumnos.jasper");
                    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is);

                    // Actualizarlo con los datos de la BD usando la conexion de ConsultaServlet
                    try (Connection cn = ConsultaServlet.getConexion()) {
                        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                                new HashMap<String, Object>(0), cn);

                        /*
                         * Ahora ejecutamos un metodo especifico segun el informe que haya seleccionado
                         * el usuario
                         */

                        // Obtener el tipo de informe seleccionado por el usuario
                        String tipoInforme = request.getParameter("optInformes");

                        response.setContentType(tipoInforme);

                        if ("application/pdf".equals(tipoInforme)) {

                            JRPdfExporter exporter = new JRPdfExporter();

                            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                            exporter.setConfiguration(configuration);

                            exporter.exportReport();

                        } else if ("application/vnd.ms-excel".equals(tipoInforme)) {

                            response.setHeader("Content-Disposition", "inline; filename=informe.xls");
                            JRXlsExporter exporter = new JRXlsExporter();

                            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));

                            SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
                            exporter.setConfiguration(configuration);

                            exporter.exportReport();

                        } else if ("application/msword".equals(tipoInforme)) {

                            response.setHeader("Content-Disposition", "inline; filename=informe.doc");
                            JRDocxExporter exporter = new JRDocxExporter();

                            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));

                            exporter.exportReport();

                        } else {

                            request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE,
                                    jasperPrint);

                            HtmlExporter exporter = new HtmlExporter();

                            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

                            SimpleHtmlExporterOutput exporterOutput = new SimpleHtmlExporterOutput(
                                    response.getOutputStream());
                            exporterOutput.setImageHandler(new WebHtmlResourceHandler("image?image={0}"));

                            exporter.setExporterOutput(exporterOutput);

                            exporter.exportReport();
                        }
                    }

                } catch (JRException | IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    ctxAsincrono.complete();
                }
            }

        });

    }
}
