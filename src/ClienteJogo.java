import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClienteJogo extends Application {
    private static final String SERVIDOR_IP = "127.0.0.1";
    private static final int PORTA = 12345;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter saida;
    private VBox root;
    private HBox botoes;
    private Label jogadorDaVez;
    private Map<String, List<String>> camposJogadores = new HashMap<>();
    private List<String> cartasMao = new ArrayList<>();
    private Label perguntaLabel;
    private HBox botoesPergunta;
    private String nomeJogadorAtual = "";

    @Override
    public void start(Stage primaryStage) {
        conectarServidor();

        jogadorDaVez = new Label("Aguardando confirmação dos jogadores...");
        jogadorDaVez.getStyleClass().add("jogador-da-vez");

        perguntaLabel = new Label("");
        perguntaLabel.setVisible(false);
        perguntaLabel.getStyleClass().add("pergunta-label");

        Button btnSim = new Button("Sim");
        btnSim.getStyleClass().add("botao");
        Button btnNao = new Button("Não");
        btnNao.getStyleClass().add("botao");
        btnSim.setOnAction(e -> enviarResposta("S"));
        btnNao.setOnAction(e -> enviarResposta("N"));

        botoesPergunta = new HBox(10, btnSim, btnNao);
        botoesPergunta.setAlignment(Pos.CENTER);
        botoesPergunta.setVisible(false);

        botoes = new HBox(20, new Button("Atacar"), new Button("Descartar"), new Button("Montar Corpo"));
        botoes.setAlignment(Pos.TOP_CENTER);
        botoes.setPadding(new Insets(20, 10, 20, 10));
        botoes.setVisible(false);

        root = new VBox(20, jogadorDaVez, perguntaLabel, botoesPergunta, botoes);
        root.setAlignment(Pos.TOP_CENTER);
        atualizarInterfaceJogadores();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Jogo de Cartas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void conectarServidor() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVIDOR_IP, PORTA);
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                saida = new PrintWriter(socket.getOutputStream(), true);

                String mensagem;
                while ((mensagem = entrada.readLine()) != null) {
                    System.out.println("[CLIENTE] Mensagem recebida: " + mensagem);
                    String finalMensagem = mensagem;
                    Platform.runLater(() -> processarMensagem(finalMensagem));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void processarMensagem(String mensagem) {
        if (mensagem.startsWith("Iniciar jogo?")) {
            perguntaLabel.setText("Iniciar jogo?");
            perguntaLabel.setVisible(true);
            botoesPergunta.setVisible(true);
        } else if (mensagem.startsWith("Suas cartas:")) {
            System.out.println("[CLIENTE] Processando cartas da mão...");
            String cartasTexto = mensagem.replace("Suas cartas:", "").trim();
            if (!cartasTexto.isEmpty() && !cartasTexto.equals("Nenhuma carta recebida")) {
                cartasMao = new ArrayList<>(Arrays.asList(cartasTexto.split(", ")));
                System.out.println("[CLIENTE] Cartas processadas: " + cartasMao);
            } else {
                cartasMao.clear();
                System.out.println("[CLIENTE] Nenhuma carta processada.");
            }
            botoes.setVisible(true);
            atualizarInterfaceJogadores();
        } else if (mensagem.startsWith("Vez do jogador:")) {
            nomeJogadorAtual = mensagem.replace("Vez do jogador:", "").trim();
            if (nomeJogadorAtual.equals("Você")) {
                jogadorDaVez.setText("É a sua vez de jogar.");
            } else {
                jogadorDaVez.setText("É a vez de " + nomeJogadorAtual + " jogar.");
            }
        }
    }

    private void enviarResposta(String resposta) {
        saida.println(resposta);
        perguntaLabel.setVisible(false);
        botoesPergunta.setVisible(false);
    }

    private void atualizarInterfaceJogadores() {
        if (root == null) return;
        root.getChildren().clear();
        root.getChildren().addAll(jogadorDaVez, perguntaLabel, botoesPergunta, botoes);

        System.out.println("[CLIENTE] Atualizando UI com cartas da mão: " + cartasMao);
        Label minhasCartasLabel = new Label("Suas Cartas: " + (cartasMao.isEmpty() ? "Aguardando cartas do servidor..." : String.join(", ", cartasMao)));
        root.getChildren().add(minhasCartasLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
