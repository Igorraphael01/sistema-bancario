package banco;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BancoTest {

    @Test
    void deveDetectarCpfJaCadastrado() {
        Banco banco = new Banco();
        banco.adicionarCliente(new Cliente("Maria", "111.111.111-11"));

        assertTrue(banco.cpfJaCadastrado("111.111.111-11"));
    }

    @Test
    void naoDeveAcusarCpfComoCadastradoSeNaoExistir() {
        Banco banco = new Banco();
        banco.adicionarCliente(new Cliente("Maria", "111.111.111-11"));

        assertFalse(banco.cpfJaCadastrado("999.999.999-99"));
    }

    @Test
    void deveEncontrarContaPorNumero() {
        Banco banco = new Banco();
        Cliente cliente = new Cliente("Maria", "111.111.111-11");
        Conta conta = new Conta(1, 100.0);
        cliente.adicionarConta(conta);
        banco.adicionarCliente(cliente);

        Conta encontrada = banco.buscarContaPorNumero(1);

        assertNotNull(encontrada);
        assertEquals(conta, encontrada);
    }

    @Test
    void deveRetornarNullQuandoContaNaoExiste() {
        Banco banco = new Banco();

        assertNull(banco.buscarContaPorNumero(999));
    }

    @Test
    void deveEncontrarContaMesmoComVariosClientes() {
        Banco banco = new Banco();

        Cliente maria = new Cliente("Maria", "111.111.111-11");
        maria.adicionarConta(new Conta(1, 100.0));

        Cliente joao = new Cliente("João", "222.222.222-22");
        Conta contaJoao = new Conta(2, 50.0);
        joao.adicionarConta(contaJoao);

        banco.adicionarCliente(maria);
        banco.adicionarCliente(joao);

        assertEquals(contaJoao, banco.buscarContaPorNumero(2));
    }

    @Test
    void deveTransferirComSucessoEntreDuasContas() throws SaldoInsuficienteException {
        Banco banco = new Banco();

        Cliente maria = new Cliente("Maria", "111.111.111-11");
        Conta contaMaria = new Conta(1, 100.0);
        maria.adicionarConta(contaMaria);

        Cliente joao = new Cliente("João", "222.222.222-22");
        Conta contaJoao = new Conta(2, 50.0);
        joao.adicionarConta(contaJoao);

        banco.adicionarCliente(maria);
        banco.adicionarCliente(joao);

        banco.transferir(1, 2, 30.0);

        assertEquals(70.0, contaMaria.getSaldo());
        assertEquals(80.0, contaJoao.getSaldo());
    }

    @Test
    void deveLancarErroAoTransferirDeContaInexistente() {
        Banco banco = new Banco();
        Cliente joao = new Cliente("João", "222.222.222-22");
        joao.adicionarConta(new Conta(2, 50.0));
        banco.adicionarCliente(joao);

        assertThrows(IllegalArgumentException.class, () -> banco.transferir(999, 2, 10.0));
    }

    @Test
    void deveLancarErroAoTransferirParaContaInexistente() {
        Banco banco = new Banco();
        Cliente maria = new Cliente("Maria", "111.111.111-11");
        maria.adicionarConta(new Conta(1, 100.0));
        banco.adicionarCliente(maria);

        assertThrows(IllegalArgumentException.class, () -> banco.transferir(1, 999, 10.0));
    }

    @Test
    void naoDeveTransferirQuandoSaldoDaOrigemInsuficiente() {
        Banco banco = new Banco();

        Cliente maria = new Cliente("Maria", "111.111.111-11");
        Conta contaMaria = new Conta(1, 50.0);
        maria.adicionarConta(contaMaria);

        Cliente joao = new Cliente("João", "222.222.222-22");
        Conta contaJoao = new Conta(2, 50.0);
        joao.adicionarConta(contaJoao);

        banco.adicionarCliente(maria);
        banco.adicionarCliente(joao);

        assertThrows(SaldoInsuficienteException.class, () -> banco.transferir(1, 2, 1000.0));

        // garante que nada mudou nas duas contas após a falha
        assertEquals(50.0, contaMaria.getSaldo());
        assertEquals(50.0, contaJoao.getSaldo());
    }
}