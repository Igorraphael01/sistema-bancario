package banco;

import java.util.ArrayList;
import java.util.List;

public class Conta {

    protected int numero;
    protected double saldo;
    protected List<Transacao> historico;

    public Conta(int numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = saldoInicial;
        this.historico = new ArrayList<>();
    }

    public void depositar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de depósito deve ser positivo.");
        }
        saldo += valor;
        historico.add(new Transacao(TipoTransacao.DEPOSITO, valor));
    }

    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser positivo.");
        }
        if (valor > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente para saque.");
        }
        saldo -= valor;
        historico.add(new Transacao(TipoTransacao.SAQUE, valor));
    }

    public double getSaldo() {
        return saldo;
    }

    public int getNumero() {
        return numero;
    }

    public List<Transacao> getHistorico() {
        return historico;
    }
}
