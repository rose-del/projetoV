package telas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import cartas.Jogador;
import cartas.Orgao;
import cartas.Virus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Esta classe representa a tela principal da aplicação, onde os jogadores interagem com o jogo.
 * <br/>
 * Implementa o metodo start(), que configura a interface gráfica, inicializa os jogadores e define as ações dos botões.
 * **/

public class TelaEscolha extends Application {
    private List<Jogador> jogadores;
    private Jogador jogadorAtual;
    private VBox root;

    @Override
    public void start(Stage primaryStage) {
        inicializarJogadores();

        Button btnAtacar = new Button("Atacar");
        Button btnDescartar = new Button("Descartar");
        Button btnMontar = new Button("Montar Corpo");

        // Adiciona uma classe CSS para estilização.
        btnAtacar.getStyleClass().add("botao");
        btnDescartar.getStyleClass().add("botao");
        btnMontar.getStyleClass().add("botao");

        HBox botoes = new HBox(20, btnAtacar, btnDescartar, btnMontar);
        botoes.setAlignment(Pos.TOP_CENTER); // Alinha os botões no topo e ao centro.
        botoes.setPadding(new Insets(20, 10, 20, 10)); // Adiciona um espaçamento no topo

        HBox.setHgrow(btnAtacar, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(btnDescartar, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(btnMontar, javafx.scene.layout.Priority.ALWAYS);

        btnAtacar.setMaxWidth(Double.MAX_VALUE);
        btnDescartar.setMaxWidth(Double.MAX_VALUE);
        btnMontar.setMaxWidth(Double.MAX_VALUE);

        // Layout principal
        root = new VBox(20, botoes);
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("tela-principal"); // Aplica uma classe CSS.

        // Cria e retorna um VBox que exibe os nomes dos jogadores e seus órgãos associados.
        GridPane exibicaoJogadores = exibirJogadores();
        //exibicaoJogadores.getStyleClass().add("jogador-label");
        root.getChildren().add(exibicaoJogadores);

        btnAtacar.setOnAction(e -> abrirMenuAtaque());

        // Configuração da cena principal do jogo com 800x600 px.
        Scene scene = new Scene(root, 800, 600);
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            root.setPrefWidth(newWidth.doubleValue());
        });
        scene.heightProperty().addListener((obs, oldHeigth, newHeight) -> {
            root.setPrefHeight(newHeight.doubleValue());
        });
        scene.getStylesheets().add(getClass().getResource("../resources/styles.css").toExternalForm());

        Label jogadorDaVez = new Label("É a sua vez de jogar " + jogadorAtual.getNome() + "\n       Escolha a sua ação.");
        jogadorDaVez.getStyleClass().add("jogador-da-vez");
        root.getChildren().add(jogadorDaVez);

        primaryStage.setTitle("Jogo de Cartas");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Este metodo inicializa uma lista de três jogadores e atribui órgãos a cada um.
     * Também define o primeiro jogador como o jogador atual.
     * **/
    private void inicializarJogadores() {
        jogadores = new ArrayList<>();
        jogadores.add(new Jogador("Jogador 1"));
        jogadores.add(new Jogador("Jogador 2"));
        jogadores.add(new Jogador("Jogador 3"));

        // Adicionar alguns órgãos e vírus para teste
        jogadores.get(0).adicionarOrgao(new Orgao("Coração", "Vermelho"));
        jogadores.get(0).adicionarOrgao(new Orgao("Cérebro", "Azul"));

        jogadores.get(1).adicionarOrgao(new Orgao("Osso", "Amarelo"));
        jogadores.get(1).adicionarOrgao(new Orgao("Cérebro", "Azul"));
        jogadores.get(1).adicionarOrgao(new Orgao("Coração", "Vermelho"));

        jogadores.get(2).adicionarOrgao(new Orgao("Cérebro", "Azul"));
        jogadores.get(2).adicionarOrgao(new Orgao("Estômago", "Verde"));


        jogadorAtual = jogadores.get(0);
    }

    private GridPane exibirJogadores() {
        GridPane vbox = new GridPane();
        vbox.setPadding(new Insets(10));
        vbox.setHgap(20);
        vbox.setVgap(20);
        vbox.setAlignment(Pos.CENTER);

        int coluna = 0;
        int linha = 0;
        int maxColunas = 2;

        for (Jogador jogador : jogadores) {
            VBox jogadorBox = new VBox(5);
            jogadorBox.setAlignment(Pos.CENTER);
            jogadorBox.setStyle("-fx-border-color: black; -fx-padding: 10px;");


            Label nomeJogador = new Label(jogador.getNome());
            nomeJogador.getStyleClass().add("jogador-label"); // Classe CSS para estilização

            //StringBuilder é para construir dinamicamente a string com as informações dos órgãos de cada jogador
            StringBuilder infoOrgaos = new StringBuilder();
            for (Orgao orgao : jogador.getOrgaos()) {
                infoOrgaos.append(orgao.getNome()) //O infoOrgaos é um objeto da classe StringBuilder
                        .append(" (")
                        .append(orgao.isInfectado() ? "Infectado" : "Saudável")
                        .append("), ");
            }
            Text textoJogador = new Text(infoOrgaos.toString());


            jogadorBox.getChildren().addAll(nomeJogador, textoJogador);
            vbox.add(jogadorBox, coluna, linha);

            // Ajusta a disposição para múltiplas colunas
            coluna++;
            if (coluna >= maxColunas) {
                coluna = 0;
                linha++;
            }
        }
        return vbox;
    }

    /**
     * Exibe uma janela de diálogo para o jogador escolher outro jogador para atacar
     * e, em seguida, outro diálogo para escolher um vírus para usar no ataque.
     * Após a escolha, o vírus é jogado, afetando o jogador alvo.
     * **/
    private void abrirMenuAtaque() {
        List<String> nomesJogadores = new ArrayList<>();

        //Cria uma lista com os nomes dos outros jogadores.
        for (Jogador jogador : jogadores) {
            if (!jogador.equals(jogadorAtual)) {
                nomesJogadores.add(jogador.getNome());
            }
        }

        //Exibe um menu de seleção para escolher quem atacar.
        ChoiceDialog<String> dialogJogador = new ChoiceDialog<>(nomesJogadores.get(0), nomesJogadores);
        dialogJogador.setTitle("Escolha um jogador");
        dialogJogador.setHeaderText("Selecione quem você quer atacar");
        dialogJogador.setContentText("Jogador:");

        Optional<String> escolhaJogador = dialogJogador.showAndWait();
        /**
         * Esse código busca um jogador na lista com o mesmo nome que o usuário escolheu no diálogo.
         * Se encontrar, retorna esse jogador; caso contrário, retorna null
         */
        if (escolhaJogador.isPresent()) {
            String nomeJogadorAlvo = escolhaJogador.get();
            //Cria um stream dos jogadores, permitindo que operações como filter() e findFirst() sejam aplicadas.
            Jogador jogadorAlvo = jogadores.stream()
                    .filter(j -> j.getNome().equals(nomeJogadorAlvo))
                    .findFirst()
                    .orElse(null);

            if (jogadorAlvo != null) {
                // Criar lista de vírus disponíveis com base nas cores dos órgãos
                List<String> virusDisponiveis = new ArrayList<>();
                for (Orgao orgao : jogadorAlvo.getOrgaos()) {
                    virusDisponiveis.add(orgao.getCor());
                }

                ChoiceDialog<String> dialogVirus = new ChoiceDialog<>(virusDisponiveis.get(0), virusDisponiveis);
                dialogVirus.setTitle("Escolha um vírus");
                dialogVirus.setHeaderText("Selecione o vírus para atacar");
                dialogVirus.setContentText("Vírus:");

                Optional<String> escolhaVirus = dialogVirus.showAndWait();
                if (escolhaVirus.isPresent()) {
                    //String nomeVirusSelecionado = escolhaVirus.get();
                    String corDoVirus = escolhaVirus.get();
                    //Ataca o jogador escolhido com um vírus.
                    Virus virus = new Virus("Vírus " + corDoVirus, corDoVirus);
                    virus.jogar(jogadorAtual, jogadorAlvo);
                    System.out.println("Ataque realizado com " + "Virus" + corDoVirus);

                    // Infecta o órgão do jogador atacado (exemplo: infectando o primeiro órgão)
                    for (Orgao orgao : jogadorAlvo.getOrgaos()) {
                        if (orgao.getCor().equals(corDoVirus)) {
                            if (!orgao.isInfectado()) {  // Se o órgão ainda não estiver infectado
                                orgao.infectar();
                                break; // Ataca o primeiro órgão não infectado
                            }
                        }
                    }
                    alternarTurno();
                    atualizarInterfaceJogadores();
                }
            }
        }
    }

    private void atualizarInterfaceJogadores() {
        // Atualiza a exibição dos jogadores e seus órgãos após a infecção
        GridPane exibicaoJogadores = exibirJogadores();
        root.getChildren().set(1, exibicaoJogadores);  // Atualiza a exibição dos jogadores na interface
    }

    private void alternarTurno() {
        int indexJogadorAtual = jogadores.indexOf(jogadorAtual);
        int proximoJogador = (indexJogadorAtual + 1) % jogadores.size(); //Alterna entre os jogadores
        jogadorAtual = jogadores.get(proximoJogador);

        // Chama a função para atualizar a interface com o jogador da vez
        atualizarJogadorDaVez();
    }

    private void atualizarJogadorDaVez() {
        Label jogadorDaVez = new Label("É a sua vez de jogar " + jogadorAtual.getNome() + "\n       Escolha a sua ação.");
        jogadorDaVez.getStyleClass().add("jogador-da-vez");

        if (root.getChildren().size() > 2) {
            root.getChildren().set(2, jogadorDaVez);// Atualiza o texto que mostra quem é o jogador da vez
        } else {
            root.getChildren().add(jogadorDaVez);// Adiciona o texto caso não exista ainda
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}