package com.cvGen.gen.service;

import com.cvGen.gen.helper.PdfHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GenerationPDfService {

	/**
	 * Génère un pdf.
	 *
	 * @param header L'en-tête du pdf à générer.
	 * @param content Le contenu du pdf à génèrer.
	 * @param footer Le footer du pdf à générer si jamais on souhaite un footer.
	 * @return un tableau de byte.
	 */
	@Transactional(readOnly = true)
	public byte[] genererPdf(String header, String content, String footer) {
		return PdfHelper.generatePdfWithHeaderContentFooter(header, content, footer);
	}
}
