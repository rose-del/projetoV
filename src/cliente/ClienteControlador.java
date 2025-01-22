package cliente;

import cartas.Carta;
import cartas.Jogador;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Classe responsável por controlar a interação entre a interface gráfica
 * ({@link ClienteGUI}) e a conexão com o servidor ({@link ClienteConexao}).
 * <p>
 * Gerencia o envio de mensagens, a exibição de mensagens recebidas
 * e a inicialização da aplicação.
 */
public class ClienteControlador {
    private ClienteGUI gui;
    private ClienteConexao conexao;

    private static ArrayList<Jogador> jogadores = new ArrayList<>();
    private static int jogadorAtual = 0;

    public ClienteControlador(ClienteGUI gui, ClienteConexao conexao) {
        this.gui = gui;
        this.conexao = conexao;

        this.gui.adicionarActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        });
    }

    /**
     * Envia a mensagem digitada pelo usuário para o servidor.
     * <p>
     * Obtém a mensagem da interface gráfica, envia para o servidor
     * através da conexão, adiciona a mensagem na interface gráfica
     * e limpa o campo de texto.
     */
    private void enviarMensagem() {
        String mensagem = gui.getMensagem();
        if (!mensagem.isEmpty()) {
            conexao.enviarMensagem(mensagem);
            gui.adicionarMensagem(mensagem, true);
            gui.limparMensagem();
        }
    }

    /**
     * Inicia a aplicação.
     * <p>
     * Mostra a interface gráfica, solicita o nome do usuário,
     * conecta ao servidor e inicia a escuta de mensagens.
     */
    public void iniciar() {
        String userName = gui.solicitarNomeUsuario();

        // Exibe uma caixa de diálogo com o IP e a porta do servidor.
        JLabel lblMensagem = new JLabel("Verificar!");
        JTextField txtIP = new JTextField("127.0.0.1");
        JTextField txtPorta = new JTextField("12345");
        Object[] texts = {lblMensagem, txtIP, txtPorta};
        JOptionPane.showMessageDialog(null, texts);

        conexao.conectar(txtIP.getText(), Integer.parseInt(txtPorta.getText()), userName);
        gui.mostrar();
        iniciarJogoPPT();
        conexao.ouvirMensagem(gui);
    }

    public void iniciarJogoPPT() {
        gui.mostrarMenuJogo((escolha) -> {
            // Envia a escolha do jogador para o servidor
            conexao.enviarMensagem("JOGO:" + escolha);
        });
    }


    /**private void notificarJogadorDaVez() {
        Jogador jogador = null;
        String texto = "Sua vez, " + jogador.getNome();
        jogador = jogadores.get(jogadorAtual);
        enviarMensagem();
    }**/

    /**private Carta gerarCartaAleatoria() {
        // Lógica para criar uma carta aleatória
        String[] tipos = {"Medicamento", "Doença", "Órgão"};
        String[] cores = {"Vermelho", "Azul", "Verde"};
        return new Carta(
                tipos[(int) (Math.random() * tipos.length)],
                cores[(int) (Math.random() * cores.length)]
        );
    }**/
}
