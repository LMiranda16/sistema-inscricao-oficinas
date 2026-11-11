package api_oficina.com.example.oficina_api_demo.utils;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class OficinaAlgoritmosTest {
    @Test
    void buscaEOrdenaOficinasPorId() {
        Oficina tres = new Oficina(3L, "Java", 5, 5);
        Oficina um = new Oficina(1L, "SQL", 2, 2);
        List<Oficina> oficinas = new ArrayList<>(List.of(tres, um));
        assertSame(um, Oficina.buscarLinearPorId(oficinas, 1L));
        assertNull(Oficina.buscarLinearPorId(oficinas, 99L));
        Oficina.ordenarPorId(oficinas, Oficina::getId);
        assertEquals(List.of(um, tres), oficinas);
        assertSame(tres, Oficina.buscarBinariaPorId(oficinas, 3L));
        assertNull(Oficina.buscarBinariaPorId(oficinas, 99L));
    }
}
