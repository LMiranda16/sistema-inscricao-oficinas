package api_oficina.com.example.oficina_api_demo.controller;

import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, String>> tratarViolacaoDeIntegridade() {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(Map.of("erro", "A operacao viola uma restricao do banco de dados."));
	}
}
