package api_oficina.com.example.oficina_api_demo.businessprocess;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import api_oficina.com.example.oficina_api_demo.repository.InscricaoRepository;
import api_oficina.com.example.oficina_api_demo.repository.OficinaRepository;
import api_oficina.com.example.oficina_api_demo.repository.ParticipanteRepository;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
	@Transactional
	public void registrarInscricao(Long participanteId, Long oficinaId) {
	    if (participanteId == null || oficinaId == null) {
	        throw new ResponseStatusException(
	                HttpStatus.BAD_REQUEST, "Informe o participante e a oficina.");
	    }

	    if (!participanteRepository.existePorId(participanteId)) {
	        throw new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "Participante nao encontrado.");
	    }
	    
	    if (!oficinaRepository.existePorId(oficinaId)) {
	        throw new ResponseStatusException(
	                HttpStatus.NOT_FOUND, "Oficina nao encontrada.");
	    }
	    int vagasAtualizadas = oficinaRepository.diminuirVaga(oficinaId);

	    if (vagasAtualizadas == 0) {
	    	throw new ResponseStatusException(
	    	        HttpStatus.CONFLICT, "Oficina sem vagas.");
	    }

	    if (inscricaoRepository.existeInscricaoAtiva(participanteId, oficinaId)) {
	        throw new ResponseStatusException(
	                HttpStatus.CONFLICT, "Participante ja inscrito nesta oficina.");
	    }

	    inscricaoRepository.registrar(participanteId, oficinaId);
	}
}
