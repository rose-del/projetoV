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

    public void infectar() {
        this.infectado = true;
    }

    public void curar() {
        this.infectado = false;
    }

    @Override
    public void jogar(Jogador jogador, Jogador alvo) {
        jogador.adicionarOrgao(this);
    }
}

