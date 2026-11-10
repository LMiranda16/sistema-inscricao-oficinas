package api_oficina.com.example.oficina_api_demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api_oficina.com.example.oficina_api_demo.businessprocess.OficinaBusinessProcess;

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

	@GetMapping("/participantes/ordenar")
	public List<Map<String, Object>> listarParticipantesOrdenados() {
		return oficinaBusinessProcess.listarParticipantesOrdenados();
	}

	@GetMapping("/participantes/{id}")
	public Map<String, Object> buscarParticipante(@PathVariable Long id) {
		return oficinaBusinessProcess.buscarParticipante(id);
	}

	@PostMapping("/participantes")
	public ResponseEntity<Map<String, Object>> adicionarParticipante(@RequestBody NovoParticipante participante) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(oficinaBusinessProcess.adicionarParticipante(participante.nome(), participante.email()));
	}

	@DeleteMapping("/participantes/{id}")
	public ResponseEntity<Void> excluirParticipante(@PathVariable Long id) {
		oficinaBusinessProcess.excluirParticipante(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/oficinas")
	public List<Map<String, Object>> listarOficinas() {
		return oficinaBusinessProcess.listarOficinas();
	}

	@GetMapping("/oficinas/ordenar")
	public List<Map<String, Object>> listarOficinasOrdenadas() {
		return oficinaBusinessProcess.listarOficinasOrdenadas();
	}

	@GetMapping("/oficinas/{id}")
	public Map<String, Object> buscarOficina(@PathVariable Long id) {
		return oficinaBusinessProcess.buscarOficina(id);
	}

	@PostMapping("/oficinas")
	public ResponseEntity<Map<String, Object>> adicionarOficina(@RequestBody NovaOficina oficina) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(oficinaBusinessProcess.adicionarOficina(oficina.titulo(), oficina.capacidade()));
	}

	@DeleteMapping("/oficinas/{id}")
	public ResponseEntity<Void> excluirOficina(@PathVariable Long id) {
		oficinaBusinessProcess.excluirOficina(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/inscricoes")
	public List<Map<String, Object>> listarInscricoes() {
		return oficinaBusinessProcess.listarInscricoes();
	}

	@GetMapping("/inscricoes/ordenar")
	public List<Map<String, Object>> listarInscricoesOrdenadas() {
		return oficinaBusinessProcess.listarInscricoesOrdenadas();
	}

	@GetMapping("/inscricoes/{id}")
	public Map<String, Object> buscarInscricao(@PathVariable Long id) {
		return oficinaBusinessProcess.buscarInscricao(id);
	}

	@PostMapping("/inscricoes")
	public ResponseEntity<Map<String, Object>> adicionarInscricao(@RequestBody NovaInscricao inscricao) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(oficinaBusinessProcess.adicionarInscricao(inscricao.participanteId(), inscricao.oficinaId()));
	}

	@DeleteMapping("/inscricoes/{id}")
	public Map<String, Object> excluirInscricao(@PathVariable Long id) {
		return oficinaBusinessProcess.excluirInscricao(id);
	}
}
