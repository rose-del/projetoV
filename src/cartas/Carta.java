package cartas;

public abstract class Carta {
    private String nome;

    public Carta(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    /**
     * Define um metodo abstrato jogar(),
     * que será implementado nas subclasses.
     * @param jogador
     * @param alvo
     */
    public abstract void jogar(Jogador jogador, Jogador alvo);
}


