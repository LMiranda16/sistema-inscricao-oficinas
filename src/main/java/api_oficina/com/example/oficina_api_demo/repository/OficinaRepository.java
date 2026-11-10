package api_oficina.com.example.oficina_api_demo.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OficinaRepository {

	private final JdbcTemplate jdbcTemplate;

	public OficinaRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<Map<String, Object>> listarTodos() {
		return jdbcTemplate.queryForList("SELECT * FROM oficinas");
	}

	public Optional<Map<String, Object>> buscarPorId(Long id) {
		return jdbcTemplate.queryForList("SELECT * FROM oficinas WHERE id = ?", id)
				.stream()
				.findFirst();
	}

	public Map<String, Object> adicionar(String titulo, int capacidade) {
		return jdbcTemplate.queryForMap(
				"INSERT INTO oficinas (titulo, capacidade, vagas_disponiveis) VALUES (?, ?, ?) RETURNING *",
				titulo, capacidade, capacidade);
	}

	public boolean excluir(Long id) {
		return jdbcTemplate.update("DELETE FROM oficinas WHERE id = ?", id) > 0;
	}

	public boolean consumirVaga(Long id) {
		return jdbcTemplate.update(
				"UPDATE oficinas SET vagas_disponiveis = vagas_disponiveis - 1 "
						+ "WHERE id = ? AND vagas_disponiveis > 0",
				id) > 0;
	}

	public void devolverVaga(Long id) {
		jdbcTemplate.update(
				"UPDATE oficinas SET vagas_disponiveis = vagas_disponiveis + 1 WHERE id = ?",
				id);
	}
}
