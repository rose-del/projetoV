package cartas;

public abstract class Carta {
    private String nome;

    public Carta(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public abstract void jogar(Jogador jogador, Jogador alvo);
}


