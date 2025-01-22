package cliente;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * Classe responsável pela conexão com o servidor de chat.
 * <br/>
 * - Estabelece a conexão, envia e recebe mensagens, e gerencia o estado da conexão.
 * **/
public class ClienteConexao {
    private Socket socket; // Representa a conexão com o servidor.
    private OutputStream ou; // Fluxo de saída para enviar dados ao servidor
    private Writer ouw; // Encapsula o OutputStream
    private BufferedWriter bfw; // Facilita a escrita eficiente de mensagens ao servidor

    /**
     * Conecta ao servidor de chat com o IP e porta especificados.
     * @param ip Endereço IP do servidor.
     * @param porta Porta utilizada para a conexão.
     * @param userName Nome do usuário a ser enviado ao servidor.
     **/
    public void conectar(String ip, int porta, String userName) {
        try {
            socket = new Socket(ip, porta); // Cria um novo socket com o endereço IP e a porta fornecidos.

            // Obtém os streams de saída para o socket.
            ou = socket.getOutputStream();
            ouw = new OutputStreamWriter(ou);
            bfw = new BufferedWriter(ouw);

            // Envia o nome do usuário para o servidor.
            bfw.write(userName + "\r\n");
            bfw.flush();
        } catch (IOException e) {
            // Exibe uma mensagem de erro caso não seja possível conectar.
            JOptionPane.showMessageDialog(null, "Erro ao conectar: " + e.getMessage());
        }
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * Envia uma mensagem para o servidor.
     *
     * @param mensagem Mensagem a ser enviada.
     */
    public void enviarMensagem(String mensagem) {
        try {
            bfw.write(mensagem + "\r\n"); // Escreve a mensagem no buffer de saída.
            bfw.flush();
        } catch (IOException e) {
            // Exibe uma mensagem de erro caso não seja possível enviar a mensagem.
            JOptionPane.showMessageDialog(null, "Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    public void ouvirMensagem(ClienteGUI gui) {
        new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String mensagemRecebida;
                while ((mensagemRecebida = br.readLine()) != null) {
                    if (mensagemRecebida.startsWith("JOGO:")) {
                        String resultado = mensagemRecebida.substring(5); // Remove o prefixo "JOGO:"
                        JOptionPane.showMessageDialog(null, "Resultado do jogo: " + resultado);
                    } else {
                        gui.adicionarMensagem(mensagemRecebida, false);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Conexão encerrada: " + e.getMessage());
            }
        }).start();
    }


    public void desconectar() {
        try {
            if (bfw != null) {
                bfw.close(); // Fecha os buffers e o socket.
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println("Erro ao desconectar: " + ex.getMessage()); // Exibe uma mensagem de erro caso não seja possível desconectar.
        }
    }
}