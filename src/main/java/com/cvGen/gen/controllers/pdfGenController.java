package com.cvGen.gen.controllers;

import com.cvGen.gen.service.GenerationPDfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class pdfGenController {

	private final GenerationPDfService generationPDfService;

	public pdfGenController(GenerationPDfService generationPDfService) {
		this.generationPDfService = generationPDfService;
	}

	@GetMapping(value = "generate", produces = "application/pdf")
	public ResponseEntity<byte[]> generateGenericPdf() {
		PdfRequest request = new PdfRequest("Header sous forme de tableau  pour les infos || QR code pour le portfolio", "This is testing content", "Empty not", "Test you");
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

	public static record PdfRequest(String header, String content, String footer, String filename) {}
}
