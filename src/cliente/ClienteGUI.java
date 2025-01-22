package cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Classe responsável pela interface gráfica do cliente de chat.
 * <p>
 * Contém os componentes visuais da aplicação, como a janela principal,
 * o painel de mensagens, o campo de texto para digitar mensagens
 * e o botão de enviar.
 */
public class ClienteGUI {
    private JFrame frame;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField mensagemField;
    private JButton sendButton;
    private String userName = "Usuário";

    public ClienteGUI() {
        frame = new JFrame("CHATsZAp");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
        frame.setLayout(new BorderLayout());

        // Cria o painel de mensagens.
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 245, 245));

        // Adiciona o painel de mensagens a um scroll pane.
        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Cria o painel inferior com o campo de texto e o botão de enviar.
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mensagemField = new JTextField();
        sendButton = new JButton("Enviar");

        // Adiciona o campo de texto e o botão de enviar ao painel inferior.
        bottomPanel.add(mensagemField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // Adiciona o painel de mensagens e o painel inferior à janela principal.
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    public void mostrarMenuJogo(ActionListener listenerEscolha) {
        JDialog dialog = new JDialog(frame, "Vírus", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(4, 1));

        JLabel label = new JLabel("Escolha sua jogada:");
        dialog.add(label);

        JButton AtacarDefenderButton = new JButton("Atacar/Defender");
        JButton DescartarButton = new JButton("Descartar");
        JButton MontarButton = new JButton("Montar Corpo");

        dialog.add(AtacarDefenderButton);
        dialog.add(DescartarButton);
        dialog.add(MontarButton);

        // Adiciona listeners aos botões
        AtacarDefenderButton.addActionListener(e -> {
            listenerEscolha.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "AtacarDefenderButton"));
            dialog.dispose();
        });
        DescartarButton.addActionListener(e -> {
            listenerEscolha.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "DescartarButton"));
            dialog.dispose();
        });
        MontarButton.addActionListener(e -> {
            listenerEscolha.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "MontarButton"));
            dialog.dispose();
        });

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }


    public String solicitarNomeUsuario() {
        this.userName = JOptionPane.showInputDialog(frame, "Nome: ", "userName",
                JOptionPane.PLAIN_MESSAGE);

        if (userName == null || userName.trim().isEmpty()) {
            userName = "Usuário";
        }
        return userName;
    }

    /**
     * Adiciona uma mensagem ao painel de mensagens.
     *
     * @param mensagem Mensagem a ser exibida.

     * @param isSelf Indica se a mensagem foi enviada pelo próprio cliente (true) ou recebida
    de outro cliente (false).
     */
    public void adicionarMensagem(String mensagem, boolean isSelf) {
        JPanel mensagemPanel = new JPanel();
        mensagemPanel.setLayout(new BorderLayout());
        mensagemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel mensagemLabel = new JLabel("<html><p style=\"width: 200px;\">" + mensagem + "</p></html>"); // Cria um rótulo para a mensagem.
        mensagemLabel.setOpaque(true);
        mensagemLabel.setBackground(isSelf ? new Color(220, 248, 198) : Color.WHITE);
        mensagemLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mensagemPanel.add(mensagemLabel, isSelf ? BorderLayout.EAST : BorderLayout.WEST);
        chatPanel.add(mensagemPanel);

        // Atualiza o layout e repinta o painel de mensagens para garantir que a nova mensagem seja exibida.
        chatPanel.revalidate();
        chatPanel.repaint();

        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    /**
     * Obtém a mensagem digitada no campo de texto.
     *
     * @return A mensagem digitada no campo de texto, com espaços e caracteres trimmados.
     */
    public String getMensagem() {
        return mensagemField.getText().trim();
    }

    public void limparMensagem() {
        mensagemField.setText("");
    }

    /**
     * Adiciona um listener ao botão de enviar.
     *
     * @param listener O listener a ser adicionado.
     */
    public void adicionarActionListener(ActionListener listener) {
        sendButton.addActionListener(listener);
    }
}
