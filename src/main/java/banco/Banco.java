package banco;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Banco {

    private List<Cliente> clientes;

    public Banco() {
        this.clientes = new ArrayList<>();
    }

    public void adicionarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public boolean cpfJaCadastrado(String cpf) {
        for (Cliente cliente : clientes) {
            if (cliente.getCpf().equals(cpf)) {
                return true;
            }
        }
        return false;
    }

    public Conta buscarContaPorNumero(int numero) {
        for (Cliente cliente : clientes) {
            for (Conta conta : cliente.getContas()) {
                if (conta.getNumero() == numero) {
                    return conta;
                }
            }
        }
        return null;
    }

    public void transferir(int numeroOrigem, int numeroDestino, double valor) throws SaldoInsuficienteException {
        Conta origem = buscarContaPorNumero(numeroOrigem);
        Conta destino = buscarContaPorNumero(numeroDestino);

        if (origem == null || destino == null) {
            throw new IllegalArgumentException("Conta de origem ou destino não encontrada.");
        }

        origem.sacar(valor);
        destino.depositar(valor);
    }

    // ---------- PERSISTÊNCIA ----------

    public void salvarDados(String caminhoArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            for (Cliente cliente : clientes) {
                writer.write("CLIENTE;" + cliente.getNome() + ";" + cliente.getCpf());
                writer.newLine();

                for (Conta conta : cliente.getContas()) {

                    if (conta instanceof ContaCorrente cc) {
                        writer.write("CONTACORRENTE;" + cc.getNumero() + ";" + cc.getSaldo()
                                + ";" + cliente.getCpf() + ";" + cc.getLimiteChequeEspecial());

                    } else if (conta instanceof ContaPoupanca cp) {
                        writer.write("CONTAPOUPANCA;" + cp.getNumero() + ";" + cp.getSaldo()
                                + ";" + cliente.getCpf() + ";" + cp.getTaxaRendimentoMensal());

                    } else {
                        writer.write("CONTA;" + conta.getNumero() + ";" + conta.getSaldo()
                                + ";" + cliente.getCpf());
                    }

                    writer.newLine();
                }
            }
            System.out.println("Dados salvos com sucesso em " + caminhoArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    public void carregarDados(String caminhoArquivo) {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            System.out.println("Nenhum arquivo de dados encontrado. Começando do zero.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(";");

                switch (partes[0]) {

                    case "CLIENTE" -> {
                        String nome = partes[1];
                        String cpf = partes[2];
                        clientes.add(new Cliente(nome, cpf));
                    }

                    case "CONTA" -> {
                        int numero = Integer.parseInt(partes[1]);
                        double saldo = Double.parseDouble(partes[2]);
                        String cpfDono = partes[3];

                        Cliente dono = buscarClientePorCpf(cpfDono);
                        if (dono != null) {
                            dono.adicionarConta(new Conta(numero, saldo));
                        }
                    }

                    case "CONTACORRENTE" -> {
                        int numero = Integer.parseInt(partes[1]);
                        double saldo = Double.parseDouble(partes[2]);
                        String cpfDono = partes[3];
                        double limite = Double.parseDouble(partes[4]);

                        Cliente dono = buscarClientePorCpf(cpfDono);
                        if (dono != null) {
                            dono.adicionarConta(new ContaCorrente(numero, saldo, limite));
                        }
                    }

                    case "CONTAPOUPANCA" -> {
                        int numero = Integer.parseInt(partes[1]);
                        double saldo = Double.parseDouble(partes[2]);
                        String cpfDono = partes[3];
                        double taxa = Double.parseDouble(partes[4]);

                        Cliente dono = buscarClientePorCpf(cpfDono);
                        if (dono != null) {
                            dono.adicionarConta(new ContaPoupanca(numero, saldo, taxa));
                        }
                    }
                }
            }
            System.out.println("Dados carregados com sucesso de " + caminhoArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
        }
    }

    private Cliente buscarClientePorCpf(String cpf) {
        for (Cliente cliente : clientes) {
            if (cliente.getCpf().equals(cpf)) {
                return cliente;
            }
        }
        return null;
    }
}