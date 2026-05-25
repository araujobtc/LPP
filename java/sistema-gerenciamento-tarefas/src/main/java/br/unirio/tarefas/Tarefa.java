import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Tarefa {
    private final String id;
    private String titulo;
    private String descricao;
    private PrioridadeTarefa prioridade;
    private StatusTarefa status;
    private Categoria categoria;
    private final LocalDateTime dataCriacao;

    public Tarefa(String titulo, String descricao, PrioridadeTarefa prioridade) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.status = StatusTarefa.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
        this.categoria = null;
    }

    public void iniciar() {
        if (this.status != StatusTarefa.PENDENTE) {
            System.out.println("Tarefa só pode ser iniciada se estiver pendente.");
            return;
        }
        this.status = StatusTarefa.EM_ANDAMENTO;
        System.out.println("Tarefa '" + titulo + "' iniciada.");
    }

    public void concluir() {
        if (this.status == StatusTarefa.CONCLUIDA) {
            System.out.println("Tarefa já está concluída.");
            return;
        }
        this.status = StatusTarefa.CONCLUIDA;
        System.out.println("Tarefa '" + titulo + "' concluída.");
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public PrioridadeTarefa getPrioridade() { return prioridade; }
    public StatusTarefa getStatus() { return status; }
    public Categoria getCategoria() { return categoria; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPrioridade(PrioridadeTarefa prioridade) { this.prioridade = prioridade; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        String cat = (categoria != null) ? categoria.getNome() : "Sem categoria";
        return String.format("[%s] %s | %s | %s | %s",
                status,
                titulo,
                prioridade,
                cat,
                dataCriacao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
}