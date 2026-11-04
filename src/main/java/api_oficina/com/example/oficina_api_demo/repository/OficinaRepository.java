package api_oficina.com.example.oficina_api_demo.repository;

import java.util.List;
import java.util.Map;

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
	
	public int diminuirVaga(Long oficinaId) {
	    String sql = "UPDATE oficinas "
	            + "SET vagas_disponiveis = vagas_disponiveis - 1 "
	            + "WHERE id = ? AND vagas_disponiveis > 0";

	    return jdbcTemplate.update(sql, oficinaId);
	}
	public boolean existePorId(Long id) {
	    String sql = "SELECT COUNT(*) FROM oficinas WHERE id = ?";

	    Integer quantidade = jdbcTemplate.queryForObject(sql, Integer.class, id);

	    return quantidade != null && quantidade > 0;
	}
}
