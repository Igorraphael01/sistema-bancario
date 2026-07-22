package banco;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaCorrenteTest {

    @Test
    void deveCriarContaCorrenteComLimiteCorreto() {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        assertEquals(100.0, conta.getSaldo());
        assertEquals(200.0, conta.getLimiteChequeEspecial());
    }

    @Test
    void deveSacarUsandoApenasOSaldoQuandoSuficiente() throws SaldoInsuficienteException {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        conta.sacar(50.0);

        assertEquals(50.0, conta.getSaldo());
    }

    @Test
    void deveSacarEntrarNoChequeEspecialQuandoSaldoNaoBasta() throws SaldoInsuficienteException {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        conta.sacar(250.0); // 100 de saldo + 150 usados do limite

        assertEquals(-150.0, conta.getSaldo());
    }

    @Test
    void devePermitirSacarExatamenteAteOLimiteTotal() throws SaldoInsuficienteException {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        conta.sacar(300.0); // 100 (saldo) + 200 (limite) = 300, no limite exato

        assertEquals(-200.0, conta.getSaldo());
    }

    @Test
    void deveLancarErroQuandoValorUltrapassaSaldoMaisLimite() {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(301.0));
    }

    @Test
    void deveLancarErroAoSacarValorZeroOuNegativo() {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        assertThrows(IllegalArgumentException.class, () -> conta.sacar(0));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(-10));
    }

    @Test
    void deveContinuarDepositandoNormalmente() {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        conta.depositar(50.0);

        assertEquals(150.0, conta.getSaldo());
    }

    @Test
    void deveRegistrarSaqueComChequeEspecialNoHistorico() throws SaldoInsuficienteException {
        ContaCorrente conta = new ContaCorrente(1, 100.0, 200.0);

        conta.sacar(250.0);

        assertEquals(1, conta.getHistorico().size());
        assertEquals(TipoTransacao.SAQUE, conta.getHistorico().get(0).getTipo());
    }
}