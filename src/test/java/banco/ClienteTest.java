package banco;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deveCriarClienteComNomeECpfCorretos() {
        Cliente cliente = new Cliente("Maria", "111.111.111-11");

        assertEquals("Maria", cliente.getNome());
        assertEquals("111.111.111-11", cliente.getCpf());
    }

    @Test
    void deveComecarSemNenhumaConta() {
        Cliente cliente = new Cliente("Maria", "111.111.111-11");

        assertTrue(cliente.getContas().isEmpty());
    }

    @Test
    void deveAdicionarContaCorretamente() {
        Cliente cliente = new Cliente("Maria", "111.111.111-11");
        Conta conta = new Conta(1, 100.0);

        cliente.adicionarConta(conta);

        assertEquals(1, cliente.getContas().size());
        assertEquals(conta, cliente.getContas().get(0));
    }

    @Test
    void devePermitirVariasContasParaOMesmoCliente() {
        Cliente cliente = new Cliente("Maria", "111.111.111-11");
        Conta conta1 = new Conta(1, 100.0);
        Conta conta2 = new ContaCorrente(2, 200.0, 50.0);

        cliente.adicionarConta(conta1);
        cliente.adicionarConta(conta2);

        assertEquals(2, cliente.getContas().size());
    }

    @Test
    void alteracoesNaContaDevemRefletirNaListaDoCliente() {
        Cliente cliente = new Cliente("Maria", "111.111.111-11");
        Conta conta = new Conta(1, 100.0);
        cliente.adicionarConta(conta);

        conta.depositar(50.0);

        // a conta dentro da lista do cliente é a mesma referência, então o saldo deve refletir
        assertEquals(150.0, cliente.getContas().get(0).getSaldo());
    }
}