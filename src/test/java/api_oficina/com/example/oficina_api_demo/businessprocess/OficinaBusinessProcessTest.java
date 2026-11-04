package api_oficina.com.example.oficina_api_demo.businessprocess;

import static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import api_oficina.com.example.oficina_api_demo.repository.InscricaoRepository;
import api_oficina.com.example.oficina_api_demo.repository.OficinaRepository;
import api_oficina.com.example.oficina_api_demo.repository.ParticipanteRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OficinaBusinessProcessTest {

	@Mock
	private ParticipanteRepository participanteRepository;

	@Mock
	private OficinaRepository oficinaRepository;

	@Mock
	private InscricaoRepository inscricaoRepository;

	@InjectMocks
	private OficinaBusinessProcess oficinaBusinessProcess;

	@Test
	void listaParticipantesDelegandoAoRepository() {
		List<Map<String, Object>> participantes = List.of(Map.of(
				"id", 1L,
				"nome", "Ana",
				"email", "ana@example.com"));
		when(participanteRepository.listarTodos()).thenReturn(participantes);

		assertSame(participantes, oficinaBusinessProcess.listarParticipantes());
		verify(participanteRepository).listarTodos();
	}

	@Test
	void listaOficinasDelegandoAoRepository() {
		List<Map<String, Object>> oficinas = List.of(Map.of(
				"id", 1L,
				"titulo", "Programacao",
				"capacidade", 20,
				"vagas_disponiveis", 20));
		when(oficinaRepository.listarTodos()).thenReturn(oficinas);

		assertSame(oficinas, oficinaBusinessProcess.listarOficinas());
		verify(oficinaRepository).listarTodos();
	}

	@Test
	void listaInscricoesDelegandoAoRepository() {
		List<Map<String, Object>> inscricoes = List.of(Map.of(
				"id", 1L,
				"participante_id", 1L,
				"oficina_id", 1L,
				"situacao", "ATIVA"));
		when(inscricaoRepository.listarTodos()).thenReturn(inscricoes);

		assertSame(inscricoes, oficinaBusinessProcess.listarInscricoes());
		verify(inscricaoRepository).listarTodos();
	}
	@Test
	void registraInscricaoQuandoHaVaga() {
	    when(participanteRepository.existePorId(1L)).thenReturn(true);
	    when(oficinaRepository.existePorId(1L)).thenReturn(true);
	    when(oficinaRepository.diminuirVaga(1L)).thenReturn(1);
	    when(inscricaoRepository.existeInscricaoAtiva(1L, 1L)).thenReturn(false);

	    oficinaBusinessProcess.registrarInscricao(1L, 1L);

	    verify(oficinaRepository).diminuirVaga(1L);
	    verify(inscricaoRepository).registrar(1L, 1L);
	}

	@Test
	void naoRegistraQuandoParticipanteNaoExiste() {
	    when(participanteRepository.existePorId(99L)).thenReturn(false);

	    ResponseStatusException erro = assertThrows(
	            ResponseStatusException.class,
	            () -> oficinaBusinessProcess.registrarInscricao(99L, 1L));

	    assertEquals(HttpStatus.NOT_FOUND, erro.getStatusCode());
	    verify(inscricaoRepository, never()).registrar(99L, 1L);
	}

	@Test
	void naoRegistraQuandoOficinaNaoTemVagas() {
	    when(participanteRepository.existePorId(1L)).thenReturn(true);
	    when(oficinaRepository.existePorId(1L)).thenReturn(true);
	    when(oficinaRepository.diminuirVaga(1L)).thenReturn(0);

	    ResponseStatusException erro = assertThrows(
	            ResponseStatusException.class,
	            () -> oficinaBusinessProcess.registrarInscricao(1L, 1L));

	    assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
	    verify(inscricaoRepository, never()).registrar(1L, 1L);
	}

	@Test
	void naoRegistraInscricaoDuplicada() {
	    when(participanteRepository.existePorId(1L)).thenReturn(true);
	    when(oficinaRepository.existePorId(1L)).thenReturn(true);
	    when(oficinaRepository.diminuirVaga(1L)).thenReturn(1);
	    when(inscricaoRepository.existeInscricaoAtiva(1L, 1L)).thenReturn(true);

	    ResponseStatusException erro = assertThrows(
	            ResponseStatusException.class,
	            () -> oficinaBusinessProcess.registrarInscricao(1L, 1L));

	    assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
	    verify(inscricaoRepository, never()).registrar(1L, 1L);
	}
}
