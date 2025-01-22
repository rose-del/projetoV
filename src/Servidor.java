import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Servidor {
    private ServerSocket serverSocket;
    private List<Socket> clientes;
    private Jogo jogo;
    private int numJogadores;
    private ExecutorService executor;

    public Servidor(int porta, int numJogadores) {
        this.clientes = new ArrayList<>();
        this.numJogadores = numJogadores;
        this.executor = Executors.newFixedThreadPool(numJogadores);

        try {
            this.serverSocket = new ServerSocket(porta);
            System.out.println("Servidor iniciado na porta " + porta);
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    public void iniciarServidor() {
        try {
            aguardarConexoes();
            iniciarJogo();
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        } finally {
            encerrarServidor();
        }
    }

    private void aguardarConexoes() throws IOException {
        System.out.println("Aguardando conexões dos clientes...");
        while (clientes.size() < numJogadores) {
            Socket cliente = serverSocket.accept();
            clientes.add(cliente);
            System.out.println("Cliente conectado: " + cliente.getInetAddress().getHostAddress());
        }
        System.out.println("Todos os clientes conectados!");
    }

    private void iniciarJogo() throws IOException {
        System.out.println("Iniciando o jogo...");

        jogo = new Jogo(numJogadores);

        // Iniciar o jogo para cada cliente em uma thread separada
        for (int i = 0; i < clientes.size(); i++) {
            Socket cliente = clientes.get(i);
            int jogadorId = i; // ID do jogador

            executor.execute(() -> {
                // Enviar mensagem para o cliente para iniciar o jogo
                enviarMensagem("INICIAR_JOGO", cliente);

                // Loop para receber e processar as ações do jogador
                while (!jogo.verificarVitoria()) {
                    // 1. Enviar o estado do jogo para o jogador
                    enviarEstadoJogo(jogo.getJogadores().get(jogadorId));

                    // 2. Receber a ação do jogador
                    Object mensagem = receberMensagem(cliente);

                    // 3. Processar a ação do jogador
                    if (mensagem instanceof String) {
                        String[] partes = ((String) mensagem).split(";");
                        if (partes.length == 4 && partes[0].equals("JOGAR_CARTA")) {
                            int tipoAcao = Integer.parseInt(partes[1]);
                            Carta carta = null;
                            int alvo = Integer.parseInt(partes[3]);

                            // Encontrar a carta na mão do jogador
                            for (Carta c : jogo.getJogadores().get(jogadorId).getMao()) {
                                if (c.toString().equals(partes[2])) {
                                    carta = c;
                                    break;
                                }
                            }

                            if (carta != null) {
                                processarAcao(jogo.getJogadores().get(jogadorId), tipoAcao, carta, alvo);
                            } else {
                                enviarMensagem("CARTA_INVALIDA", cliente);
                            }
                        } else if (partes.length == 3 && partes[0].equals("DESCARTAR_CARTA")) {
                            int tipoAcao = Integer.parseInt(partes[1]);
                            Carta carta = null;

                            // Encontrar a carta na mão do jogador
                            for (Carta c : jogo.getJogadores().get(jogadorId).getMao()) {
                                if (c.toString().equals(partes[2])) {
                                    carta = c;
                                    break;
                                }
                            }

                            if (carta != null) {
                                processarAcao(jogo.getJogadores().get(jogadorId), tipoAcao, carta, -1); // Alvo irrelevante para descartar
                            } else {
                                enviarMensagem("CARTA_INVALIDA", cliente);
                            }
                        } else {
                            enviarMensagem("MENSAGEM_INVALIDA", cliente);
                        }
                    } else {
                        enviarMensagem("MENSAGEM_INVALIDA", cliente);
                    }

                    // 5. Completar a mão do jogador (se necessário)
                    int cartasParaComprar = 3 - jogo.getJogadores().get(jogadorId).getMao().size();
                    if (cartasParaComprar > 0) {
                        List<Carta> novasCartas = jogo.getBaralho().distribuirCartas(cartasParaComprar);
                        if (novasCartas != null) {
                            jogo.getJogadores().get(jogadorId).getMao().addAll(novasCartas);

                            // Enviar as novas cartas para o jogador (string formatada)
                            String cartasString = novasCartas.stream()
                                    .map(Carta::toString)
                                    .collect(Collectors.joining(";"));
                            enviarMensagem("NOVAS_CARTAS:" + cartasString, cliente);
                        }
                    }
                }
                jogo.finalizarJogo(); // Finalizar o jogo (dentro da thread)
            });
        }
    }

    private void enviarMensagem(Object mensagem, Socket socket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(mensagem);
            out.flush();
        } catch (IOException e) {
            System.err.println("Erro ao enviar mensagem para o cliente " + socket.getInetAddress().getHostAddress() + ": " + e.getMessage());
        }
    }

    private Object receberMensagem(Socket socket) {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao receber mensagem do cliente " + socket.getInetAddress().getHostAddress() + ": " + e.getMessage());
            return null;
        }
    }

    private void processarAcao(Jogador jogador, int tipoAcao, Carta carta, int alvo) {
        switch (tipoAcao) {
            case 1: // Jogar carta
                if (jogo.validarJogada(jogador, carta, alvo)) {
                    jogo.aplicarJogada(jogador, carta, alvo);
                    enviarMensagem("JOGADA_VALIDA", clientes.get(jogador.getId()));
                    // enviarEstadoJogo(); // Talvez seja necessário enviar o estado do jogo aqui também
                } else {
                    enviarMensagem("JOGADA_INVALIDA", clientes.get(jogador.getId()));
                }
                break;
            case 2: // Descartar cartas
                List<Carta> cartasDescartadas = new ArrayList<>();
                cartasDescartadas.add(carta);
                jogador.descartarCartas(cartasDescartadas, jogo);
                enviarMensagem("JOGADA_VALIDA", clientes.get(jogador.getId()));
                // enviarEstadoJogo(); // Talvez seja necessário enviar o estado do jogo aqui também
                break;
            case 3: // Passar a vez
                // Não precisa fazer nada aqui, o turno já é passado no final do loop em iniciarJogo()
                break;
            default:
                enviarMensagem("MENSAGEM_INVALIDA", clientes.get(jogador.getId()));
        }
    }

    private void enviarEstadoJogo() {
        for (Socket cliente : clientes) {
            enviarEstadoJogo(jogo.getJogadores().get(clientes.indexOf(cliente)));
        }
    }

    private void enviarEstadoJogo(Jogador jogador) {
        try {
            // 1. Criar um objeto com o estado do jogo
            EstadoJogo estado = new EstadoJogo(jogo, jogador); // Classe EstadoJogo precisa ser criada

            // 2. Enviar o estado do jogo para o cliente
            ObjectOutputStream out = new ObjectOutputStream(clientes.get(jogador.getId()).getOutputStream());
            out.writeObject("ESTADO_JOGO:" + estado.toString()); // Envia o estado como string
            out.flush();
        } catch (IOException e) {
            System.err.println("Erro ao enviar estado do jogo para o cliente " + jogador.getNome() + ": " + e.getMessage());
        }
    }

    private void encerrarServidor() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Servidor encerrado.");
            }
        } catch (IOException e) {
            System.err.println("Erro ao encerrar o servidor: " + e.getMessage());
        } finally {
            // Encerrar as conexões com os clientes
            for (Socket cliente : clientes) {
                try {
                    if (cliente != null && !cliente.isClosed()) {
                        cliente.close();
                        System.out.println("Conexão com o cliente " + cliente.getInetAddress().getHostAddress() + " encerrada.");
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao encerrar a conexão com o cliente " + cliente.getInetAddress().getHostAddress() + ": " + e.getMessage());
                }
            }

            // Interromper as threads do executor
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        int porta = 12345; // Porta do servidor
        int numJogadores = 3; // Número de jogadores
        Servidor servidor = new Servidor(porta, numJogadores);
        servidor.iniciarServidor();
    }
}
