import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteJogo {
    private static final String SERVIDOR_IP = "127.0.0.1";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVIDOR_IP, PORTA);
            System.out.println("Conectado ao servidor!");

            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            // Thread para ouvir mensagens do servidor
            new Thread(() -> {
                try {
                    String mensagem;
                    while ((mensagem = entrada.readLine()) != null) {
                        System.out.println(mensagem);

                        // Se receber a mensagem de remoção, encerrar o cliente
                        if (mensagem.equals("VOCÊ FOI REMOVIDO DO JOGO.")) {
                            System.out.println("Você recusou jogar e foi removido. Encerrando cliente...");
                            socket.close();
                            System.exit(0);
                        }

                        // Se receber cartas iniciais, formatar e exibir de forma mais clara
                        if (mensagem.startsWith("Suas cartas iniciais:")) {
                            String cartas = mensagem.replace("Suas cartas iniciais:", "").trim();
                            System.out.println("\n=== Suas Cartas Iniciais ===");
                            Arrays.stream(cartas.split(","))
                                    .map(String::trim)
                                    .forEach(carta -> System.out.println("- " + carta));
                            System.out.println("===========================\n");
                        }

                        // Se o servidor perguntar se deseja iniciar o jogo, pedir entrada do usuário
                        if (mensagem.startsWith("Iniciar jogo?")) {
                            System.out.print("Digite S para iniciar ou N para sair: ");
                            String resposta = scanner.nextLine().trim();
                            saida.println(resposta);

                            // Se o jogador digitou "N", encerrar imediatamente
                            if (resposta.equalsIgnoreCase("N")) {
                                System.out.println("Você escolheu sair. Encerrando cliente...");
                                socket.close();
                                System.exit(0);
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Conexão com o servidor encerrada.");
                }
            }).start();

            // Loop para enviar mensagens ao servidor
            while (true) {
                if (scanner.hasNextLine()) {
                    String mensagem = scanner.nextLine().trim();
                    saida.println(mensagem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}