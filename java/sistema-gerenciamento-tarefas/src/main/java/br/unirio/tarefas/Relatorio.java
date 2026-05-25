import java.util.*;
import java.util.stream.Collectors;

public class Relatorio {
    private final List<Tarefa> tarefas;

    public Relatorio(List<Tarefa> tarefas) {
        this.tarefas = Collections.unmodifiableList(tarefas);
    }

    public double calcularTaxaConclusao() {
        if (tarefas.isEmpty()) return 0.0;

        long concluidas = tarefas.stream()
                .filter(t -> t.getStatus() == StatusTarefa.CONCLUIDA)
                .count();

        return (concluidas * 100.0) / tarefas.size();
    }

    public Map<StatusTarefa, Long> contagemPorStatus() {
        return tarefas.stream()
                .collect(Collectors.groupingBy(Tarefa::getStatus, Collectors.counting()));
    }

    public Map<PrioridadeTarefa, Long> contagemPorPrioridade() {
        return tarefas.stream()
                .collect(Collectors.groupingBy(Tarefa::getPrioridade, Collectors.counting()));
    }

    public String gerarRelatorioTexto() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== RELATÓRIO DE PRODUTIVIDADE ===\n");
        sb.append("Total de tarefas: ").append(tarefas.size()).append("\n");
        sb.append(String.format("Taxa de conclusão: %.1f%%\n", calcularTaxaConclusao()));

        sb.append("\nPor Status:\n");
        contagemPorStatus().forEach((s, c) ->
                sb.append(" ").append(s).append(": ").append(c).append("\n"));

        sb.append("\nPor Prioridade:\n");
        contagemPorPrioridade().forEach((p, c) ->
                sb.append(" ").append(p).append(": ").append(c).append("\n"));

        return sb.toString();
    }
}