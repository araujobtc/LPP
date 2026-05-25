import java.util.*;
import java.util.stream.Collectors;

public class GerenciadorTarefas {
    private final List<Tarefa> tarefas = new ArrayList<>();

    public void adicionarTarefa(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("Tarefa nula");
        }
        tarefas.add(tarefa);
        System.out.println("Tarefa '" + tarefa.getTitulo() + "' adicionada.");
    }

    public boolean removerTarefa(String titulo) {
        return tarefas.removeIf(t -> t.getTitulo().equalsIgnoreCase(titulo));
    }

    public List<Tarefa> listarTodas() {
        return Collections.unmodifiableList(tarefas);
    }

    public List<Tarefa> filtrarPorStatus(StatusTarefa status) {
        return tarefas.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Tarefa> filtrarPorPrioridade(PrioridadeTarefa prioridade) {
        return tarefas.stream()
                .filter(t -> t.getPrioridade() == prioridade)
                .sorted(Comparator.comparing(Tarefa::getDataCriacao))
                .collect(Collectors.toList());
    }

    public List<Tarefa> buscarPorNome(String termo) {
        String termoBusca = termo.toLowerCase();

        return tarefas.stream()
                .filter(t -> t.getTitulo().toLowerCase().contains(termoBusca))
                .collect(Collectors.toList());
    }

    public boolean editarTarefa(String titulo, String novoTitulo, String novaDesc) {
        Optional<Tarefa> encontrada = tarefas.stream()
                .filter(t -> t.getTitulo().equalsIgnoreCase(titulo))
                .findFirst();

        encontrada.ifPresent(t -> {
            if (novoTitulo != null && !novoTitulo.isBlank()) {
                t.setTitulo(novoTitulo);
            }

            if (novaDesc != null && !novaDesc.isBlank()) {
                t.setDescricao(novaDesc);
            }
        });

        return encontrada.isPresent();
    }

    public int totalTarefas() {
        return tarefas.size();
    }
}