import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Baralho {
    private List<Carta> cartas;

    public Baralho() {
        this.cartas = new ArrayList<>();
        criarCartas();
    }

    public void criarCartas() {

        // Criar cartas de órgãos
        for (Carta.Cor cor : Carta.Cor.values()) {
            if (cor != Carta.Cor.UNIVERSAL) {
                for (int i = 0; i < 5; i++) {
                    cartas.add(new Carta(cor, Carta.Tipo.ÓRGÃO, false));
                }
            }
        }

        // Criar cartas de vírus e remédios
        for (Carta.Cor cor : Carta.Cor.values()) {
            if (cor != Carta.Cor.UNIVERSAL) {
                for (int i = 0; i < 4; i++) {
                    cartas.add(new Carta(cor, Carta.Tipo.VÍRUS, false));
                    cartas.add(new Carta(cor, Carta.Tipo.REMÉDIO, false));
                }
            }
        }

        // criar cartas especiais
        cartas.add(new Carta(Carta.Cor.UNIVERSAL, Carta.Tipo.ÓRGÃO, true));
        cartas.add(new Carta(Carta.Cor.UNIVERSAL, Carta.Tipo.VÍRUS, true));
        cartas.add(new Carta(Carta.Cor.UNIVERSAL, Carta.Tipo.REMÉDIO, true));
    }

    public void embaralhar() {
        Collections.shuffle(cartas);
    }

    public List<Carta> distribuirCartas(int numCartas) {
        List<Carta> cartasDistribuidas = new ArrayList<>();

        for (int i = 0; i < numCartas; i++) {
            if (!cartas.isEmpty()) {    // Verifica se  ainda há cartas no baralho
                cartasDistribuidas.add(cartas.remove(0));
            }
        }
        return cartasDistribuidas;
    }

    public Carta compraCarta() {
        if (!cartas.isEmpty()) {    // Verifica se ainda há cartas no baralho
            return cartas.remove(0);
        } else {
            return null;    // Ou lançar uma exceção indicando que o baralho está vazio
        }
    }

    // Getter para cartas
    public List<Carta> getCartas() {
        return cartas;
    }

}
