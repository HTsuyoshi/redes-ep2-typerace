package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ClientMain {

    private WebSocketClient client;
    private static String server = "",
            clientId = "";

    public ClientMain(WebSocketClient client) {
        this.client = client;
    }

    public ClientMain(WebSocketClient client, String server, String clientId) {
        this.client = client;
        this.server = server;
        this.clientId = clientId;
    }

    public void init() {
        System.out.println("Iniciando cliente: " + clientId);
        client.addHeader("Id do cliente", clientId);
        client.connect();
    }

    public static void main(String[] args) throws IOException {

        Scanner entrada = new Scanner(System.in);
        Logger console;
        Choice choice = Choice.NONE;

        console = createLogger(entrada);

        while (choice != Choice.QUIT_GAME) {
            printMenu(console);
            choice = getUserChoice(entrada);

            switch (choice) {
                case ENTER_GAME:
                    if (server.isBlank() || clientId.isBlank()) {
                        console.printErr("Servidor ou ID não definido");
                        break;
                    }

                    try {
                        WebSocketClient client = new Client(new URI(server));
                        ClientMain main = new ClientMain(client);
                        main.init();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    break;

                case SET_SERVER:
                    server = getUserInput(console, entrada, "Defina o seu servidor (Padrão: ws://localhost:8080):");
                    break;

                case SET_USER:
                    clientId = getUserInput(console, entrada, "Defina seu ID (Padrão: clientId):");
                    break;

                case COLORED_OUTPUT:
                    console.setSupportsANSI(!console.getSupportsANSI());
                    break;

                case QUIT_GAME:
                    break;

            }
            System.out.print("\033[H\033[2J");

        }

    }

    public static Logger createLogger(Scanner entrada) {
        Logger console = new Logger(true);
        console.printErr("\n\tFRASE EM VERMELHO");
        System.out.println("A frase acima está na cor vermelha? (S/N)");

        String userInput = entrada.nextLine().toLowerCase();
        if (userInput.charAt(0) != 's') console.setSupportsANSI(false);

        console.clear();
        return console;
    }

    public static void printMenu(Logger console) {
        String gameMenu = "==== Bem Vindo ao TYPE RACE ====\n" +
                "1 - Começar o Jogo\n" +
                "2 - Definir nome do servidor\n" +
                "3 - Definir nome do usuário\n" +
                "4 - Colocar/Remover cor do terminal\n" +
                "0 - Sair do jogo\n";

        System.out.println(gameMenu);

        if (server.isBlank()) console.printErr("Servidor não definido");
        else console.printGood("Server: " + server);

        if (clientId.isBlank()) console.printErr("ID não definido");
        else console.printGood("Seu ID: " + clientId);

        console.emptyLine();

        System.out.print("Sua escolha: ");
    }

    public static String getUserInput(Logger console, Scanner entrada, String text) {
        String userInput = "";
        while (userInput.isBlank()) {
            System.out.println(text);
            userInput = entrada.nextLine();
            console.clear();
        }
        return userInput;
    }

    public static Choice getUserChoice(Scanner entrada) {
        int choice = entrada.nextInt();
        switch (choice) {
            case 0:
                return Choice.QUIT_GAME;
            case 1:
                return Choice.ENTER_GAME;
            case 2:
                return Choice.SET_SERVER;
            case 3:
                return Choice.SET_USER;
            case 4:
                return Choice.COLORED_OUTPUT;
            default:
                return Choice.NONE;
        }
    }

}

enum Choice {
    NONE,
    ENTER_GAME,
    SET_USER,
    SET_SERVER,
    COLORED_OUTPUT,
    QUIT_GAME
}
