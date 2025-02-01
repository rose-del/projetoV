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
