package cartas;

public class Orgao extends Carta {
    private String cor;
    private boolean infectado;

    public Orgao(String nome, String cor) {
        super(nome);
        this.cor = cor;
        this.infectado = false;
    }

    public String getCor() {
        return cor;
    }

    public boolean isInfectado() {
        return infectado;
    }

    //marca o órgão como infectado.
    public void infectar() {
        this.infectado = true;
    }

    //remove a infecção.
    public void curar() {
        this.infectado = false;
    }

    //adiciona o órgão ao jogador que jogou a carta.?
    @Override
    public void jogar(Jogador jogador, Jogador alvo) {
        jogador.adicionarOrgao(this);
    }
}

