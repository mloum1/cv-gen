package com.cvGen.gen.service;

import com.cvGen.gen.helper.PdfHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenerationPDfService {

	/**
	 * Génère un PDF d'exemple en utilisant le helper iText.
	 * @return bytes du PDF
	 */
	@Transactional(readOnly = true)
	public byte[] genererPdfg() {
		String titre = "Exemple de CV";
		String contenu = "Ceci est un PDF généré par le service via PdfHelper.";
		return PdfHelper.generateSimplePdf(titre, contenu);
	}

	/**
	 * Génère un PDF avec header (optionnel), contenu et footer (optionnel).
	 */
	@Transactional(readOnly = true)
	public byte[] genererPdf(String header, String content, String footer) {
		return PdfHelper.generatePdfWithHeaderContentFooter(header, content, footer);
	}
}
