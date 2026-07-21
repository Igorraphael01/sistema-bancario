package banco;

import java.util.Scanner;

public class Main {

    private static final String ARQUIVO_DADOS = "dados_banco.txt";
    private static Banco banco = new Banco();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        banco.carregarDados(ARQUIVO_DADOS);

        int opcao;

        do {
            exibirMenu();
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> criarCliente();
                case 2 -> criarConta();
                case 3 -> depositar();
                case 4 -> sacar();
                case 5 -> transferir();
                case 6 -> consultarSaldo();
                case 7 -> exibirExtrato();
                case 8 -> listarClientes();
                case 9 -> renderJuros();
                case 0 -> System.out.println("Encerrando o sistema...");
                default -> System.out.println("Opção inválida. Tente novamente.");
            }

        } while (opcao != 0);

        banco.salvarDados(ARQUIVO_DADOS);
        scanner.close();
    }

    private static void exibirMenu() {
        System.out.println("\n===== BANCO =====");
        System.out.println("1 - Criar cliente");
        System.out.println("2 - Criar conta");
        System.out.println("3 - Depositar");
        System.out.println("4 - Sacar");
        System.out.println("5 - Transferir");
        System.out.println("6 - Consultar saldo");
        System.out.println("7 - Ver extrato");
        System.out.println("8 - Listar clientes");
        System.out.println("9 - Render juros Conta poupança");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int lerOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void criarCliente() {
        System.out.print("Nome do cliente: ");
        String nome = scanner.nextLine();
        System.out.print("CPF do cliente: ");
        String cpf = scanner.nextLine();

        if (banco.cpfJaCadastrado(cpf)) {
            System.out.println("Erro: já existe um cliente cadastrado com esse CPF.");
            return;
        }

        Cliente cliente = new Cliente(nome, cpf);
        banco.adicionarCliente(cliente);

        System.out.println("Cliente criado com sucesso!");
    }

    private static void criarConta() {
        System.out.print("CPF do cliente dono da conta: ");
        String cpf = scanner.nextLine();

        Cliente cliente = buscarClientePorCpf(cpf);
        if (cliente == null) {
            System.out.println("Cliente não encontrado.");
            return;
        }

        System.out.print("Número da nova conta: ");
        int numero = Integer.parseInt(scanner.nextLine());

        if (banco.buscarContaPorNumero(numero) != null) {
            System.out.println("Erro: já existe uma conta com esse número.");
            return;
        }

        System.out.print("Saldo inicial: ");
        double saldoInicial = Double.parseDouble(scanner.nextLine());

        System.out.println("Tipo de conta:");
        System.out.println("1 - Conta Corrente");
        System.out.println("2 - Conta Poupança");
        System.out.print("Escolha: ");
        int tipo = Integer.parseInt(scanner.nextLine());

        Conta conta;

        if (tipo == 1) {
            System.out.print("Limite do cheque especial: ");
            double limite = Double.parseDouble(scanner.nextLine());
            conta = new ContaCorrente(numero, saldoInicial, limite);

        } else if (tipo == 2) {
            System.out.print("Taxa de rendimento mensal (ex: 0.01 para 1%): ");
            double taxa = Double.parseDouble(scanner.nextLine());
            conta = new ContaPoupanca(numero, saldoInicial, taxa);

        } else {
            System.out.println("Tipo inválido. Conta não criada.");
            return;
        }

        cliente.adicionarConta(conta);
        System.out.println("Conta criada com sucesso!");
    }

    private static void depositar() {
        Conta conta = pedirConta();
        if (conta == null) return;

        System.out.print("Valor a depositar: ");
        double valor = Double.parseDouble(scanner.nextLine());

        try {
            conta.depositar(valor);
            System.out.println("Depósito realizado! Novo saldo: " + conta.getSaldo());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void sacar() {
        Conta conta = pedirConta();
        if (conta == null) return;

        System.out.print("Valor a sacar: ");
        double valor = Double.parseDouble(scanner.nextLine());

        try {
            conta.sacar(valor);
            System.out.println("Saque realizado! Novo saldo: " + conta.getSaldo());
        } catch (SaldoInsuficienteException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void transferir() {
        System.out.print("Número da conta de origem: ");
        int origem = Integer.parseInt(scanner.nextLine());
        System.out.print("Número da conta de destino: ");
        int destino = Integer.parseInt(scanner.nextLine());
        System.out.print("Valor a transferir: ");
        double valor = Double.parseDouble(scanner.nextLine());

        try {
            banco.transferir(origem, destino, valor);
            System.out.println("Transferência realizada com sucesso!");
        } catch (SaldoInsuficienteException | IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void renderJuros() {
        Conta conta = pedirConta();
        if (conta == null) return;

        if (conta instanceof ContaPoupanca poupanca) {
            poupanca.renderJuros();
            System.out.println("Juros aplicados! Novo saldo: " + poupanca.getSaldo());
        } else {
            System.out.println("Essa operação só é válida para contas poupança.");
        }
    }

    private static void consultarSaldo() {
        Conta conta = pedirConta();
        if (conta == null) return;

        System.out.println("Saldo atual: R$ " + conta.getSaldo());
    }

    private static void exibirExtrato() {
        Conta conta = pedirConta();
        if (conta == null) return;

        System.out.println("--- Extrato da conta " + conta.getNumero() + " ---");
        if (conta.getHistorico().isEmpty()) {
            System.out.println("Nenhuma movimentação ainda.");
        } else {
            for (Transacao t : conta.getHistorico()) {
                System.out.println(t);
            }
        }
    }

    private static void listarClientes() {
        if (banco.getClientes().isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }

        for (Cliente cliente : banco.getClientes()) {
            System.out.println("- " + cliente.getNome() + " (CPF: " + cliente.getCpf() + ")");
            for (Conta conta : cliente.getContas()) {
                System.out.println("    Conta " + conta.getNumero() + " - Saldo: R$ " + conta.getSaldo());
            }
        }
    }

    private static Conta pedirConta() {
        System.out.print("Número da conta: ");
        int numero = Integer.parseInt(scanner.nextLine());
        Conta conta = banco.buscarContaPorNumero(numero);

        if (conta == null) {
            System.out.println("Conta não encontrada.");
        }
        return conta;
    }

    private static Cliente buscarClientePorCpf(String cpf) {
        for (Cliente cliente : banco.getClientes()) {
            if (cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }
        return null;
    }
}