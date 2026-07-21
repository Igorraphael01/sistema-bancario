package banco;

public class ContaPoupanca extends Conta {

    private double taxaRendimentoMensal; // exemplo: 0.005 = 0,5% ao mês

    public ContaPoupanca(int numero, double saldoInicial, double taxaRendimentoMensal) {
        super(numero, saldoInicial);
        this.taxaRendimentoMensal = taxaRendimentoMensal;
    }

    public void renderJuros() {
        double juros = saldo * taxaRendimentoMensal;
        saldo += juros;
        historico.add(new Transacao(TipoTransacao.DEPOSITO, juros));
    }

    public double getTaxaRendimentoMensal() {
        return taxaRendimentoMensal;
    }
}