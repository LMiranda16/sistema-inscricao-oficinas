package api_oficina.com.example.oficina_api_demo.algoritmos;

import java.util.List;

public class ModuloAlgoritmos {

    public static class Oficina {
        private Long id;
        private String titulo;
        private int capacidade;
        private int vagasDisponiveis;

        public Oficina(Long id, String titulo, int capacidade, int vagasDisponiveis) {
            this.id = id;
            this.titulo = titulo;
            this.capacidade = capacidade;
            this.vagasDisponiveis = vagasDisponiveis;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public int getCapacidade() {
            return capacidade;
        }

        public void setCapacidade(int capacidade) {
            this.capacidade = capacidade;
        }

        public int getVagasDisponiveis() {
            return vagasDisponiveis;
        }

        public void setVagasDisponiveis(int vagasDisponiveis) {
            this.vagasDisponiveis = vagasDisponiveis;
        }
    }

    public static Oficina buscarLinearPorId(List<Oficina> oficinas, Long id) {
        for (Oficina oficina : oficinas) {
            if (oficina.getId().equals(id)) {
                return oficina;
            }
        }
        return null;
    }

    public static Oficina buscarBinariaPorId(List<Oficina> oficinas, Long id) {
        // a lista precisa estar ordenada por id
        int inicio = 0;
        int fim = oficinas.size() - 1;

        while (inicio <= fim) {
            int meio = inicio + (fim - inicio) / 2;
            Oficina oficina = oficinas.get(meio);
            int comparacao = oficina.getId().compareTo(id);

            if (comparacao == 0) {
                return oficina;
            }
            if (comparacao < 0) {
                inicio = meio + 1;
            } else {
                fim = meio - 1;
            }
        }
        return null;
    }

    public static void ordenarPorTitulo(List<Oficina> oficinas) {
        for (int i = 0; i < oficinas.size() - 1; i++) {
            for (int j = 0; j < oficinas.size() - 1 - i; j++) {
                if (oficinas.get(j).getTitulo()
                        .compareToIgnoreCase(oficinas.get(j + 1).getTitulo()) > 0) {
                    Oficina temporaria = oficinas.get(j);
                    oficinas.set(j, oficinas.get(j + 1));
                    oficinas.set(j + 1, temporaria);
                }
            }
        }
    }
}
