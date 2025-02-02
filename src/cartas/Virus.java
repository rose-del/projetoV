package cartas;

public class Virus extends Carta {
    private String cor;

    public Virus(String nome, String cor) {
        super(nome);
        this.cor = cor;
    }

    public String getCor() {
        return cor;
    }

    /**
     * O metodo jogar() percorre os órgãos do jogador alvo e,
     * se encontrar um órgão com a mesma cor do vírus, o infecta.
     * @param jogador
     * @param alvo
     */
    @Override
    public void jogar(Jogador jogador, Jogador alvo) {
        for (Orgao orgao : alvo.getOrgaos()) {
            if (orgao.getCor().equals(this.cor)) {
                orgao.infectar();
                break;
            }
        }
    }
}
