import java.util.*;

public class Usuario {
    private final String id;
    private String nome;
    private String email;
    private String senhaHash;
    private GerenciadorTarefas gerenciador;

    public Usuario(String nome, String email, String senha) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.email = validarEmail(email);
        this.senhaHash = gerarHash(senha);
        this.gerenciador = new GerenciadorTarefas();
    }

    public boolean verificarSenha(String senha) {
        return senhaHash.equals(gerarHash(senha));
    }

    public void adicionarTarefa(Tarefa tarefa) {
        gerenciador.adicionarTarefa(tarefa);
    }

    private String gerarHash(String senha) {
        return Integer.toHexString(senha.hashCode());
    }

    private String validarEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        return email;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public GerenciadorTarefas getGerenciador() { return gerenciador; }
}