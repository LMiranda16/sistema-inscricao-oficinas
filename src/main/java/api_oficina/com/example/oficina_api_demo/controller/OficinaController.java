package api_oficina.com.example.oficina_api_demo.controller;

import java.util.List;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api_oficina.com.example.oficina_api_demo.businessprocess.OficinaBusinessProcess;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
public class OficinaController {

	private final OficinaBusinessProcess oficinaBusinessProcess;

	public OficinaController(OficinaBusinessProcess oficinaBusinessProcess) {
		this.oficinaBusinessProcess = oficinaBusinessProcess;
	}

	@GetMapping("/participantes")
	public List<Map<String, Object>> listarParticipantes() {
		return oficinaBusinessProcess.listarParticipantes();
	}

	@GetMapping("/oficinas")
	public List<Map<String, Object>> listarOficinas() {
		return oficinaBusinessProcess.listarOficinas();
	}
	
	@GetMapping("/oficinas/{id}")
	public ResponseEntity<Map<String, Object>> buscarOficinaPorId(
	        @PathVariable("id") Long id) {

	    List<Map<String, Object>> oficinas =
	            oficinaBusinessProcess.buscarOficinaPorId(id);

	    if (oficinas.isEmpty()) {
	        return ResponseEntity.notFound().build();
	    }

	    return ResponseEntity.ok(oficinas.get(0));
	}

	@GetMapping("/inscricoes")
	public List<Map<String, Object>> listarInscricoes() {
		return oficinaBusinessProcess.listarInscricoes();
	}
}
