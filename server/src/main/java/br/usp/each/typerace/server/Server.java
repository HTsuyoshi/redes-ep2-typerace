package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Server extends WebSocketServer {

    private TypeRace typeRace;
    private final Map<String, WebSocket> connections;

    public Server(int port, Map<String, WebSocket> connections) {
        super(new InetSocketAddress(port));
        this.connections = connections;
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
        if (conn != null) {
            System.out.println(String.format("Um erro ocorreu com a conexão: %s\n", getUserId(conn).get()));
        }
        ex.printStackTrace();
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Optional<String> userId = getUserId(conn);
        if (userId.isPresent()) {
            System.out.printf("%s se desconectou%n", userId.get());
            if (typeRace.isRunning()) {
                typeRace.finishPlayer(typeRace.getPlayer(userId.get()));
            }
            this.connections.remove(conn);
        }

        if (!conn.isClosed()) {
            conn.close(code, reason);
        }
    }

    /**
     * onMessage quando uma mensagem e recebida, caso seja
     *  uma nova conexao, a funcao adiciona o jogador no
     * hashmap connections.
     *
     * Se o jogador ja estiver cadastrado a entrada do jogador
     * e interpretada como uma escolha do menu
     *
     * Se estiver rodando um jogo a entrada e interpretada
     * como palavra do jogo
     *
     */

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message.length() == 0) return;

        Optional<String> optionalUser = this.getUserId(conn);
        if (optionalUser.isEmpty()) {
            verifyUser(conn);
            return;
        }

        String user = optionalUser.get();
        if (this.typeRace.isRunning()) {
            inputToTypeRace(conn, user, message);
            return;
        }

        Choice userChoice = getChoice(message.charAt(0));
        switch (userChoice) {
            case HELP:
                this.help(conn);
                break;

            case START_GAME:
                broadcast(String.format("%s começou o jogo\n", user));
                broadcast("Gerando lista de palavras\n");
                typeRace.init(this.connections.keySet());
                broadcast(typeRace.getWord(user));
                break;

            case SET_MAX_SCORE:
                if (message.length() < 3) return;
                this.setMaxScore(message.substring(2).strip(), conn, user);
                break;

            case QUIT:
                System.out.printf("%s saiu do jogo com sucesso!\n", user);
                removeUser(conn);
                break;

            default:
                conn.send("Esse comando não está na lista de comandos");
        }
    }

    public void verifyUser(WebSocket conn) {
        String user = conn.getResourceDescriptor();
        if (validUser(conn, user)) {
            addUser(conn, user);
            System.out.println("Conexão feita com sucesso!");
        }
    }

    /**
     * inputToTypeRace logica do jogo typeRace:
     *   * Comparar palavras
     *   * Terminar o jogo
     *
     * @param conn    conexao para mandar mensagem
     * @param user    usado para verificar se esta jogando ou
     *                esta cadastrado
     * @param message mensagem do jogador
     */

    public void inputToTypeRace(WebSocket conn, String user, String message) {
        if (typeRace.getPlayersPlaying() == 0)  {
            typeRace.setRunning(false);
            broadcast(typeRace.scoreboard());
            return;
        }

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
            conn.send(this.typeRace.getWord(user));
        }
    }

    /**
     * validUser Verifica se o usuario viola alguma politica
     * do jogo
     */

    public boolean validUser(WebSocket conn, String clientId) {
        if (clientId.length() <= 1) {
            conn.send("Este ID está vazio!\n" +
                    "Por escolha um ID.\n");
            conn.close(1008, "Sem Id"); // Policy Violation (no ID)
            return false;
        }

        clientId = clientId.substring(1);
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
     * addUser Adiciona o jogador e envia o menu do jogo
     */

    public void addUser(WebSocket conn, String clientId) {
        connections.put(clientId, conn);
        if (typeRace.isRunning()) {
            conn.send("Uma partida está em execução no momento, por favor espere a partida acabar.");
            return;
        }

        broadcast(String.format("%s acabou de entrar!\n" +
                "Atualmente existem %d jogadores conectados.\n" +
                "\nPressione h para continuar\n", clientId, connections.size()));

        help(conn);
    }

    public void help(WebSocket conn) {
        StringBuilder message = new StringBuilder("=== Bem vindo ao servidor do TypeRace! ===\n\n");
        message.append("Lista de commandos disponíveis:\n\n");
        message.append("\tH - Para ver essa mensagem de texto\n");
        message.append("\tC - Para começar o jogo\n");
        message.append("\tN - Para escolher o número máximo de pontos\n");
        message.append("\tEx:\n");
        message.append("\t\tN 10\n");
        message.append("\tS - Para sair do jogo\n\n");
        message.append(String.format("O número de pontos para acabar é: %d\n", typeRace.getMaxScore()));
        conn.send(message.toString());
    }


    public boolean validAndPositive(String stringNumber) {
        int number;
        try {
            number = Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            return false;
        }

        return (number > 0 && number < 100000);
    }

    public boolean isInGame(String user) {
        return (this.typeRace.getPlayer(user) != null);
    }

    public void removeUser(WebSocket conn) {
        Optional<String> userId = getUserId(conn);
        if (userId.isEmpty()) return;

        conn.send(String.format("Obrigado por jogar! %s", userId.get()));
        connections.remove(userId.get(), conn);
        conn.close(1000, "O usuário quis sair do jogo");
    }

    /* Getter and Setters*/

    public void setMaxScore(String maxScore, WebSocket conn, String user) {
        if (validAndPositive(maxScore) ) {
            int score = Integer.parseInt(maxScore);
            if (score > typeRace.getWordListSize()) return;
            typeRace.setMaxScore(score);
            broadcast(String.format("O jogador %s definiu %d para a pontuação máxima\n" +
                    "\nPressione h para continuar", user, typeRace.getMaxScore()));
        } else {
            conn.send("Esse não é um número válido para pontuação máxima\n" +
                    "\nPressione h para continuar");
        }
    }

    public boolean isPlaying(String user) {
        return this.typeRace.getPlayer(user).isPlaying();
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
        HELP('h'),
        START_GAME('c'),
        SET_MAX_SCORE('n'),
        QUIT('s'),
        NONE(' ');

        private final char value;
        private static final Map<Object, Object> map = new HashMap<>();

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
