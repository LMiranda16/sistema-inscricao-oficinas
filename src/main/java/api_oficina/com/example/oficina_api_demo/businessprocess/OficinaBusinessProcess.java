package api_oficina.com.example.oficina_api_demo.businessprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import api_oficina.com.example.oficina_api_demo.repository.InscricaoRepository;
import api_oficina.com.example.oficina_api_demo.repository.OficinaRepository;
import api_oficina.com.example.oficina_api_demo.repository.ParticipanteRepository;
import api_oficina.com.example.oficina_api_demo.utils.Oficina;

@Service
public class OficinaBusinessProcess {

	private final ParticipanteRepository participanteRepository;
	private final OficinaRepository oficinaRepository;
	private final InscricaoRepository inscricaoRepository;

	public OficinaBusinessProcess(
			ParticipanteRepository participanteRepository,
			OficinaRepository oficinaRepository,
			InscricaoRepository inscricaoRepository) {
		this.participanteRepository = participanteRepository;
		this.oficinaRepository = oficinaRepository;
		this.inscricaoRepository = inscricaoRepository;
	}

	public List<Map<String, Object>> listarParticipantes() {
		return participanteRepository.listarTodos();
	}

	public List<Map<String, Object>> listarOficinas() {
		return oficinaRepository.listarTodos();
	}

	public List<Map<String, Object>> listarInscricoes() {
		return inscricaoRepository.listarTodos();
	}

	public List<Map<String, Object>> listarParticipantesOrdenados() {
		List<Map<String, Object>> participantes = new ArrayList<>(participanteRepository.listarTodos());
		Oficina.ordenarPorId(participantes, participante -> ((Number) participante.get("id")).longValue());
		return participantes;
	}

	public List<Map<String, Object>> listarOficinasOrdenadas() {
		List<Map<String, Object>> oficinas = new ArrayList<>(oficinaRepository.listarTodos());
		Oficina.ordenarPorId(oficinas, oficina -> ((Number) oficina.get("id")).longValue());
		return oficinas;
	}

	public List<Map<String, Object>> listarInscricoesOrdenadas() {
		List<Map<String, Object>> inscricoes = new ArrayList<>(inscricaoRepository.listarTodos());
		Oficina.ordenarPorId(inscricoes, inscricao -> ((Number) inscricao.get("id")).longValue());
		return inscricoes;
	}

	public Map<String, Object> buscarParticipante(Long id) {
		return participanteRepository.buscarPorId(id)
				.orElseThrow(() -> naoEncontrado("Participante"));
	}

	public Map<String, Object> adicionarParticipante(String nome, String email) {
		if (nome == null || nome.isBlank() || nome.length() > 120
				|| email == null || email.isBlank() || email.length() > 150) {
			throw requisicaoInvalida("Nome (ate 120 caracteres) e e-mail (ate 150) sao obrigatorios.");
		}
		return participanteRepository.adicionar(nome, email);
	}

	public void excluirParticipante(Long id) {
		if (!participanteRepository.excluir(id)) {
			throw naoEncontrado("Participante");
		}
	}

	public Map<String, Object> buscarOficina(Long id) {
		return oficinaRepository.buscarPorId(id)
				.orElseThrow(() -> naoEncontrado("Oficina"));
	}

	public Map<String, Object> adicionarOficina(String titulo, int capacidade) {
		if (titulo == null || titulo.isBlank() || titulo.length() > 150 || capacidade <= 0) {
			throw requisicaoInvalida("Titulo (ate 150 caracteres) e capacidade maior que zero sao obrigatorios.");
		}
		return oficinaRepository.adicionar(titulo, capacidade);
	}

	public void excluirOficina(Long id) {
		if (!oficinaRepository.excluir(id)) {
			throw naoEncontrado("Oficina");
		}
	}

	@Transactional
	public Map<String, Object> atualizarOficina(Long id, String titulo, Integer capacidade) {
		if (titulo == null || titulo.isBlank() || titulo.length() > 150 || capacidade == null || capacidade <= 0) {
			throw requisicaoInvalida("Titulo e capacidade valida sao obrigatorios.");
		}
		Map<String, Object> oficina = oficinaRepository.buscarPorIdComBloqueio(id)
				.orElseThrow(() -> naoEncontrado("Oficina"));
		int anterior = ((Number) oficina.get("capacidade")).intValue();
		boolean historico = oficinaRepository.temHistorico(id);
		if (historico && anterior != capacidade) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Nao e possivel alterar a capacidade de oficina com historico.");
		}
		int vagas = historico ? ((Number) oficina.get("vagas_disponiveis")).intValue() : capacidade;
		return oficinaRepository.atualizar(id, titulo.trim(), capacidade, vagas);
	}

	public Map<String, Object> buscarInscricao(Long id) {
		return inscricaoRepository.buscarPorId(id)
				.orElseThrow(() -> naoEncontrado("Inscricao"));
	}

	@Transactional
	public Map<String, Object> adicionarInscricao(Long participanteId, Long oficinaId) {
		if (participanteId == null || oficinaId == null) {
			throw requisicaoInvalida("Participante e oficina sao obrigatorios.");
		}
		participanteRepository.buscarPorId(participanteId)
				.orElseThrow(() -> naoEncontrado("Participante"));
		oficinaRepository.buscarPorIdComBloqueio(oficinaId)
				.orElseThrow(() -> naoEncontrado("Oficina"));
		if (inscricaoRepository.existeAtiva(participanteId, oficinaId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Participante ja inscrito nesta oficina.");
		}
		if (!oficinaRepository.consumirVaga(oficinaId)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A oficina nao tem vagas disponiveis.");
		}
		return inscricaoRepository.adicionar(participanteId, oficinaId);
	}

	@Transactional
	public Map<String, Object> excluirInscricao(Long id) {
		Map<String, Object> inscricao = buscarInscricao(id);
		if (!"ATIVA".equals(inscricao.get("situacao"))) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A inscricao ja esta cancelada.");
		}
		if (!inscricaoRepository.cancelar(id)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A inscricao ja foi cancelada.");
		}
		oficinaRepository.devolverVaga(((Number) inscricao.get("oficina_id")).longValue());
		return buscarInscricao(id);
	}

	private ResponseStatusException naoEncontrado(String recurso) {
		return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " nao encontrado.");
	}

	private ResponseStatusException requisicaoInvalida(String mensagem) {
		return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
	}
}
