package api_oficina.com.example.oficina_api_demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api_oficina.com.example.oficina_api_demo.businessprocess.OficinaBusinessProcess;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

	@GetMapping("/inscricoes")
	public List<Map<String, Object>> listarInscricoes() {
		return oficinaBusinessProcess.listarInscricoes();
	}
	@PostMapping("/inscricoes")
	public ResponseEntity<Void> registrarInscricao(
	        @RequestBody Map<String, Long> dados) {

	    Long participanteId = dados.get("participante_id");
	    Long oficinaId = dados.get("oficina_id");

	    oficinaBusinessProcess.registrarInscricao(participanteId, oficinaId);

	    return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
