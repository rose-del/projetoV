public class Carta {

    public enum Cor {
        VERMELHO, AZUL, VERDE, AMARELO, UNIVERSAL
    }

    public enum Tipo {
        ÓRGÃO, VÍRUS, REMÉDIO
    }

    private Cor cor;
    private Tipo tipo;
    private boolean especial;

    public Carta(Cor cor, Tipo tipo, boolean especial) {
        this.cor = cor;
        this.tipo = tipo;
        this.especial = especial;
    }

    public Cor getCor() {
        return cor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public boolean isEspecial() {
        return especial;
    }

    @Override
    public String toString() {
        String nome = "";

        switch (tipo) {
            case ÓRGÃO:
                nome = (especial ? "Órgão Universal" : "Órgão " + cor);
                break;

            case VÍRUS:
                nome = (especial ? "Vírus Universal" : "Vírus " + cor);
                break;

            case REMÉDIO:
                nome = (especial ? "Remédio Universal" : "Remédio " + cor);
        }

        return nome;
    }
}