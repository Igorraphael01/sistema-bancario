package banco;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    @Test
    void deveCriarContaComSaldoInicialCorreto() {
        Conta conta = new Conta(1, 100.0);

        assertEquals(100.0, conta.getSaldo());
        assertEquals(1, conta.getNumero());
    }

    @Test
    void deveDepositarComSucesso() {
        Conta conta = new Conta(1, 100.0);

        conta.depositar(50.0);

        assertEquals(150.0, conta.getSaldo());
    }

    @Test
    void deveLancarErroAoDepositarValorZeroOuNegativo() {
        Conta conta = new Conta(1, 100.0);

        assertThrows(IllegalArgumentException.class, () -> conta.depositar(0));
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(-10));
    }

    @Test
    void deveSacarComSucessoQuandoHaSaldoSuficiente() throws SaldoInsuficienteException {
        Conta conta = new Conta(1, 100.0);

        conta.sacar(30.0);

        assertEquals(70.0, conta.getSaldo());
    }

    @Test
    void deveLancarErroAoSacarComSaldoInsuficiente() {
        Conta conta = new Conta(1, 100.0);

        assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(200.0));
    }

    @Test
    void deveLancarErroAoSacarValorZeroOuNegativo() {
        Conta conta = new Conta(1, 100.0);

        assertThrows(IllegalArgumentException.class, () -> conta.sacar(0));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(-10));
    }

    @Test
    void naoDeveAlterarSaldoQuandoSaqueFalha() {
        Conta conta = new Conta(1, 100.0);

        assertThrows(SaldoInsuficienteException.class, () -> conta.sacar(200.0));
        assertEquals(100.0, conta.getSaldo()); // saldo continua intacto após a falha
    }

    @Test
    void deveRegistrarDepositoNoHistorico() {
        Conta conta = new Conta(1, 100.0);

        conta.depositar(50.0);

        assertEquals(1, conta.getHistorico().size());
        assertEquals(TipoTransacao.DEPOSITO, conta.getHistorico().get(0).getTipo());
    }

    @Test
    void deveRegistrarSaqueNoHistorico() throws SaldoInsuficienteException {
        Conta conta = new Conta(1, 100.0);

        conta.sacar(30.0);

        assertEquals(1, conta.getHistorico().size());
        assertEquals(TipoTransacao.SAQUE, conta.getHistorico().get(0).getTipo());
    }

    @Test
    void naoDeveRegistrarNoHistoricoQuandoOperacaoFalha() {
        Conta conta = new Conta(1, 100.0);

        assertThrows(IllegalArgumentException.class, () -> conta.depositar(-10));

        assertTrue(conta.getHistorico().isEmpty());
    }
}