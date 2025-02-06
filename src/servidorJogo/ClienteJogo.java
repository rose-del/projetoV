package servidorJogo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteJogo extends Application {
    private static final String SERVIDOR_IP = "127.0.0.1";
    private static final int PORTA = 12345;
    private BufferedReader entrada;
    private PrintWriter saida;
    private Socket socket;
    private List<String> maoCartas = new ArrayList<>();
    private VBox root;
    private GridPane exibicaoCartas;
    private Label jogadorLabel;
    private Label perguntaLabel;
    private HBox botoesPergunta;


    @Override
    public void start(Stage primaryStage) {
        conectarAoServidor();

        Button btnAtacar = new Button("Atacar");
        Button btnDescartar = new Button("Descartar");
        Button btnMontar = new Button("Montar Corpo");

        // Adiciona uma classe CSS para estilização.
        btnAtacar.getStyleClass().add("botao");
        btnDescartar.getStyleClass().add("botao");
        btnMontar.getStyleClass().add("botao");

        HBox botoes = new HBox(20, btnAtacar, btnDescartar, btnMontar);
        botoes.setAlignment(Pos.TOP_CENTER);
        botoes.setPadding(new Insets(20, 10, 20, 10));


        //btnAtacar.setOnAction(e -> abrirMenuAtaque());

        exibicaoCartas = new GridPane();
        exibicaoCartas.setPadding(new Insets(10));
        exibicaoCartas.setHgap(10);
        exibicaoCartas.setVgap(10);
        exibicaoCartas.setAlignment(Pos.CENTER);

        jogadorLabel = new Label("Aguardando informações do servidor...");
        perguntaLabel = new Label("");
        perguntaLabel.setVisible(false);

        Button btnSim = new Button("Sim");
        Button btnNao = new Button("Não");
        btnSim.setOnAction(e -> enviarResposta("S"));
        btnNao.setOnAction(e -> enviarResposta("N"));

        botoesPergunta = new HBox(10, btnSim, btnNao);
        botoesPergunta.setAlignment(Pos.CENTER);
        botoesPergunta.setVisible(false);

        root = new VBox(20, botoes, jogadorLabel, exibicaoCartas, perguntaLabel, botoesPergunta);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(10));

        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("tela-principal");

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

    private void processarMensagemServidor(String mensagem) {
        System.out.println("Servidor: " + mensagem);

        if (mensagem.startsWith("Suas cartas iniciais:")) {
            maoCartas.clear();
            String cartas = mensagem.replace("Suas cartas iniciais:", "").trim();
            maoCartas.addAll(Arrays.asList(cartas.split(",")));
            atualizarInterfaceCartas();
        } else if (mensagem.startsWith("Iniciar jogo?")) {
            perguntaLabel.setText("Iniciar jogo? (S/N)");
            perguntaLabel.setVisible(true);
            botoesPergunta.setVisible(true);
        }
    }

    private void atualizarInterfaceCartas() {
        exibicaoCartas.getChildren().clear();
        int coluna = 0;
        for (String carta : maoCartas) {
            Label cartaLabel = new Label(carta.trim());
            cartaLabel.setStyle("-fx-border-color: black; -fx-padding: 5px;");
            exibicaoCartas.add(cartaLabel, coluna++, 0);
        }
    }

    private void enviarResposta(String resposta) {
        saida.println(resposta);
        perguntaLabel.setVisible(false);
        botoesPergunta.setVisible(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}