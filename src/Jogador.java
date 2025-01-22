import java.util.ArrayList;
import java.util.List;

public class Jogador {

    private String nome;
    private List<Carta> mao;
    private List<Carta> mesa;
    private int id;

    public Jogador(String nome, int id) {
        this.nome = nome;
        this.mao = new ArrayList<>();
        this.mesa = new ArrayList<>();
        this.id = id;
    }


    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Carta> getMao() {
        return mao;
    }

    public List<Carta> getMesa() {
        return mesa;
    }

    public void setMesa(List<Carta> mesa) {
        this.mesa = mesa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void adicionarCarta(Carta carta) {
        mao.add(carta);
    }

    public void removerCarta(Carta carta) {
        mao.remove(carta);
    }

    public void descartarCartas(List<Carta> cartasDescartadas, Jogo jogo) {
        mao.removeAll(cartasDescartadas);
        jogo.getPilhaDescarte().addAll(cartasDescartadas);

        for (int i = 0; i < cartasDescartadas.size(); i++) {
            if (!jogo.getBaralho().getCartas().isEmpty()) {
                mao.add(jogo.getBaralho().compraCarta());
            }
        }
    }

    public void jogarCarta(Carta carta, int alvo, Jogo jogo) {
        if (carta != null && mao.contains(carta)) {
            if (jogo.validarJogada(this, carta, alvo)) {
                mao.remove(carta);

                if (carta.getTipo() == Carta.Tipo.ÓRGÃO) {
                    mesa.add(carta);
                } else {
                    Jogador jogadorAlvo = jogo.getJogadores().get(alvo);
                    List<Carta> mesaAlvo = jogadorAlvo.getMesa();

                    if (carta.getTipo() == Carta.Tipo.VÍRUS) {

                        // Lógica para adicionar o vírus à mesa do jogador alvo
                        mesaAlvo.add(carta);

                        int numVirus = (int) mesaAlvo.stream()
                                .filter(c -> c.getTipo() == Carta.Tipo.VÍRUS)
                                .count();

                        if (numVirus >= 2) {
                            mesaAlvo.clear();   // Descarta o órgão (remove todas as cartas)
                            jogo.getPilhaDescarte().addAll(mesaAlvo);
                        }
                    } else if (carta.getTipo() == Carta.Tipo.REMÉDIO) {

                        // Lógica para aplicar o remédio na mesa do jogador alvo
                        if (mesaAlvo.stream().anyMatch(c -> c.getTipo() == Carta.Tipo.VÍRUS)) {

                            // Remove o vírus e o remédio da mesa
                            mesaAlvo.removeIf(c -> c.getTipo() == Carta.Tipo.VÍRUS || c.getTipo() == Carta.Tipo.REMÉDIO);
                            jogo.getPilhaDescarte().add(carta); // Adiciona o remédio à pilha de descarte
                        } else {

                            // Adiciona o remédio à mesa (imunidade)
                            mesaAlvo.add(carta);
                        }
                    }
                }

                // Após jogar a carta, o jogador compra outra do baralho
                if (!jogo.getBaralho().getCartas().isEmpty()) {
                    mao.add(jogo.getBaralho().compraCarta());
                }
            } else {
                // Jogada inválida, exibir mensagem de erro
                System.out.println("Jogada inválida!");
            }
        }
    }

    public void realizarAcao(int tipoAcao, Carta carta, int alvo, Jogo jogo) {
        switch (tipoAcao) {
            case 1: // Jogar carta
                jogarCarta(carta, alvo, jogo);
                break;

            case 2: // Descartar cartas
                List<Carta> cartasDescartadas = new ArrayList<>();
                cartasDescartadas.add(carta);   // Supondo que a carta a ser descartada seja passada como parâmetro
                descartarCartas(cartasDescartadas, jogo);
                break;

            case 3: // Passar a vez
                // Não precisa fazer nada aqui, apenas passa para o próximo jogador
                break;

            default:
                System.out.println("Ação inválida!");
        }
    }
}