package com.cvGen.gen.helper;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.ByteArrayOutputStream;

/**
 * Helper utilitaire pour la génération de PDF avec iText 7.
 */
public final class PdfHelper {

    private PdfHelper() {}

    /**
     * Génère un PDF très simple avec un titre et un contenu.
     *
     * @return un tableau d'octets représentant le PDF généré
     */
    public static byte[] generateSimplePdf(String titre, String contenu) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            

            if (titre != null && !titre.isBlank()) {
                document.add(new Paragraph(titre).setBold().setFontSize(18));
            }
            if (contenu != null && !contenu.isBlank()) {
                document.add(new Paragraph(contenu));
            } else {
                document.add(new Paragraph("Document PDF généré avec succès."));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            try {
                baos.reset();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);
                document.add(new Paragraph("Erreur lors de la génération du PDF:"));
                document.add(new Paragraph(String.valueOf(e.getMessage())));
                document.close();
                return baos.toByteArray();
            } catch (Exception ignored) {
                // En dernier recours, renvoyer un tableau vide
                return new byte[0];
            }
        }
    }

    /**
     * Génère un document avec header (optionnel), contenu et footer (optionnel).
     * Les sections header et footer sont ajoutées si fournies.
     *
     * @param header texte d'en-tête (optionnel)
     * @param content contenu principal (peut être null/blank)
     * @param footer texte de pied de page (optionnel)
     * @return bytes du PDF généré
     */
    public static byte[] generatePdfWithHeaderContentFooter(String header, String content, String footer) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Header (optionnel)
            if (header != null && !header.isBlank()) {
                document.add(new Paragraph(header).setBold().setFontSize(14));
                document.add(new Paragraph("")); // espacement
            }

            // Contenu principal
            if (content != null && !content.isBlank()) {
                document.add(new Paragraph(content));
            } else {
                document.add(new Paragraph("Contenu vide."));
            }

            // Footer (optionnel)
            if (footer != null && !footer.isBlank()) {
                document.add(new Paragraph("")); // espacement
                document.add(new Paragraph(footer).setFontSize(10).setItalic());
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            try {
                baos.reset();
                PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);
                document.add(new Paragraph("Erreur lors de la génération du PDF:"));
                document.add(new Paragraph(String.valueOf(e.getMessage())));
                document.close();
                return baos.toByteArray();
            } catch (Exception ignored) {
                return new byte[0];
            }
        }
    }
}
