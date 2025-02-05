import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;

import java.awt.*;

import static javafx.application.Application.launch;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
      // Criando o cabeçalho
     HBox header = new HBox();
     header.getStyleClass().add("header");

     /* • Fazendo o título "BEM-VINDO" no cabeçalho,
     *  • Fazendo um espaço flexível para centralizar o título
     * com o "Region spacer".,
     *  - Com o "header.getChildren()" adicionando elementos.
     */

     Label title = new Label("BEM-VINDOS");
     title.getStyleClass().add("title");
     Region spacer = new Region();
     HBox.setHgrow(spacer,Priority.ALWAYS);
     //header.getChildren().addAll(title,spacer);

     // Criando os Componentes
      Label ipLabel = new Label("IP:");
      Label portLabel = new Label("Porta:");
      TextField ipField = new TextField();
      TextField portField = new TextField();
      Button nextButton = new Button("PRÓXIMO");
      Button rulesButton = new Button("REGRAS");
      Button tutorialButton = new Button("TUTORIAL RÁPIDO");

      // Caixa de login (cinza)
      VBox loginBox = new VBox(10);
      loginBox.getChildren().addAll(
         new Label("ENTRE NA SALA"), ipLabel, ipField, portLabel, portField, nextButton
      );
      loginBox.getStyleClass().add("login-box");

       // Botões laterais
      VBox sideMenu = new VBox(10, rulesButton, tutorialButton);
      sideMenu.getStyleClass().add("side-menu");

      //Adiciona elementos no cabeçalho
       header.getChildren().addAll(title);

       // Layout principal
       BorderPane root = new BorderPane();
       root.setTop(title);
       root.setLeft(sideMenu);
       root.setCenter(loginBox);

       // Criando a cena e aplicando o CSS
       Scene scene = new Scene(root, 700, 500);
       scene.getStylesheets().add(getClass().getResource("./resources/style.css").toExternalForm());

       // Configuração da Janela
        primaryStage.setTitle("JOGO VIRUS");
        primaryStage.setScene(scene);
        primaryStage.show();
  }

  public static void main(String[] args) {
  launch(args);
  }
}





