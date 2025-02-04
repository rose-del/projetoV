public class Carta {
    enum Tipo { CORACAO, OSSO, ESTOMAGO, CEREBRO, VIRUS, REMEDIO, UNIVERSAL }
    enum Cor { VERMELHO, AMARELO, VERDE, AZUL, UNIVERSAL }

    private Tipo tipo;
    private Cor cor;
    private boolean especial;

    public Carta(Tipo tipo, Cor cor) {
        this.tipo = tipo;
        this.cor = cor;
        this.especial = (tipo == Tipo.UNIVERSAL && cor == Cor.UNIVERSAL);
    }

    public Tipo getTipo() {
        return tipo;
    }

    public Cor getCor() {
        return cor;
    }

    public boolean isEspecial() {
        return especial;
    }

    @Override
    public String toString() {
        return (especial ? "ESPECIAL - " : tipo + " - ") + cor;
    }
}
