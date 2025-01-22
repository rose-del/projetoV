import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    private Socket socket;
    private String nomeJogador;

    public Cliente(String nomeJogador) {
        this.nomeJogador = nomeJogador;
    }

    public void conectarAoServidor(String enderecoServidor, int porta) {
        try {
            socket = new Socket(enderecoServidor, porta);
            System.out.println("Conectado ao servidor: " + enderecoServidor + ":" + porta);

            // ... (remover a criação das streams out e in) ...

            // ... (remover o envio do nome do jogador) ...

            // ... (remover a thread para receber mensagens) ...

            // ... (remover a leitura das ações do jogador) ...

        } catch (IOException e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite seu nome: ");
        String nomeJogador = scanner.nextLine(); // Lê o nome do jogador

        Cliente cliente = new Cliente(nomeJogador); // Cria a instância com o nome
        cliente.conectarAoServidor("localhost", 12345); // Conecta ao servidor
    }
}