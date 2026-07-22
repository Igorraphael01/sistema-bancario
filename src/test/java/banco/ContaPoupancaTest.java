package banco;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaPoupancaTest {

    @Test
    void deveCriarContaPoupancaComTaxaCorreta() {
        ContaPoupanca conta = new ContaPoupanca(1, 1000.0, 0.01);

        assertEquals(1000.0, conta.getSaldo());
        assertEquals(0.01, conta.getTaxaRendimentoMensal());
    }

    @Test
    void deveAplicarJurosCorretamente() {
        ContaPoupanca conta = new ContaPoupanca(1, 1000.0, 0.01); // 1% ao mês

        conta.renderJuros();

        assertEquals(1010.0, conta.getSaldo());
    }

    @Test
    void deveAplicarJurosAcumuladosEmChamadasSucessivas() {
        ContaPoupanca conta = new ContaPoupanca(1, 1000.0, 0.01);

        conta.renderJuros(); // saldo vira 1010.0
        conta.renderJuros(); // 1% de 1010.0 = 10.10 -> saldo vira 1020.10

        assertEquals(1020.10, conta.getSaldo(), 0.001); // margem para arredondamento de double
    }

    @Test
    void deveRegistrarJurosNoHistoricoComoDeposito() {
        ContaPoupanca conta = new ContaPoupanca(1, 1000.0, 0.01);

        conta.renderJuros();

        assertEquals(1, conta.getHistorico().size());
        assertEquals(TipoTransacao.DEPOSITO, conta.getHistorico().get(0).getTipo());
    }

    @Test
    void naoDevePermitirSaldoNegativoAoSacar() {
        ContaPoupanca conta = new ContaPoupanca(1, 100.0, 0.01);

        // ContaPoupanca não sobrescreve sacar(), então deve herdar o comportamento da Conta comum
        assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(200.0));
    }

    @Test
    void deveSacarNormalmenteQuandoHaSaldoSuficiente() throws SaldoInsuficienteException {
        ContaPoupanca conta = new ContaPoupanca(1, 1000.0, 0.01);

        conta.sacar(300.0);

        assertEquals(700.0, conta.getSaldo());
    }
}