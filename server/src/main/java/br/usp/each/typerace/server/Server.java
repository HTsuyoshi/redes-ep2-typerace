package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.Console;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Server extends WebSocketServer {

    private TypeRace typeRace;
    private final Map<String, WebSocket> connections;
    private final Console es;

    public Server(int port, Map<String, WebSocket> connections) {
        super(new InetSocketAddress(port));
        this.connections = connections;
        this.es = System.console();
    }

    @Override
    public void onStart() {
        this.typeRace = new TypeRace();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Nova conexão recebida...");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        conn.send(String.format("Um erro ocorreu: %s\n", ex.toString()));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if(!conn.isClosed()) {
            conn.close(code, reason);
        }
    }

    /**
     * Quando uma mensagem e recebida, caso seja uma nova
     * conexao, a funcao adiciona o jogador no hashmap
     * connections.
     *
     * Se o jogador ja estiver cadastrado o server recebe
     * a entrada do jogador como uma escolha do menu
     */

    @Override
    public void onMessage(WebSocket conn, String message) {

        if (isANewConnection(conn)) {
            if (verifyUser(conn, message)) {
                addUser(conn, message);
                System.out.println("Conexão feita com sucesso!");
            }
            return;
        }

        String user = this.getUserId(conn).get();

        if (this.typeRace.isRunning()) {
            if (!isInGame(user)) return;
            if (isPlaying(user)) this.typeRace.verifyAnswer(user, message);

            if (!typeRace.isRunning())  {
                broadcast(typeRace.scoreboard());

                return;
            }

            if (!isPlaying(user)) {
                conn.send("Parabéns, você terminou sua lista de palavras\n" +
                        "Por favor espere os outros jogadores terminarem");
            } else {
                conn.send(this.typeRace.nextWord(user));
            }

            return;
        }

        Choice userChoice = getChoice(message.charAt(0));
        switch (userChoice) {
            case HELP:
                this.help(conn);
                break;

            case START_GAME:
                broadcast(String.format("%s começou o jogo", user));
                typeRace.init(this.connections.keySet());
                broadcast(typeRace.getWord(user));
                break;

            case QUIT:
                System.out.println(message);
                removeUser(conn);
                break;

            default:
                conn.send("Esse comando não está na lista de comandos");
        }
    }

    public boolean isANewConnection(WebSocket conn) {
        return !connections.containsValue(conn);
    }

    /**
     * Verifica se ja existe algum usuario com o mesmo nome,
     * se existir retorna erro de violacao de politica e uma
     * mensagem
     */

    public boolean verifyUser(WebSocket conn, String clientId) {
        if (clientId.length() > 18) {
            conn.send("Este ID é muito grande! (Limite de 18 caracteres)\n" +
                    "Por gentileza escolha outro ID.\n");
            conn.close(1008, "ID grande demais"); // Policy Violation (long ID)
            return false;
        }

        if (connections.containsKey(clientId)) {
            conn.send("Este ID já está sendo utilizado!\n" +
                    "Por gentileza escolha outro ID.\n");
            conn.close(1008, "ID repitido"); // Policy Violation (2 users with the same username)
            return false;
        }

        return true;
    }

    /**
     * Adiciona o jogador e envia o menu do jogo
     */

    public void addUser(WebSocket conn, String clientId) {
        connections.put(clientId, conn);
        if (typeRace.isRunning()) {
            conn.send("Uma partida está em execução no momento, por favor espere a partida acabar.");
        } else {
            broadcast(String.format("%s acabou de entrar!\n" +
                    "Atualmente existem %d jogadores conectados.\n", clientId, connections.size()));
        }

        help(conn);
    }

    public void help(WebSocket conn) {
        conn.send("Bem vindo ao servidor do TypeRace!\n" +
                "Lista de commandos disponíveis:\n\n" +
                "\tH - Para ver essa mensagem de texto\n" +
                "\tC - Para começar o jogo\n" +
                "\tS - Para sair do jogo\n");
    }

    public boolean isInGame(String user) {
        return (this.typeRace.getPlayer(user) != null);
    }

    public boolean isPlaying(String user) {
        return this.typeRace.getPlayer(user).isPlaying();
    }

    public void removeUser(WebSocket conn) {
        String userId = getUserId(conn).get();
        conn.send(String.format("Obrigado por jogar! %s", userId.toString()));
        connections.remove(userId, conn);
        conn.close(1000, "O usuário quis sair do jogo");
    }

    public Optional<String> getUserId(WebSocket conn) {
        return connections.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(conn))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public Choice getChoice(char message) {
        Choice choice = Choice.valueOf(Character.toLowerCase(message));
        return (choice == null) ? Choice.NONE : choice;
    }

    /**
     * Mapeia as escolhas do usuario
     * para facilitar a leitura do codigo
     */

    enum Choice {
        START_GAME('c'),
        HELP('h'),
        QUIT('s'),
        NONE(' ');

        private final char value;
        private static Map map = new HashMap<>();

        Choice(char value) {
            this.value = value;
        }

        static {
            for (Choice choice : Choice.values()) {
                map.put(choice.value, choice);
            }
        }

        public static Choice valueOf(char choice) {
            return (Choice) map.get(choice);
        }
    }
}
