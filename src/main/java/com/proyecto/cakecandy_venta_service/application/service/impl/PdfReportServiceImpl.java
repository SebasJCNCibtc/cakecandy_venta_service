package com.proyecto.cakecandy_venta_service.application.service.impl;

import com.proyecto.cakecandy_venta_service.application.dto.DetalleVentaConProductoDto;
import com.proyecto.cakecandy_venta_service.application.dto.VentaDetalleDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.springframework.stereotype.Service;
import com.proyecto.cakecandy_venta_service.application.dto.VentaResponseDto;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportServiceImpl {

    public byte[] generateVentasReport(List<VentaResponseDto> ventas) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                addWatermark(contentStream, page);
                addHeader(contentStream, page);

                // Ajustar posición del título principal hacia abajo
                writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16, 50, 650, "Reporte de Historial de Ventas");
                // Ajustar posición de la tabla hacia abajo
                drawVentasTable(contentStream, ventas, 630);
                addFooter(contentStream, 1);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    public byte[] generateVentaDetailReport(VentaDetalleDto venta) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                addWatermark(contentStream, page);
                addHeader(contentStream, page);

                // Ajustar posiciones para el detalle de venta
                drawInfoCard(contentStream, venta);
                drawDetailTable(contentStream, venta);
                addFooter(contentStream, 1);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void drawVentasTable(PDPageContentStream contentStream, List<VentaResponseDto> ventas, float y) throws IOException {
        final float rowHeight = 20f;
        final float tableWidth = 500f;
        final float margin = 50;
        float[] colWidths = {60, 70, 140, 70, 160};

        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        contentStream.addRect(margin, y - rowHeight, tableWidth, rowHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.WHITE);

        String[] headers = {"ID Venta", "ID Usuario", "Fecha", "N° Items", "Total"};
        float textX = margin + 5;
        float textY = y - 15;
        for (int i = 0; i < headers.length; i++) {
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, textX, textY, headers[i]);
            textX += colWidths[i];
        }

        contentStream.setNonStrokingColor(Color.BLACK);
        textY -= rowHeight;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (VentaResponseDto venta : ventas) {
            textX = margin + 5;
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, String.valueOf(venta.getIdVenta()));
            textX += colWidths[0];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, String.valueOf(venta.getIdUsuario()));
            textX += colWidths[1];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, venta.getFechaVenta().format(formatter));
            textX += colWidths[2];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9, textX, textY, String.valueOf(venta.getDetalles().size()));
            textX += colWidths[3];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 9, textX, textY, "S/ " + venta.getTotal().toString());
            textY -= rowHeight;
        }
    }

    private void drawInfoCard(PDPageContentStream contentStream, VentaDetalleDto venta) throws IOException {
        float margin = 50;
        float cardWidth = 500;
        // Ajustar posición de la tarjeta de información hacia abajo
        float yStart = 650;

        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18, margin, yStart, "Detalle de Venta #" + venta.getIdVenta());
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, margin, yStart - 15, venta.getFechaVenta().format(DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy, hh:mm a")));
        contentStream.setStrokingColor(Color.LIGHT_GRAY);
        contentStream.moveTo(margin, yStart - 30);
        contentStream.lineTo(margin + cardWidth, yStart - 30);
        contentStream.stroke();
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, margin, yStart - 50, "CLIENTE");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, margin, yStart - 65, venta.getCliente().getNombre());
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, margin, yStart - 80, venta.getCliente().getEmail());
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, margin + 350, yStart - 50, "TOTAL PAGADO");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24, margin + 350, yStart - 75, "S/ " + venta.getTotal().toString());
    }

    private void drawDetailTable(PDPageContentStream contentStream, VentaDetalleDto venta) throws IOException {
        List<DetalleVentaConProductoDto> detalles = venta.getDetalles();
        float margin = 50;
        // Ajustar posición de la tabla de detalles hacia abajo
        float y = 480;
        float rowHeight = 20f;
        float tableWidth = 500f;
        float[] colWidths = {250, 80, 80, 90};

        // Dibuja el fondo de la cabecera
        contentStream.setNonStrokingColor(Color.decode("#F3F4F6"));
        contentStream.addRect(margin, y, tableWidth, rowHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK);

        // Escribe el texto de la cabecera
        String[] headers = {"Producto", "Cantidad", "P. Unitario", "Subtotal"};
        float textX = margin + 5;
        float textY = y + 5;
        for (int i = 0; i < headers.length; i++) {
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, textX, textY, headers[i]);
            textX += colWidths[i];
        }

        y -= rowHeight;

        for (DetalleVentaConProductoDto detalle : detalles) {
            contentStream.setStrokingColor(Color.LIGHT_GRAY);
            contentStream.moveTo(margin, y);
            contentStream.lineTo(margin + tableWidth, y);
            contentStream.stroke();

            textX = margin + 5;
            textY = y + 5;

            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, textX, textY, detalle.getNombreProducto());
            textX += colWidths[0];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, textX, textY, String.valueOf(detalle.getCantidad()));
            textX += colWidths[1];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, textX, textY, "S/ " + detalle.getPrecioUnitario().toString());
            textX += colWidths[2];
            writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, textX, textY, "S/ " + detalle.getSubtotal().toString());

            y -= rowHeight;
        }

        // Fila del TOTAL
        contentStream.setStrokingColor(Color.DARK_GRAY);
        contentStream.setLineWidth(1f);
        contentStream.moveTo(margin, y);
        contentStream.lineTo(margin + tableWidth, y);
        contentStream.stroke();

        textY = y + 5;
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, margin + colWidths[0] + colWidths[1] + 5, textY, "TOTAL:");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10, margin + colWidths[0] + colWidths[1] + colWidths[2] + 5, textY, "S/ " + venta.getTotal().toString());
    }

    private void addHeader(PDPageContentStream contentStream, PDPage page) throws IOException {
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        // Ajustar posición del header hacia abajo
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18, 50, 750, "Cake Candy");
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, 450, 750, "Fecha: " + fecha);
        contentStream.setStrokingColor(Color.DARK_GRAY);
        contentStream.setLineWidth(1.5f);
        contentStream.moveTo(50, 740);
        contentStream.lineTo(page.getMediaBox().getWidth() - 50, 740);
        contentStream.stroke();
    }

    private void addWatermark(PDPageContentStream contentStream, PDPage page) throws IOException {
        PDExtendedGraphicsState gs = new PDExtendedGraphicsState();
        gs.setNonStrokingAlphaConstant(0.08f);
        contentStream.setGraphicsStateParameters(gs);

        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 100);
        contentStream.setNonStrokingColor(Color.GRAY);

        contentStream.saveGraphicsState();
        // Ajustar posición del watermark para mejor centrado
        contentStream.transform(new org.apache.pdfbox.util.Matrix(
                (float) Math.cos(Math.toRadians(45)), (float) Math.sin(Math.toRadians(45)),
                -(float) Math.sin(Math.toRadians(45)), (float) Math.cos(Math.toRadians(45)),
                page.getMediaBox().getWidth() / 3, page.getMediaBox().getHeight() / 3));

        contentStream.beginText();
        contentStream.showText("Cake Candy");
        contentStream.endText();

        contentStream.restoreGraphicsState();

        // Restaurar estado gráfico
        gs.setNonStrokingAlphaConstant(1.0f);
        contentStream.setGraphicsStateParameters(gs);
        contentStream.setNonStrokingColor(Color.BLACK);
    }

    private void addFooter(PDPageContentStream contentStream, int pageNum) throws IOException {
        // Ajustar posición del footer
        writeText(contentStream, new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10, 270, 50, "Página " + pageNum);
    }

    private void writeText(PDPageContentStream stream, PDType1Font font, int fontSize, float x, float y, String text) throws IOException {
        stream.setFont(font, fontSize);
        stream.beginText();
        stream.newLineAtOffset(x, y);
        stream.showText(text != null ? text : "");
        stream.endText();
    }
}