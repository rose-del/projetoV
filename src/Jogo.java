import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Jogo {
    private List<Jogador> jogadores;
    private Baralho baralho;
    private int jogadorAtual;
    private List<Carta> pilhaDescarte;
    private Map<Jogador, Integer> turnoVitoria; // Mapa para armazenar o turno da vitória de cada jogador
    private int turno;

    public Jogo(int numJogadores) {
        this.jogadores = new ArrayList<>();
        for (int i = 0; i < numJogadores; i++) {
            jogadores.add(new Jogador("Jogador " + (i + 1), i));
        }
        this.baralho = new Baralho();
        this.jogadorAtual = 0;
        this.pilhaDescarte = new ArrayList<>();
        this.turnoVitoria = new HashMap<>();
        this.turno = 0;
    }

    public void iniciarJogo() {
        baralho.embaralhar();
        for (Jogador jogador : jogadores) {
            jogador.getMao().addAll(baralho.distribuirCartas(3));
        }
    }

    // Getters para jogadores, baralho e pilhaDescarte
    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public Baralho getBaralho() {
        return baralho;
    }

    public List<Carta> getPilhaDescarte() {
        return pilhaDescarte;
    }

    public void proximoTurno() {
        jogadorAtual = (jogadorAtual + 1) % jogadores.size();
    }

    public int getJogadorAtual() {
        return jogadorAtual;
    }

    public int getTurno() {  // Adicionar o método getTurno()
        return turno;
    }

    public boolean verificarVitoria() {
        List<Jogador> vencedores = new ArrayList<>();

        // Verifica se algum jogador tem os 4 órgãos saudáveis
        for (Jogador jogador : jogadores) {
            if (jogador.getMesa().size() == 4 && !turnoVitoria.containsKey(jogador)) {  // verifica se o jogador ja não teve a vitória registrada
                vencedores.add(jogador);
                turnoVitoria.put(jogador, jogadorAtual); // Registra o turno atual como o turno da vitória do jogador
            }
        }

        // Se houver mais de um vencedor, aplica critérios de desempate
        if (vencedores.size() > 1) {

            //1. Jogador com um ou mais cartas na mão
            vencedores.sort((j1, j2) -> Integer.compare(j2.getMao().size(), j1.getMao().size()));
            if (vencedores.get(0).getMao().size() > vencedores.get(1).getMao().size()) {
                return true; // Já temos um vencedor
            } else {
                // 2. Jogador que completou os órgãos primeiro (implementação simplificada)
                // **Observação:** Essa implementação considera o jogador com menor ID como
                // aquele que completou os órgãos primeiro.
                // Idealmente, você deve registrar o turno em que cada jogador completou
                // os órgãos para um desempate mais preciso.
                vencedores.sort((j1, j2) -> Integer.compare(turnoVitoria.get(j1), turnoVitoria.get(j2))); // Ordena pelo turno da vitória
                return true; // Já temos um vencedor
            }
        } else if (vencedores.size() == 1) {
            return true;    // Há um único vencedor
        }
        return false;   // Não há vencedor ainda
    }

    public boolean validarJogada(Jogador jogador, Carta carta, int alvo) {
        if (!jogador.getMao().contains(carta)) {
            return false;   // Jogador não possui a carta na mão
        }

        switch (carta.getTipo()) {
            case ÓRGÃO:
                return alvo == jogador.getId(); // Só pode jogar órgão na própria mesa

            case VÍRUS:
                if (alvo == jogador.getId()) {
                    return false;   // Não pode jogar vírus em si mesmo
                }
                Jogador jogadorAlvo = jogadores.get(alvo);
                List<Carta> mesaAlvo = jogadorAlvo.getMesa();
                if (mesaAlvo.isEmpty()) {
                    return true; // Pode jogar vírus em mesa vazia
                }

                // Verificar se já existe um órgão da mesma cor na mesa do alvo (exceto se for universal)
                return carta.isEspecial() || mesaAlvo.stream()
                        .filter(c -> c.getTipo() == Carta.Tipo.ÓRGÃO)
                        .noneMatch(c -> c.getCor() == carta.getCor());

            case REMÉDIO:
                // Pode jogar remédio em si ou em outro jogador
                Jogador jogadorRemedio = jogadores.get(alvo);
                List<Carta> mesaRemedio = jogadorRemedio.getMesa();

                if (mesaRemedio.isEmpty()) {
                    return true;    // Pode jogar remédio em mesa vazia (imunidade)
                }

                // Verificar se existe um órgão ou vírus da mesma cor na mesa do alvo (exceto se for universal)
                return carta.isEspecial() || mesaRemedio.stream()
                        .anyMatch(c -> c.getTipo() != Carta.Tipo.REMÉDIO && c.getCor() == carta.getCor());

            default:
                return false;   // tipo de carta inválido
        }
    }

    public void aplicarJogada(Jogador jogador, Carta carta, int alvo) {
        jogador.removerCarta(carta);    // Remove a carta da mão do jogador

        if (carta.getTipo() == Carta.Tipo.ÓRGÃO) {
            jogador.getMesa().add(carta); // Adiciona o órgão à mesa do jogador
        } else {
            Jogador jogadorAlvo = jogadores.get(alvo);
            List<Carta> mesaAlvo = jogadorAlvo.getMesa();

            if (carta.getTipo() == Carta.Tipo.VÍRUS) {
                mesaAlvo.add(carta); // Adiciona o vírus à mesa do alvo

                long numVirus = mesaAlvo.stream()
                        .filter(c -> c.getTipo() == Carta.Tipo.VÍRUS)
                        .count();

                if (numVirus >= 2) {
                    mesaAlvo.clear(); // Remove todas as cartas do órgão
                    pilhaDescarte.addAll(mesaAlvo); // Adiciona as cartas à pilha de descarte
                }
            } else if (carta.getTipo() == Carta.Tipo.REMÉDIO){
                if (mesaAlvo.stream().anyMatch(c -> c.getTipo() == Carta.Tipo.VÍRUS)) {

                    // Remove o vírus e o remédio
                    mesaAlvo.removeIf(c -> c.getTipo() == Carta.Tipo.VÍRUS || c.getTipo() == Carta.Tipo.REMÉDIO);
                    pilhaDescarte.add(carta);
                } else {
                    mesaAlvo.add(carta);    // Adiciona o remédio à mesa (imunidade)
                }
            }
        }
    }

    public void finalizarJogo() {

        // 1. Verificar o vencedor
        List<Jogador> vencedores = new ArrayList<>();
        for (Jogador jogador : jogadores) {
            if (jogador.getMesa().size() == 4) {
                vencedores.add(jogador);
            }
        }

        // 2. Anunciar o vencedor (ou empate)
        if (vencedores.size() == 1) {
            System.out.println("O jogador " + vencedores.get(0).getNome() + " venceu!");
        } else if (vencedores.size() > 1) {

            // Aplicar critérios de desempate (implementado em verificarVitoria())
            verificarVitoria();
            System.out.println("Empate entre os jogadores: " + vencedores.stream()
                    .map(Jogador::getNome)
                    .collect(Collectors.joining(", ")));
        } else {
            System.out.println("O jogo terminou sem vencedores.");
        }

        // 3. Encerrar a conexão com os jogadores (se aplicável)
        // ... (implementar lógica para encerrar a conexão com os clientes) ...

        // 4. Limpar os dados do jogo (opcional)
        // ... (implementar lógica para reiniciar o jogo ou limpar os dados) ...
    }
}
