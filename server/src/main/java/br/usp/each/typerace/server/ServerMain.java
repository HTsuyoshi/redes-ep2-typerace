package br.usp.each.typerace.server;

import org.java_websocket.server.WebSocketServer;

import java.util.HashMap;


public class ServerMain {

    public static int port;

    private WebSocketServer server;

    public ServerMain(WebSocketServer server) {
        this.server = server;
    }

    public void init() {
        System.out.println("Iniciando servidor na porta " + port);
        server.start();
    }

    /**
     * Vai receber a porta do server no argumento para
     * iniciar o server na porta especificada
     *
     */

    public static void main(String[] args) {

        port = 8080;

        if (args.length != 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    System.err.println("A porta informada está fora do limite de portas");
                    System.err.println("Usando a porta padrão...");
                    port = 8080;
                }
            } catch (Exception exception) {
                System.err.println("A porta informada não é valida");
                exception.printStackTrace();
            }
        }

        WebSocketServer server = new Server(port, new HashMap<>());

        ServerMain main = new ServerMain(server);

        main.init();
    }

}