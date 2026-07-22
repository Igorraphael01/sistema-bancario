package banco;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TransacaoTest {

    @Test
    void deveCriarTransacaoComTipoEValorCorretos() {
        Transacao transacao = new Transacao(TipoTransacao.DEPOSITO, 100.0);

        assertEquals(TipoTransacao.DEPOSITO, transacao.getTipo());
        assertEquals(100.0, transacao.getValor());
    }

    @Test
    void devePreencherDataHoraAutomaticamente() {
        LocalDateTime antes = LocalDateTime.now();

        Transacao transacao = new Transacao(TipoTransacao.SAQUE, 50.0);

        LocalDateTime depois = LocalDateTime.now();

        // a data/hora da transação deve estar entre o instante "antes" e "depois" do teste
        assertFalse(transacao.getDataHora().isBefore(antes));
        assertFalse(transacao.getDataHora().isAfter(depois));
    }

    @Test
    void toStringDeveConterTipoEValor() {
        Transacao transacao = new Transacao(TipoTransacao.DEPOSITO, 75.5);

        String texto = transacao.toString();

        assertTrue(texto.contains("DEPOSITO"));
        assertTrue(texto.contains("75.5"));
    }

    @Test
    void doisTiposDiferentesDevemSerDistinguidos() {
        Transacao deposito = new Transacao(TipoTransacao.DEPOSITO, 100.0);
        Transacao saque = new Transacao(TipoTransacao.SAQUE, 100.0);

        assertNotEquals(deposito.getTipo(), saque.getTipo());
    }
}