package telas;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
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

        // Botões
        Button btnAtacar = new Button("Atacar/Defender");
        Button btnDescartar = new Button("Descartar");
        Button btnMontar = new Button("Montar Corpo");

        btnAtacar.getStyleClass().add("botao");
        btnDescartar.getStyleClass().add("botao");
        btnMontar.getStyleClass().add("botao");

        // Layout dos botões
        HBox botoes = new HBox(20, btnAtacar, btnDescartar, btnMontar);
        botoes.setAlignment(Pos.TOP_CENTER); // Alinha os botões no topo e ao centro
        botoes.setPadding(new Insets(20, 0, 0, 0)); // Adiciona um espaçamento no topo

        // Cria e retorna um VBox que exibe os nomes dos jogadores e seus órgãos associados.
        VBox exibicaoJogadores = exibirJogadores();
        exibicaoJogadores.setPadding(new Insets(20)); // Adiciona um espaçamento interno

        // Layout principal
        root = new VBox(20, botoes, exibicaoJogadores);
        root.setAlignment(Pos.TOP_CENTER); // Alinha todo o conteúdo no topo e ao centro
        root.getStyleClass().add("tela-principal");

        // Ação do botão Atacar
        btnAtacar.setOnAction(e -> abrirMenuAtaque());

        // Configuração da cena
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("../resources/styles.css").toExternalForm());

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

        jogadorAtual = jogadores.get(0);
    }

    private VBox exibirJogadores() {
        VBox vbox = new VBox(40);
        for (Jogador jogador : jogadores) {
            StringBuilder infoOrgaos = new StringBuilder();
            for (Orgao orgao : jogador.getOrgaos()) {
                infoOrgaos.append(orgao.getNome())
                        .append(" (")
                        .append(orgao.isInfectado() ? "Infectado" : "Saudável")
                        .append("), ");
            }
            Text textoJogador = new Text(jogador.getNome() + ": " + infoOrgaos.toString());
            vbox.getChildren().add(textoJogador);
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
        for (Jogador jogador : jogadores) {
            if (!jogador.equals(jogadorAtual)) {
                nomesJogadores.add(jogador.getNome());
            }
        }

        ChoiceDialog<String> dialogJogador = new ChoiceDialog<>(nomesJogadores.get(0), nomesJogadores);
        dialogJogador.setTitle("Escolha um jogador");
        dialogJogador.setHeaderText("Selecione quem você quer atacar");
        dialogJogador.setContentText("Jogador:");

        Optional<String> escolhaJogador = dialogJogador.showAndWait();
        if (escolhaJogador.isPresent()) {
            String nomeJogadorAlvo = escolhaJogador.get();
            Jogador jogadorAlvo = jogadores.stream()
                    .filter(j -> j.getNome().equals(nomeJogadorAlvo))
                    .findFirst()
                    .orElse(null);

            if (jogadorAlvo != null) {
                // Criar lista de vírus disponíveis com base nas cores dos órgãos
                List<String> virusDisponiveis = new ArrayList<>();
                for (Orgao orgao : jogadorAlvo.getOrgaos()) {
                    virusDisponiveis.add("Vírus" + orgao.getCor());
                }

                ChoiceDialog<String> dialogVirus = new ChoiceDialog<>(virusDisponiveis.get(0), virusDisponiveis);
                dialogVirus.setTitle("Escolha um vírus");
                dialogVirus.setHeaderText("Selecione o vírus para atacar");
                dialogVirus.setContentText("Vírus:");

                Optional<String> escolhaVirus = dialogVirus.showAndWait();
                if (escolhaVirus.isPresent()) {
                    String nomeVirusSelecionado = escolhaVirus.get();
                    Virus virus = new Virus(nomeVirusSelecionado, "Vermelho"); // Cor do vírus pode ser ajustada
                    virus.jogar(jogadorAtual, jogadorAlvo);
                    System.out.println("Ataque realizado com " + nomeVirusSelecionado);

                    // Infecta o órgão do jogador atacado (exemplo: infectando o primeiro órgão)
                    for (Orgao orgao : jogadorAlvo.getOrgaos()) {
                        if (!orgao.isInfectado()) {  // Se o órgão ainda não estiver infectado
                            orgao.infectar();
                            break; // Ataca o primeiro órgão não infectado
                        }
                    }
                    atualizarInterfaceJogadores();
                }
            }
        }
    }

    private void atualizarInterfaceJogadores() {
        // Atualiza a exibição dos jogadores e seus órgãos após a infecção
        VBox exibicaoJogadores = exibirJogadores();
        root.getChildren().set(1, exibicaoJogadores);  // Atualiza a exibição dos jogadores na interface
    }

    public static void main(String[] args) {
        launch(args);
    }
}