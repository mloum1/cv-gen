package com.cvGen.gen.controllers;

import com.cvGen.gen.service.GenerationPDfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class pdfGenController {

	private final GenerationPDfService generationPDfService;

	@Autowired
	public pdfGenController(GenerationPDfService generationPDfService) {
		this.generationPDfService = generationPDfService;
	}

	/**
	 * Endpoint de test simple.
	 */
	@GetMapping(value = "generer-pdf")
	public String genererPDF() {
		return "Test du controller ";
	}

	/**
	 * Exemple de génération d'un PDF de démonstration (utilise un contenu par défaut).
	 */
	@GetMapping(value = "/generer-pdf-t", produces = "application/pdf")
	public HttpEntity<byte[]> genererPDFT() {
		byte[] bytes = generationPDfService.genererPdfg();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentLength(bytes.length);
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=exemple.pdf");
		return new HttpEntity<>(bytes, headers);
	}

	/**
	 * Endpoint générique: génère un PDF à partir d'un header (optionnel), contenu, et footer (optionnel).
	 * Permet d'utiliser le service PDF pour tout type de document, pas uniquement des CV.
	 */
	@PostMapping(value = "/api/pdf/generate", produces = "application/pdf")
	public ResponseEntity<byte[]> generateGenericPdf(@RequestBody PdfRequest request) {
		byte[] bytes = generationPDfService.genererPdf(request.header(), request.content(), request.footer());
		String filename = request.filename() == null || request.filename().isBlank() ? "document.pdf" : ensurePdfExtension(request.filename());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentLength(bytes.length);
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		return ResponseEntity.ok().headers(headers).body(bytes);
	}

	private static String ensurePdfExtension(String name) {
		String trimmed = name.trim();
		if (!trimmed.toLowerCase().endsWith(".pdf")) {
			return trimmed + ".pdf";
		}
		return trimmed;
	}

	// Modèle de requête minimal pour éviter de créer une nouvelle classe (Java 21 record)
	public static record PdfRequest(String header, String content, String footer, String filename) {}
}
