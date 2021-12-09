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
                        console.appendErr("Servidor ou ID não definido");
                        console.println();
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
                    server = getUserInput(console, entrada, "Defina o seu servidor ", "(Padrão: ws://localhost:8080):");
                    break;

                case SET_USER:
                    clientId = getUserInput(console, entrada, "Defina seu ID ", "(Padrão: clientId):");
                    break;

                case COLORED_OUTPUT:
                    console.setSupportsANSI(!console.getSupportsANSI());
                    break;

                case QUIT_GAME:
                    break;

            }

            console.clear();
        }

    }

    public static Logger createLogger(Scanner entrada) {
        Logger console = new Logger(true);
        console.appendErr("\n\tFRASE EM VERMELHO\n");
        console.append("A frase acima está na cor vermelha? (S/N)");
        console.println();

        String userInput = entrada.nextLine().toLowerCase();
        if (userInput.charAt(0) != 's') console.setSupportsANSI(false);

        console.clear();
        return console;
    }

    public static void printMenu(Logger console) {
        console.append("==== Bem Vindo ao TYPE RACE ====\n");
        console.append("1 - Começar o Jogo\n");
        console.append("2 - Definir nome do servidor\n");
        console.append("3 - Definir nome do usuário\n");
        console.append("4 - Colocar/Remover cor do terminal\n");
        console.append("0 - Sair do jogo\n");
        console.println();

        if (server.isBlank()) {
            console.appendErr("Servidor não definido\n");
        } else {
            console.appendGood("Server: ");
            console.append(server);
            console.append("\n");
        }

        if (clientId.isBlank()) {
            console.appendErr("ID não definido\n");
        } else {
            console.appendGood("Seu ID: ");
            console.append(clientId);
            console.append("\n");
        }
        console.append("\nSua escolha: ");
        console.print();
    }

    public static String getUserInput(Logger console, Scanner entrada, String text, String example) {
        String userInput = "";
        while (userInput.isBlank()) {
            console.append(text);
            console.appendGood(example);
            console.println();
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
