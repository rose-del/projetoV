package servidorJogo;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServidorJogo {
    private static final int PORTA = 12345;
    private static final int MIN_JOGADORES = 2;
    private static final int MAX_JOGADORES = 4;
    private static final int TEMPO_ESPERA_MS = 60000; // 1 minuto
    private static List<Socket> jogadores = new ArrayList<>();
    private static Map<Socket, Integer> idsJogadores = new HashMap<>();
    private static Map<Socket, String> respostasJogadores = new HashMap<>();
    private static ServerSocket servidor;
    private static Jogo jogo;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean tempoExpirado = false;

    public static void main(String[] args) {
        try {
            servidor = new ServerSocket(PORTA);
            jogo = new Jogo();
            System.out.println("Servidor iniciado na porta " + PORTA);
            System.out.println("Aguardando jogadores... (Mínimo: " + MIN_JOGADORES + ", Máximo: " + MAX_JOGADORES + ")");

            while (jogadores.size() < MAX_JOGADORES) {
                Socket jogador = servidor.accept();
                if (tempoExpirado) {
                    jogador.close();
                    continue;
                }

                jogadores.add(jogador);
                int id = jogadores.size();
                idsJogadores.put(jogador, id);
                jogo.adicionarJogador(jogador);
                respostasJogadores.put(jogador, "");
                System.out.println("Jogador " + id + " conectado!");
                new Thread(new GerenciadorJogador(jogador, id)).start();

                if (jogadores.size() == MIN_JOGADORES) {
                    iniciarContagemTempo();
                }

                if (jogadores.size() == MAX_JOGADORES) {
                    scheduler.shutdownNow();
                    perguntarInicioJogo();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void iniciarContagemTempo() {
        System.out.println("Mínimo de jogadores atingido! Esperando até 1 minuto por mais conexões...");
        scheduler.schedule(() -> {
            tempoExpirado = true;
            if (jogadores.size() >= MIN_JOGADORES) {
                System.out.println("Tempo esgotado! Iniciando votação para começar o jogo.");
                perguntarInicioJogo();
            } else {
                System.out.println("Tempo esgotado! Jogadores insuficientes. Encerrando servidor.");
                jogo.encerrarJogo();
            }
        }, TEMPO_ESPERA_MS, TimeUnit.MILLISECONDS);
    }

    private static void perguntarInicioJogo() {
        System.out.println("Perguntando aos jogadores se querem iniciar o jogo...");
        for (Socket jogador : new ArrayList<>(jogadores)) {
            try {
                PrintWriter saida = new PrintWriter(jogador.getOutputStream(), true);
                saida.println("Iniciar jogo? (S/N)");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void registrarResposta(Socket jogador, String resposta) {
        respostasJogadores.put(jogador, resposta);
        verificarRespostas();
    }

    private static void verificarRespostas() {
        List<Socket> jogadoresRemovidos = new ArrayList<>();

        for (Socket jogador : new ArrayList<>(jogadores)) {
            if ("N".equalsIgnoreCase(respostasJogadores.get(jogador))) {
                System.out.println("Jogador recusou jogar e será removido: " + jogador);
                jogadoresRemovidos.add(jogador);
                try {
                    jogador.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        jogadores.removeAll(jogadoresRemovidos);
        respostasJogadores.keySet().removeAll(jogadoresRemovidos);
        jogo.removerJogadores(jogadoresRemovidos);

        if (jogadores.size() < MIN_JOGADORES) {
            System.out.println("Jogadores insuficientes após recusas. Encerrando servidor.");
            jogo.encerrarJogo();
            return;
        }

        if (!respostasJogadores.containsValue("")) {
            System.out.println("Todos aceitaram iniciar o jogo! Começando...");
            jogo.iniciarJogo();
        }
    }
}