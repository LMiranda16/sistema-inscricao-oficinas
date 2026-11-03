package api_oficina.com.example.oficina_api_demo.businessprocess;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import api_oficina.com.example.oficina_api_demo.repository.InscricaoRepository;
import api_oficina.com.example.oficina_api_demo.repository.OficinaRepository;
import api_oficina.com.example.oficina_api_demo.repository.ParticipanteRepository;

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
	
	public List<Map<String, Object>> buscarOficinaPorId(Long id) {
	    return oficinaRepository.buscarPorId(id);
	}

	public List<Map<String, Object>> listarInscricoes() {
		return inscricaoRepository.listarTodos();
	}
}
