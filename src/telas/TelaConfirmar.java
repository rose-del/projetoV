package telas;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import telas.TelaEscolha;


public class TelaConfirmar extends Application {
    private static final String SERVIDOR_IP = "127.0.0.1";
    private static final int PORTA = 12345;
    private BufferedReader entrada;
    private PrintWriter saida;
    private Socket socket;
    private VBox root;
    private Label jogadorLabel;
    private Label perguntaLabel;
    private HBox botoesPergunta;

    @Override
    public void start(Stage primaryStage) {
        conectarAoServidor();

        jogadorLabel = new Label("Aguardando informações do servidor...");
        jogadorLabel.getStyleClass().add("jogador-label");

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

        root = new VBox(20, jogadorLabel, perguntaLabel, botoesPergunta);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(10));

        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("tela-iniciaJogo");

        Scene scene = new Scene(root, 800, 600);
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            root.setPrefWidth(newWidth.doubleValue());
        });
        scene.heightProperty().addListener((obs, oldHeigth, newHeight) -> {
            root.setPrefHeight(newHeight.doubleValue());
        });
        scene.getStylesheets().add(getClass().getResource("../resources/styles.css").toExternalForm());

        primaryStage.setTitle("Jogo de Cartas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void conectarAoServidor() {
        new Thread(() -> {
            try {
                socket = new Socket(SERVIDOR_IP, PORTA);
                entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                saida = new PrintWriter(socket.getOutputStream(), true);
                System.out.println("Conectado ao servidor!");

                String mensagem;
                while ((mensagem = entrada.readLine()) != null) {
                    String finalMensagem = mensagem;
                    Platform.runLater(() -> processarMensagemServidor(finalMensagem));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Para garantir que qualquer atualização na interface seja segura,
     * usamos ({@link Platform.runLater()}), que coloca a execução do código na fila
     * da Thread da UI.
     * <br/>
     * <br/>
     * - Essa parte é essencial em aplicações JavaFX porque atualizações da
     * interface gráfica devem ser feitas na JavaFX Application Thread.
     * <br/>
     * - Como a mensagem do servidor pode estar sendo processada em uma
     * thread separada, Platform.runLater() garante que as mudanças na UI
     * (como setText(), setVisible()) sejam feitas na thread correta.
     * */
    private void processarMensagemServidor(String mensagem) {
        Platform.runLater(() -> {
            if (mensagem.contains("Mínimo de jogadores atingido")) {
                jogadorLabel.setText(mensagem);
            }else if (mensagem.startsWith("Iniciar jogo?")) {
                perguntaLabel.setText("Iniciar jogo?");
                perguntaLabel.setVisible(true);
                botoesPergunta.setVisible(true);
            }else if (mensagem.contains("Tempo esgotado")) {
                jogadorLabel.setText(mensagem);
            }else if (mensagem.contains("Jogadores insuficientes")) {
                jogadorLabel.setText(mensagem);
            }else if (mensagem.contains("Um jogador recusou")) {
                jogadorLabel.setText(mensagem);
            }else if (mensagem.contains("Todos aceitaram")) {
                jogadorLabel.setText(mensagem);
            }
        });
    }

    private void enviarResposta(String resposta) {
        saida.println(resposta);
        perguntaLabel.setVisible(false);
        botoesPergunta.setVisible(false);

        /**if ("S".equals(resposta)) {
            // Criando e iniciando a nova tela
            Platform.runLater(() -> {
                TelaEscolha telaEscolha = new TelaEscolha();
                Stage stage = (Stage) root.getScene().getWindow(); // Obtendo a janela atual
                try{
                    telaEscolha.start(stage); // Inicia a TelaEscolha na mesma janela
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }**/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
