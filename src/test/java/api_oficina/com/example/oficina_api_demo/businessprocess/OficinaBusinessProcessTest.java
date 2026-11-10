package api_oficina.com.example.oficina_api_demo.businessprocess;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import api_oficina.com.example.oficina_api_demo.repository.InscricaoRepository;
import api_oficina.com.example.oficina_api_demo.repository.OficinaRepository;
import api_oficina.com.example.oficina_api_demo.repository.ParticipanteRepository;

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
	void ordenaAsTresEntidadesPorIdCrescenteSemAlterarAsListasDoRepository() {
		Map<String, Object> participanteId2 = Map.of("id", 2L, "nome", "Bia");
		Map<String, Object> participanteId1 = Map.of("id", 1L, "nome", "Ana");
		Map<String, Object> oficinaId4 = Map.of("id", 4L, "titulo", "Web");
		Map<String, Object> oficinaId3 = Map.of("id", 3L, "titulo", "Java");
		Map<String, Object> inscricaoId6 = Map.of("id", 6L, "situacao", "ATIVA");
		Map<String, Object> inscricaoId5 = Map.of("id", 5L, "situacao", "CANCELADA");
		List<Map<String, Object>> participantes = List.of(participanteId2, participanteId1);
		List<Map<String, Object>> oficinas = List.of(oficinaId4, oficinaId3);
		List<Map<String, Object>> inscricoes = List.of(inscricaoId6, inscricaoId5);
		when(participanteRepository.listarTodos()).thenReturn(participantes);
		when(oficinaRepository.listarTodos()).thenReturn(oficinas);
		when(inscricaoRepository.listarTodos()).thenReturn(inscricoes);

		assertEquals(List.of(participanteId1, participanteId2), oficinaBusinessProcess.listarParticipantesOrdenados());
		assertEquals(List.of(oficinaId3, oficinaId4), oficinaBusinessProcess.listarOficinasOrdenadas());
		assertEquals(List.of(inscricaoId5, inscricaoId6), oficinaBusinessProcess.listarInscricoesOrdenadas());
		assertEquals(List.of(participanteId2, participanteId1), participantes);
		assertEquals(List.of(oficinaId4, oficinaId3), oficinas);
		assertEquals(List.of(inscricaoId6, inscricaoId5), inscricoes);
	}

	@Test
	void buscaCadaEntidadePorId() {
		Map<String, Object> participante = Map.of("id", 1L, "nome", "Ana");
		Map<String, Object> oficina = Map.of("id", 2L, "titulo", "Programacao");
		Map<String, Object> inscricao = Map.of("id", 3L, "situacao", "ATIVA");
		when(participanteRepository.buscarPorId(1L)).thenReturn(Optional.of(participante));
		when(oficinaRepository.buscarPorId(2L)).thenReturn(Optional.of(oficina));
		when(inscricaoRepository.buscarPorId(3L)).thenReturn(Optional.of(inscricao));

		assertSame(participante, oficinaBusinessProcess.buscarParticipante(1L));
		assertSame(oficina, oficinaBusinessProcess.buscarOficina(2L));
		assertSame(inscricao, oficinaBusinessProcess.buscarInscricao(3L));
	}

	@Test
	void buscaEntidadesInexistentesRetorna404() {
		when(participanteRepository.buscarPorId(99L)).thenReturn(Optional.empty());
		when(oficinaRepository.buscarPorId(99L)).thenReturn(Optional.empty());
		when(inscricaoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

		assertEquals(HttpStatus.NOT_FOUND, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.buscarParticipante(99L)).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.buscarOficina(99L)).getStatusCode());
		assertEquals(HttpStatus.NOT_FOUND, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.buscarInscricao(99L)).getStatusCode());
	}

	@Test
	void criarParticipanteComDadosValidos() {
		Map<String, Object> participante = Map.of("id", 1L, "nome", "Ana", "email", "ana@example.com");
		when(participanteRepository.adicionar("Ana", "ana@example.com")).thenReturn(participante);

		assertSame(participante, oficinaBusinessProcess.adicionarParticipante("Ana", "ana@example.com"));
		verify(participanteRepository).adicionar("Ana", "ana@example.com");
	}

	@Test
	void criarParticipanteComDadosInvalidosRetorna400() {
		assertEquals(HttpStatus.BAD_REQUEST, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarParticipante("", "ana@example.com")).getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarParticipante("Ana", " ")).getStatusCode());
	}

	@Test
	void criarOficinaComDadosValidos() {
		Map<String, Object> oficina = Map.of("id", 2L, "titulo", "Programacao", "capacidade", 20);
		when(oficinaRepository.adicionar("Programacao", 20)).thenReturn(oficina);

		assertSame(oficina, oficinaBusinessProcess.adicionarOficina("Programacao", 20));
		verify(oficinaRepository).adicionar("Programacao", 20);
	}

	@Test
	void criarOficinaComDadosInvalidosRetorna400() {
		assertEquals(HttpStatus.BAD_REQUEST, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarOficina("", 20)).getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarOficina("Programacao", 0)).getStatusCode());
	}

	@Test
	void criarInscricaoConsomeVaga() {
		Map<String, Object> inscricao = Map.of("id", 1L, "situacao", "ATIVA");
		when(participanteRepository.buscarPorId(1L)).thenReturn(Optional.of(Map.of("id", 1L)));
		when(oficinaRepository.buscarPorIdComBloqueio(2L)).thenReturn(Optional.of(Map.of("id", 2L)));
		when(oficinaRepository.consumirVaga(2L)).thenReturn(true);
		when(inscricaoRepository.adicionar(1L, 2L)).thenReturn(inscricao);

		assertSame(inscricao, oficinaBusinessProcess.adicionarInscricao(1L, 2L));
		verify(oficinaRepository).consumirVaga(2L);
		verify(inscricaoRepository).adicionar(1L, 2L);
	}

	@Test
	void criarInscricaoComReferenciasInexistentesRetorna404() {
		when(participanteRepository.buscarPorId(99L)).thenReturn(Optional.empty());

		assertEquals(HttpStatus.NOT_FOUND, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarInscricao(99L, 2L)).getStatusCode());
	}

	@Test
	void criarInscricaoSemOficinaRetorna404() {
		when(participanteRepository.buscarPorId(1L)).thenReturn(Optional.of(Map.of("id", 1L)));
		when(oficinaRepository.buscarPorIdComBloqueio(99L)).thenReturn(Optional.empty());

		assertEquals(HttpStatus.NOT_FOUND, assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarInscricao(1L, 99L)).getStatusCode());
	}

	@Test
	void criarInscricaoSemVagaRetornaConflito() {
		when(participanteRepository.buscarPorId(1L)).thenReturn(Optional.of(Map.of("id", 1L)));
		when(oficinaRepository.buscarPorIdComBloqueio(2L)).thenReturn(Optional.of(Map.of("id", 2L)));
		when(oficinaRepository.consumirVaga(2L)).thenReturn(false);

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarInscricao(1L, 2L));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

	@Test
	void excluirInscricaoAtivaCancelaEDevolveVaga() {
		when(inscricaoRepository.buscarPorId(3L))
				.thenReturn(Optional.of(Map.of("id", 3L, "oficina_id", 2L, "situacao", "ATIVA")))
				.thenReturn(Optional.of(Map.of("id", 3L, "oficina_id", 2L, "situacao", "CANCELADA")));
		when(inscricaoRepository.cancelar(3L)).thenReturn(true);

		Map<String, Object> cancelada = oficinaBusinessProcess.excluirInscricao(3L);

		assertEquals("CANCELADA", cancelada.get("situacao"));
		verify(oficinaRepository).devolverVaga(2L);
	}

	@Test
	void excluirInscricaoJaCanceladaRetornaConflito() {
		when(inscricaoRepository.buscarPorId(3L))
				.thenReturn(Optional.of(Map.of("id", 3L, "oficina_id", 2L, "situacao", "CANCELADA")));

		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> oficinaBusinessProcess.excluirInscricao(3L));

		assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
	}

	@Test
	void rejeitaInscricaoAtivaDuplicada() {
		when(participanteRepository.buscarPorId(1L)).thenReturn(Optional.of(Map.of("id", 1L)));
		when(oficinaRepository.buscarPorIdComBloqueio(2L)).thenReturn(Optional.of(Map.of("id", 2L)));
		when(inscricaoRepository.existeAtiva(1L, 2L)).thenReturn(true);
		ResponseStatusException erro = assertThrows(ResponseStatusException.class,
				() -> oficinaBusinessProcess.adicionarInscricao(1L, 2L));
		assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
		org.mockito.Mockito.verify(oficinaRepository, org.mockito.Mockito.never()).consumirVaga(2L);
	}

	@Test
	void naoAlteraCapacidadeQuandoExisteHistorico() {
		when(oficinaRepository.buscarPorIdComBloqueio(2L)).thenReturn(Optional.of(
				Map.of("id", 2L, "capacidade", 10, "vagas_disponiveis", 8)));
		when(oficinaRepository.temHistorico(2L)).thenReturn(true);
		ResponseStatusException erro = assertThrows(ResponseStatusException.class,
				() -> oficinaBusinessProcess.atualizarOficina(2L, "Java", 20));
		assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
	}
}
