package banco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
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

    // ---------- PERSISTÊNCIA (MySQL) ----------

    public void salvarDados() {
        String sqlCliente = "INSERT INTO clientes (cpf, nome) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE nome = VALUES(nome)";

        String sqlConta = "INSERT INTO contas (numero, tipo, saldo, cpf_cliente, limite_cheque_especial, taxa_rendimento_mensal) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE saldo = VALUES(saldo), " +
                "limite_cheque_especial = VALUES(limite_cheque_especial), " +
                "taxa_rendimento_mensal = VALUES(taxa_rendimento_mensal)";

        try (Connection conn = ConexaoBD.conectar();
             PreparedStatement psCliente = conn.prepareStatement(sqlCliente);
             PreparedStatement psConta = conn.prepareStatement(sqlConta)) {

            for (Cliente cliente : clientes) {
                psCliente.setString(1, cliente.getCpf());
                psCliente.setString(2, cliente.getNome());
                psCliente.addBatch();

                for (Conta conta : cliente.getContas()) {
                    String tipo;
                    Double limite = null;
                    Double taxa = null;

                    if (conta instanceof ContaCorrente cc) {
                        tipo = "CORRENTE";
                        limite = cc.getLimiteChequeEspecial();
                    } else if (conta instanceof ContaPoupanca cp) {
                        tipo = "POUPANCA";
                        taxa = cp.getTaxaRendimentoMensal();
                    } else {
                        tipo = "COMUM";
                    }

                    psConta.setInt(1, conta.getNumero());
                    psConta.setString(2, tipo);
                    psConta.setDouble(3, conta.getSaldo());
                    psConta.setString(4, cliente.getCpf());

                    if (limite != null) {
                        psConta.setDouble(5, limite);
                    } else {
                        psConta.setNull(5, Types.DOUBLE);
                    }

                    if (taxa != null) {
                        psConta.setDouble(6, taxa);
                    } else {
                        psConta.setNull(6, Types.DOUBLE);
                    }

                    psConta.addBatch();
                }
            }

            psCliente.executeBatch();
            psConta.executeBatch();

            System.out.println("Dados salvos com sucesso no MySQL!");
        } catch (SQLException e) {
            System.out.println("Erro ao salvar dados no MySQL: " + e.getMessage());
        }
    }

    public void carregarDados() {
        clientes.clear();

        String sqlClientes = "SELECT cpf, nome FROM clientes";
        String sqlContas = "SELECT numero, tipo, saldo, cpf_cliente, limite_cheque_especial, taxa_rendimento_mensal FROM contas";

        try (Connection conn = ConexaoBD.conectar();
             Statement stClientes = conn.createStatement();
             ResultSet rsClientes = stClientes.executeQuery(sqlClientes)) {

            while (rsClientes.next()) {
                String cpf = rsClientes.getString("cpf");
                String nome = rsClientes.getString("nome");
                clientes.add(new Cliente(nome, cpf));
            }

            try (Statement stContas = conn.createStatement();
                 ResultSet rsContas = stContas.executeQuery(sqlContas)) {

                while (rsContas.next()) {
                    int numero = rsContas.getInt("numero");
                    String tipo = rsContas.getString("tipo");
                    double saldo = rsContas.getDouble("saldo");
                    String cpfCliente = rsContas.getString("cpf_cliente");

                    Cliente dono = buscarClientePorCpf(cpfCliente);
                    if (dono == null) {
                        continue;
                    }

                    switch (tipo) {
                        case "CORRENTE" -> {
                            double limite = rsContas.getDouble("limite_cheque_especial");
                            dono.adicionarConta(new ContaCorrente(numero, saldo, limite));
                        }
                        case "POUPANCA" -> {
                            double taxa = rsContas.getDouble("taxa_rendimento_mensal");
                            dono.adicionarConta(new ContaPoupanca(numero, saldo, taxa));
                        }
                        default -> dono.adicionarConta(new Conta(numero, saldo));
                    }
                }
            }

            System.out.println("Dados carregados com sucesso do MySQL!");
        } catch (SQLException e) {
            System.out.println("Erro ao carregar dados do MySQL: " + e.getMessage());
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