package banco;

public class ContaCorrente extends Conta {

    private double limiteChequeEspecial;

    public ContaCorrente(int numero, double saldoInicial, double limiteChequeEspecial) {
        super(numero, saldoInicial);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser positivo.");
        }

        double saldoDisponivel = saldo + limiteChequeEspecial;

        if (valor > saldoDisponivel) {
            throw new SaldoInsuficienteException("Saldo insuficiente, mesmo considerando o limite do cheque especial.");
        }

        saldo -= valor;
        historico.add(new Transacao(TipoTransacao.SAQUE, valor));
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }
}