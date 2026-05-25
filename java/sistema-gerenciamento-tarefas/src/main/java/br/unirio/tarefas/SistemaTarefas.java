public class SistemaTarefas {
    public static void main(String[] args) {
        SistemaLogin sistemaLogin = new SistemaLogin();

        sistemaLogin.cadastrar("Ana Silva", "ana@email.com", "senha123");
        sistemaLogin.cadastrar("Bruno Costa", "bruno@email.com", "abc456");

        sistemaLogin.login("ana@email.com", "senha123");

        sistemaLogin.getUsuarioLogado().ifPresent(usuario -> {
            GerenciadorTarefas gerenciador = usuario.getGerenciador();

            Categoria catEstudo = new Categoria("Estudo", "Tarefas acadêmicas");
            Categoria catTrabalho = new Categoria("Trabalho", "Tarefas profissionais");

            Tarefa t1 = new Tarefa("Estudar POO", "Revisar herança e polimorfismo", PrioridadeTarefa.ALTA);
            t1.setCategoria(catEstudo);

            Tarefa t2 = new Tarefa("Entregar relatório", "Relatório mensal da equipe", PrioridadeTarefa.ALTA);
            t2.setCategoria(catTrabalho);

            Tarefa t3 = new Tarefa("Ler artigo", "Artigo sobre programação funcional", PrioridadeTarefa.BAIXA);
            t3.setCategoria(catEstudo);

            usuario.adicionarTarefa(t1);
            usuario.adicionarTarefa(t2);
            usuario.adicionarTarefa(t3);

            t1.iniciar();
            t1.concluir();
            t2.iniciar();

            System.out.println("\n--- Todas as Tarefas ---");
            gerenciador.listarTodas().forEach(System.out::println);

            System.out.println("\n--- Tarefas Em Andamento ---");
            gerenciador.filtrarPorStatus(StatusTarefa.EM_ANDAMENTO).forEach(System.out::println);

            System.out.println("\n--- Tarefas de Alta Prioridade ---");
            gerenciador.filtrarPorPrioridade(PrioridadeTarefa.ALTA).forEach(System.out::println);

            System.out.println("\n--- Busca por 'artigo' ---");
            gerenciador.buscarPorNome("artigo").forEach(System.out::println);

            System.out.println();
            Relatorio relatorio = new Relatorio(gerenciador.listarTodas());
            System.out.println(relatorio.gerarRelatorioTexto());
        });

        sistemaLogin.logout();
    }
}