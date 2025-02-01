package cartas;

import java.util.ArrayList;
import java.util.List;

public class Jogador {
    private String nome;
    private List<Carta> mao;
    private List<Orgao> orgaos;

    public Jogador(String nome) {
        this.nome = nome;
        this.mao = new ArrayList<>();
        this.orgaos = new ArrayList<>();
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public List<Carta> getMao() {
        return mao;
    }

    public List<Orgao> getOrgaos() {
        return orgaos;
    }

    public void adicionarCarta(Carta carta) {
        mao.add(carta);
    }

    public void descartarCarta(Carta carta) {
        mao.remove(carta);
    }

    public void adicionarOrgao(Orgao orgao) {
        orgaos.add(orgao);
    }

    public void removerOrgao(Orgao orgao) {
        orgaos.remove(orgao);
    }
}
