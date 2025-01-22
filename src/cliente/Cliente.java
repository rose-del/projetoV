package cliente;

public class Cliente {
    /**
     * Metodo principal da aplicação.
     * Cria as instâncias das classes {@link ClienteGUI},
     * {@link ClienteConexao} e {@link ClienteControlador},
     * e inicia a aplicação.
     **/
    public static void main(String[] args) {
        ClienteGUI gui = new ClienteGUI(); //Cria a interface gráfica do cliente.
        ClienteConexao conexao = new ClienteConexao(); //Cria a conexão com o servidor.
        ClienteControlador controlador = new ClienteControlador(gui, conexao); // Cria o controlador que gerencia a interação entre a interface e a conexão.
        controlador.iniciar();
    }
}