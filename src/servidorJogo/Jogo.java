package servidorJogo;

import java.io.*;
import java.net.*;
import java.util.*;

import telas.TelaEscolha;

public class Jogo {
    private List<Socket> jogadores = new ArrayList<>();
    private List<Carta> baralho;
    private Map<Socket, List<Carta>> maosJogadores = new HashMap<>();

    public Jogo() {
        this.baralho = new ArrayList<>(); // Inicializa apenas quando necessário
    }

    public void adicionarJogador(Socket jogador) {
        jogadores.add(jogador);
        maosJogadores.put(jogador, new ArrayList<>());
    }

    public void removerJogadores(List<Socket> jogadoresRemovidos) {
        for (Socket jogador : jogadoresRemovidos) {
            jogadores.remove(jogador);
            maosJogadores.remove(jogador);
        }
        System.out.println("Jogadores removidos: " + jogadoresRemovidos.size());
    }

    public void iniciarJogo() {

        criarBaralho();
        embaralharBaralho();
        distribuirCartas();
        System.out.println("O jogo está começando com " + jogadores.size() + " jogadores!");
        System.out.println("Cartas restantes no baralho: " + baralho.size());
        enviarCartasParaJogadores();
    }

    private void criarBaralho() {
        // Criar cartas de órgãos (20 cartas: 5 de cada tipo fixo)
        for (int i = 0; i < 5; i++) {
            baralho.add(new Carta(Carta.Tipo.CORACAO, Carta.Cor.VERMELHO));
            baralho.add(new Carta(Carta.Tipo.OSSO, Carta.Cor.AMARELO));
            baralho.add(new Carta(Carta.Tipo.CEREBRO, Carta.Cor.AZUL));
            baralho.add(new Carta(Carta.Tipo.ESTOMAGO, Carta.Cor.VERDE));
        }

        // Criar cartas de vírus (16 cartas: 4 de cada cor)
        for (int i = 0; i < 4; i++) {
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.VERMELHO));
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.AMARELO));
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.AZUL));
            baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.VERDE));
        }

        // Criar cartas de remédio (16 cartas: 4 de cada cor)
        for (int i = 0; i < 4; i++) {
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.VERMELHO));
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.AMARELO));
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.AZUL));
            baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.VERDE));
        }

        // Criar cartas universais (3 cartas: 1 órgão, 1 vírus e 1 remédio)
        baralho.add(new Carta(Carta.Tipo.UNIVERSAL, Carta.Cor.UNIVERSAL)); // Órgão universal
        baralho.add(new Carta(Carta.Tipo.VIRUS, Carta.Cor.UNIVERSAL)); // Vírus universal
        baralho.add(new Carta(Carta.Tipo.REMEDIO, Carta.Cor.UNIVERSAL)); // Remédio universal

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

    private void enviarCartasParaJogadores() {
        for (Socket jogador : jogadores) {
            try {
                PrintWriter saida = new PrintWriter(jogador.getOutputStream(), true);
                List<Carta> mao = maosJogadores.get(jogador);
                saida.println("Suas cartas iniciais: " + mao);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
