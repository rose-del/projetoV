import java.util.List;
import java.util.stream.Collectors;

public class EstadoJogo {
    private List<Carta> mao;
    private List<List<Carta>> mesas;
    private int jogadorAtual;
    private List<Carta> pilhaDescarte;
    private int turno;

    public EstadoJogo(Jogo jogo, Jogador jogador) {
        this.mao = jogador.getMao();
        this.mesas = jogo.getJogadores().stream().map(Jogador::getMesa).collect(Collectors.toList());
        this.jogadorAtual = jogo.getJogadorAtual();
        this.pilhaDescarte = jogo.getPilhaDescarte();
        this.turno = jogo.getTurno(); // Assumindo que a classe Jogo tem um atributo "turno"
    }

    @Override
    public String toString() {
        return "mao:" + mao.stream().map(Carta::toString).collect(Collectors.joining(",")) +
                ";mesas:" + mesas.stream()
                .map(mesa -> mesa.stream().map(Carta::toString).collect(Collectors.joining(",")))
                .collect(Collectors.joining(";")) +
                ";jogadorAtual:" + jogadorAtual +
                ";pilhaDescarte:" + pilhaDescarte.stream().map(Carta::toString).collect(Collectors.joining(",")) +
                ";turno:" + turno;
    }
}