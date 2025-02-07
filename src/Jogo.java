import java.io.*;
import java.net.*;
import java.util.*;

public class Jogo {
    private List<Socket> jogadores = new ArrayList<>();
    private List<Carta> baralho;
    private Map<Socket, List<Carta>> maosJogadores = new HashMap<>();
    private Map<Socket, List<Carta>> camposJogadores = new HashMap<>();
    private List<Socket> ordemJogadores = new ArrayList<>();
    private int indiceJogadorAtual;

    public Jogo() {
        this.baralho = new ArrayList<>(); // Inicializa apenas quando necessário
    }

    public void adicionarJogador(Socket jogador) {
        jogadores.add(jogador);
        maosJogadores.put(jogador, new ArrayList<>());
        camposJogadores.put(jogador, new ArrayList<>());
    }

    public void removerJogadores(List<Socket> jogadoresRemovidos) {
        for (Socket jogador : jogadoresRemovidos) {
            jogadores.remove(jogador);
            maosJogadores.remove(jogador);
            camposJogadores.remove(jogador);
        }
        System.out.println("Jogadores removidos: " + jogadoresRemovidos.size());
    }

    public void iniciarJogo() {
        criarBaralho();
        embaralharBaralho();
        distribuirCartas();
        definirOrdemJogadores();
        System.out.println("O jogo está começando com " + jogadores.size() + " jogadores!");
        System.out.println("Cartas restantes no baralho: " + baralho.size());
        enviarEstadoParaJogadores();
        iniciarTurno();
    }

    private void enviarEstadoParaJogadores() {
        for (Socket jogador : jogadores) {
            try {
                PrintWriter saida = new PrintWriter(jogador.getOutputStream(), true);
                List<Carta> mao = maosJogadores.get(jogador);
                List<Carta> campo = camposJogadores.get(jogador);
                saida.println("Suas cartas: " + mao);
                saida.println("========== Campo ==========");
                for (Socket outroJogador : jogadores) {
                    if (outroJogador.equals(jogador)) {
                        saida.println("Meu Campo: " + (campo.isEmpty() ? "Sem Cartas" : campo));
                    } else {
                        List<Carta> campoOutro = camposJogadores.get(outroJogador);
                        saida.println("Jogador " + (jogadores.indexOf(outroJogador) + 1) + ": " + (campoOutro.isEmpty() ? "Sem Cartas" : campoOutro));
                    }
                }
                saida.println("=========================");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void definirOrdemJogadores() {
        ordemJogadores.addAll(jogadores);
        Collections.shuffle(ordemJogadores);
        indiceJogadorAtual = new Random().nextInt(ordemJogadores.size());
        System.out.println("Ordem dos jogadores definida:");
        for (int i = 0; i < ordemJogadores.size(); i++) {
            System.out.println("Jogador " + (jogadores.indexOf(ordemJogadores.get(i)) + 1));
        }
        System.out.println("Jogador inicial: " + (jogadores.indexOf(ordemJogadores.get(indiceJogadorAtual)) + 1));
    }

    private void criarBaralho() {
        for (int i = 0; i < 5; i++) {
            baralho.add(new Carta(Carta.Tipo.CORACAO, Carta.Cor.VERMELHO));
            baralho.add(new Carta(Carta.Tipo.OSSO, Carta.Cor.AMARELO));
            baralho.add(new Carta(Carta.Tipo.CEREBRO, Carta.Cor.AZUL));
            baralho.add(new Carta(Carta.Tipo.ESTOMAGO, Carta.Cor.VERDE));
        }

        for (int i = 0; i < 4; i++) {
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.VERMELHO));
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.AMARELO));
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.AZUL));
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.VERDE));
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.VERMELHO));
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.AMARELO));
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.AZUL));
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.VERDE));
        }

        baralho.add(new Carta(Carta.Tipo.UNIVERSAL, Carta.Cor.UNIVERSAL));
        baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.UNIVERSAL));
        baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.UNIVERSAL));

        System.out.println("Baralho criado com " + baralho.size() + " cartas.");
    }

    private void embaralharBaralho() {
        Collections.shuffle(baralho);
        System.out.println("Baralho embaralhado!");
    }

    private void distribuirCartas() {
        for (Socket jogador : jogadores) {
            List<Carta> mao = maosJogadores.get(jogador);
            System.out.println("Distribuindo cartas para jogador...");
            for (int i = 0; i < 3; i++) {
                if (!baralho.isEmpty()) {
                    Carta cartaDistribuida = baralho.remove(0);
                    mao.add(cartaDistribuida);
                    System.out.println("Carta dada: " + cartaDistribuida);
                }
            }
        }
        System.out.println("Cartas distribuídas! Cartas restantes no baralho: " + baralho.size());
    }

    private void iniciarTurno() {
        Socket jogadorAtual = ordemJogadores.get(indiceJogadorAtual);
        try {
            PrintWriter saida = new PrintWriter(jogadorAtual.getOutputStream(), true);
            saida.println("Seu turno começou! Escolha uma ação.");
            System.out.println("Turno do jogador " + (jogadores.indexOf(jogadorAtual) + 1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encerrarJogo() {
        System.out.println("Encerrando o jogo...");
        try {
            for (Socket jogador : jogadores) {
                if (!jogador.isClosed()) {
                    jogador.close();
                }
            }
            System.out.println("Jogo encerrado.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
