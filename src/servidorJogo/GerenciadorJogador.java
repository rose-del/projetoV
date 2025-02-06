package servidorJogo;

import java.io.*;
import java.net.*;

public class GerenciadorJogador implements Runnable {
    private Socket socket;
    private int idJogador;

    public GerenciadorJogador(Socket socket, int id) {
        this.socket = socket;
        this.idJogador = id;
    }

    @Override
    public void run() {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);

            saida.println("Bem-vindo ao jogo! Seu ID é " + idJogador + ". Aguarde o início.");

            while (true) {
                String mensagem = entrada.readLine();
                if (mensagem == null) {
                    System.out.println("Jogador " + idJogador + " desconectou.");
                    socket.close();
                    break;
                }
                System.out.println("Jogador " + idJogador + ": " + mensagem);

                if (mensagem.equalsIgnoreCase("S") || mensagem.equalsIgnoreCase("N")) {
                    ServidorJogo.registrarResposta(socket, mensagem);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro de conexão com o jogador " + idJogador + ".");
        }
    }
}