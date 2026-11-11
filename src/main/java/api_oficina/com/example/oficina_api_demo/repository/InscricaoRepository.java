package api_oficina.com.example.oficina_api_demo.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class InscricaoRepository {

	private final JdbcTemplate jdbcTemplate;

	public InscricaoRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Map<String, Object>> listarTodos() {
		return jdbcTemplate.queryForList("SELECT * FROM inscricoes");
	}

	public Optional<Map<String, Object>> buscarPorId(Long id) {
		return jdbcTemplate.queryForList("SELECT * FROM inscricoes WHERE id = ?", id)
				.stream()
				.findFirst();
	}

	public Map<String, Object> adicionar(Long participanteId, Long oficinaId) {
		return jdbcTemplate.queryForMap(
				"INSERT INTO inscricoes (participante_id, oficina_id) VALUES (?, ?) RETURNING *",
				participanteId, oficinaId);
	}

	public boolean existeAtiva(Long participanteId, Long oficinaId) {
		return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
				"SELECT EXISTS (SELECT 1 FROM inscricoes WHERE participante_id = ? AND oficina_id = ? AND situacao = 'ATIVA')",
				Boolean.class, participanteId, oficinaId));
	}

	public boolean cancelar(Long id) {
		return jdbcTemplate.update(
				"UPDATE inscricoes SET situacao = 'CANCELADA' WHERE id = ? AND situacao = 'ATIVA'",
				id) > 0;
	}
}
