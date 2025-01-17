package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

import br.usp.each.typerace.client.Logger.*;

/**
 * ClientMenu interface para o usuario se conectar com o server
 *
 * console logger para uma UI bonita :D
 * client websocket usada para fazer a conexao
 */

public class ClientMain {

    public static Logger console;

    private static WebSocketClient client;

    public ClientMain(WebSocketClient client) {
        ClientMain.client = client;
    }

    /**
     * init comeca uma conexao com o servidor
     *
     * @param clientId ID escolhido pelo usuario
     */

    public void init(String clientId) {
        System.out.println("Iniciando cliente: " + clientId);
        client.connect();
    }

    public void sendMessage(String message) {
        client.send(message);
    }

    public boolean isOpen() {
        return !client.isClosed();
    }

    /**
     * main serve para executar o menu do jogo permitindo que
     * o jogador consiga escolher seu servidor,
     * colocar seu nome, conectar com o server e habilitar ou
     * desabilitar cor no terminal.
     */

    public static void main(String[] args) throws URISyntaxException {
        console = new Logger(true);
        Scanner scan = new Scanner(System.in);
        Choice choice = Choice.NONE;

        String clientId = "",
                server = "";

        setLogger(scan);
        while (true) {
            printMenu(choice, server, clientId);
            choice = getUserChoice(scan);

            switch (choice) {
                case ENTER_GAME:
                    if (server.isBlank() || clientId.isBlank()) {
                        break;
                    }
                    console.clear();
                    startGame(scan, server, clientId);
                    break;

                case SET_SERVER:
                    server = getUserInput(scan, "Defina o seu servidor ", "ws://localhost:8080");
                    break;

                case SET_USER:
                    clientId = getUserInput(scan, "Defina seu ID (Limite de 18 caracteres)", "clientId");
                    break;

                case COLORED_OUTPUT:
                    console.setSupportsANSI(!console.getSupportsANSI());
                    break;

                case QUIT_GAME:
                    scan.close();
                    return;

                default:
                    break;
            }

            console.clear();
        }

    }

    /**
     * setLogger serve para o usuario verificar
     * se o terminal tem suporte para caracteres
     * de escape ANSI
     */

    public static void setLogger(Scanner scan) {
        console.appendEffect("\n\tFRASE EM VERMELHO\n", Color.RED, Mode.UNDERLINE);
        console.append("A frase acima está na cor vermelha? (S/N)");
        console.println();

        String userInput = scan.nextLine().toLowerCase();
        if (userInput.charAt(0) != 's') console.setSupportsANSI(false);

        console.clear();
    }

    /**
     * printMenu a interface principal do usuario
     *
     * @param server server que o usuario vai se conectar
     * @param clientId Id que o usuario vai utilizar
     * @param userChoice mostrar erro caso escolha seja invalida
     */

    public static void printMenu(Choice userChoice, String server, String clientId) {
        console.appendEffect("==== Bem Vindo ao TYPE RACE ====\n", Color.NONE, Mode.BOLD);
        console.append("1 - Começar o Jogo\n");
        console.append("2 - Definir nome do servidor\n");
        console.append("3 - Definir nome do usuário\n");
        console.append("4 - Colocar/Remover cor do terminal\n");
        console.append("5 - Sair do jogo\n\n");

        if (server.isBlank()) {
            console.appendEffect("Servidor: ", Color.RED, Mode.NONE);
            console.append(" não definido\n");
        } else {
            console.appendEffect("Servidor: ", Color.GREEN, Mode.NONE);
            console.append(server);
            console.append("\n");
        }

        if (clientId.isBlank()) {
            console.appendEffect("Seu ID: ", Color.RED, Mode.NONE);
            console.append(" não definido\n");
        } else {
            console.appendEffect("Seu ID: ", Color.GREEN, Mode.NONE);
            console.append(clientId);
            console.append("\n");
        }

        if (userChoice.equals(Choice.ERROR)) {
            console.appendEffect("\nSua escolha não é válida.", Color.RED, Mode.UNDERLINE);
            console.print();
        }

        console.append("\nSua escolha: ");
        console.print();
    }

    /**
     * getUserChoice recebe o escolha no menu
     * principal
     */

    public static Choice getUserChoice(Scanner scan) {

        int userInput;
        try {
            userInput = scan.nextInt();
            scan.nextLine();
        } catch (InputMismatchException exception) {
            userInput = -1;
            scan.next();
        }

        Choice choice = Choice.valueOf(userInput);
        return (choice == null) ? Choice.ERROR : choice;
    }

    /**
     * startGame cria uma conexao com o server.
     *
     * Enquanto a conxexao estiver aberta a funcao vai
     * receber a entrada do usuario e enviar para o server.
     *
     * @param server server usado para conexao
     * @param clientId Id usado na conexao
     */

    public static void startGame(Scanner scan, String server, String clientId) throws URISyntaxException {
        console.clear();
        client = new Client(new URI(String.format("%s/%s", server, clientId)), console);
        ClientMain main = new ClientMain(client);
        main.init(clientId);

        String userInput;
        while (main.isOpen()) {
            userInput = scan.nextLine();
            if (main.isOpen()) main.sendMessage(userInput);
        }

        console.clear();
    }

    /**
     * getUserInput recebe o nome do server ou nome
     * do usuario. E caso o usuario nao escreva nada
     * o valor padrao e utilizado
     *
     * @param defaultString servidor/nome padrao
     * @param text servidor/nome que o usuario escolheu
     */

    public static String getUserInput(Scanner scan, String text, String defaultString) {
        console.clear();
        console.append(text);
        console.appendEffect(String.format("(Padrão: %s)", defaultString), Color.GREEN, Mode.NONE);
        console.println();
        String userInput = scan.nextLine();
        return userInput.isBlank() ? defaultString : userInput;
    }

    /**
     * Mapeia as escolhas do usuario
     * para facilitar a leitura do codigo
     */

    enum Choice {
        ERROR(-1),
        NONE(0),
        ENTER_GAME(1),
        SET_SERVER(2),
        SET_USER(3),
        COLORED_OUTPUT(4),
        QUIT_GAME(5);

        private final int value;
        private static final HashMap<Object, Object> map = new HashMap<>();

        Choice(int value) {
            this.value = value;
        }

        static {
            for (Choice choice : Choice.values()) {
                map.put(choice.value, choice);
            }
        }

        public static Choice valueOf(int choice) {
            return (Choice) map.get(choice);
        }
    }
}

