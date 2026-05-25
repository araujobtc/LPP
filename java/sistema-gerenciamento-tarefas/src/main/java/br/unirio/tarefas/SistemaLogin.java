import java.util.*;

public class SistemaLogin {
    private final Map<String, Usuario> usuarios = new HashMap<>();
    private Usuario usuarioLogado = null;

    public boolean cadastrar(String nome, String email, String senha) {
        if (usuarios.containsKey(email)) {
            System.out.println("Email já cadastrado!");
            return false;
        }

        Usuario novoUsuario = new Usuario(nome, email, senha);
        usuarios.put(email, novoUsuario);

        System.out.println("Usuário '" + nome + "' cadastrado com sucesso!");
        return true;
    }

    public boolean login(String email, String senha) {
        Usuario usuario = usuarios.get(email);

        if (usuario != null && usuario.verificarSenha(senha)) {
            usuarioLogado = usuario;
            System.out.println("Bem-vindo, " + usuario.getNome() + "!");
            return true;
        }

        System.out.println("Credenciais inválidas!");
        return false;
    }

    public void logout() {
        System.out.println("Sessão encerrada.");
        usuarioLogado = null;
    }

    public Optional<Usuario> getUsuarioLogado() {
        return Optional.ofNullable(usuarioLogado);
    }

    public boolean estaLogado() {
        return usuarioLogado != null;
    }
}