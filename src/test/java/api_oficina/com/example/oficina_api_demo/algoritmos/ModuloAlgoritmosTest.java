package api_oficina.com.example.oficina_api_demo.algoritmos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ModuloAlgoritmosTest {

    @Test
    void testaBuscasEOrdenacao() {
        ModuloAlgoritmos.Oficina primeira = new ModuloAlgoritmos.Oficina(1L, "Java", 20, 10);
        ModuloAlgoritmos.Oficina segunda = new ModuloAlgoritmos.Oficina(2L, "Algoritmos", 15, 5);
        ModuloAlgoritmos.Oficina terceira = new ModuloAlgoritmos.Oficina(3L, "Banco de Dados", 25, 12);
        List<ModuloAlgoritmos.Oficina> oficinas = new ArrayList<>(List.of(primeira, segunda, terceira));

        assertSame(segunda, ModuloAlgoritmos.buscarLinearPorId(oficinas, 2L));
        assertSame(segunda, ModuloAlgoritmos.buscarBinariaPorId(oficinas, 2L));

        ModuloAlgoritmos.ordenarPorTitulo(oficinas);

        assertEquals(
                List.of("Algoritmos", "Banco de Dados", "Java"),
                oficinas.stream().map(ModuloAlgoritmos.Oficina::getTitulo).toList());
    }
}
