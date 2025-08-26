package com.cvGen.gen.helper;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import java.awt.*;
import java.io.ByteArrayOutputStream;

/**
 * Helper utilitaire pour la génération de PDF avec iText 7.
 */
public final class PdfHelper {

    private PdfHelper() {}

    /**
     * Génère un pdf.
     *
     * @param header L'en-tête du pdf à génèrer.
     * @param content Le contenu du pdf à génèrer.
     * @param footer Le footer du pdf à génèrer.
     * @return un tableau de byte.
     */
    public static byte[] generatePdfWithHeaderContentFooter(String header, String content, String footer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            float topMargin = 72f;
            float bottomMargin = 54f;
            // définition des marges de chaque document.
            document.setMargins(topMargin + 10f, 36f, bottomMargin + 10f, 36f);

            // définition d'un box pour stocker le total des pages dans le footer
            PdfFormXObject totalPagePlaceholder = new PdfFormXObject(new Rectangle(0, 0, 30, 12));

            // gestion de l'insertion du header, du footer et le total de générer dans le footer
            HeaderFooterHandler handler = new HeaderFooterHandler(header, footer, totalPagePlaceholder);
            pdfDoc.addEventHandler(PdfDocumentEvent.START_PAGE, handler);
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, handler);

            // Contenu principal
            if (content != null && !content.isBlank()) {
                document.add(new Paragraph(content));
            } else {
                document.add(new Paragraph("Contenu vide."));
            }
            handler.writeTotalPages(pdfDoc);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            try {
                baos.reset();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);
                document.add(new Paragraph("Erreur lors de la génération du PDF:"));
                System.out.println(e.getMessage());
                document.add(new Paragraph(String.valueOf(e.getMessage())));
                document.close();
                return baos.toByteArray();
            } catch (Exception ignored) {
                return new byte[0];
            }
        }
    }

    /**
     * Handler iText 7 pour dessiner un header au START_PAGE et un footer au END_PAGE.
     */
    private static class HeaderFooterHandler implements IEventHandler {
        private final String header; // TODO : Update du header pour un table au lieu de string, pareil pour le footer pour qu'il englobe  le total Pages.
        private final String footer;
        private final PdfFormXObject totalPagePlaceholder;

        HeaderFooterHandler(String header, String footer, PdfFormXObject totalPagePlaceholder) {
            this.header = (header != null && !header.isBlank()) ? header : null;
            this.footer = (footer != null && !footer.isBlank()) ? footer : null;
            this.totalPagePlaceholder = totalPagePlaceholder;
        }

        @Override
        public void handleEvent(Event event) {
            // vars
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            int pageNumber = pdfDoc.getPageNumber(page);
            Rectangle pageSize = page.getPageSize();

            // margins
            float left = 36f;
            float right = pageSize.getWidth() - left;
            float topY = pageSize.getTop() - left;
            float bottomY = pageSize.getBottom() + 30f;

            // Styles pour le header et le footer
            Color color = new DeviceRgb(0, 255, 255);

            // TODO: START_PAGE: header
            if (PdfDocumentEvent.START_PAGE.equals(docEvent.getType())) {
                if (header != null) {
                    Canvas canvas = new Canvas(page, page.getPageSize());
                    Paragraph p = new Paragraph(header).setBold().setFontSize(12).setTextAlignment(TextAlignment.CENTER);
                    canvas.showTextAligned(p, pageSize.getWidth() / 2, topY, TextAlignment.CENTER);
                    canvas.close();
                }
            }

            // TODO: END_PAGE: footer + pagination
            if (PdfDocumentEvent.END_PAGE.equals(docEvent.getType())) {
                Canvas canvas = new Canvas(page, page.getPageSize());

                if (footer != null) {
                    Paragraph f = new Paragraph(footer).setFontSize(9).setTextAlignment(TextAlignment.CENTER);
                    canvas.showTextAligned(f, pageSize.getWidth() / 2, bottomY + 12, TextAlignment.CENTER);
                }

                // Pagination "Page X / Y".
                String pageText = "Page " + pageNumber + " / ";
                Paragraph p = new Paragraph(pageText).setFontSize(9).setBackgroundColor(color);
                float textWidthX = 50f;
                float x = right - textWidthX;
                canvas.showTextAligned(p, x, bottomY, TextAlignment.LEFT);
                new PdfCanvas(page).addXObjectAt(totalPagePlaceholder, x + 35f, bottomY - 2f);
                canvas.close();
            }
        }

        /**
         * Ecrit le nombre total de pages générés.
         *
         * @param pdfDoc le pdf.
         */
        void writeTotalPages(PdfDocument pdfDoc) {
            int total = pdfDoc.getNumberOfPages();
            Canvas canvas = new Canvas(totalPagePlaceholder, pdfDoc);
            canvas.showTextAligned(new Paragraph(String.valueOf(total)).setFontSize(9), 0, 0, TextAlignment.LEFT);
            canvas.close();
        }
    }
}
