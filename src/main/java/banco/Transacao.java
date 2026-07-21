package banco;

import java.time.LocalDateTime;

public class Transacao {

    private TipoTransacao tipo;
    private double valor;
    private LocalDateTime dataHora;

    public Transacao(TipoTransacao tipo, double valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    @Override
    public String toString() {
        return dataHora + " - " + tipo + " - R$ " + valor;
    }
}