package api_oficina.com.example.oficina_api_demo.repository;

import java.util.List;
import java.util.Map;

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
	public boolean existeInscricaoAtiva(Long participanteId, Long oficinaId) {
	    String sql = "SELECT COUNT(*) FROM inscricoes "
	            + "WHERE participante_id = ? AND oficina_id = ? "
	            + "AND situacao = 'ATIVA'";

	    Integer quantidade = jdbcTemplate.queryForObject(
	            sql, Integer.class, participanteId, oficinaId);

	    return quantidade != null && quantidade > 0;
	}		
	
	public void registrar(Long participanteId, Long oficinaId) {
	    String sql = "INSERT INTO inscricoes (participante_id, oficina_id) "
	            + "VALUES (?, ?)";

	    jdbcTemplate.update(sql, participanteId, oficinaId);
	}
	
}
