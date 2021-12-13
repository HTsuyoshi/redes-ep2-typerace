package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import br.usp.each.typerace.client.Logger.*;

/**
 * @class Client gerenciar a conexao com o server
 *
 * @atrr console logger para uma UI mais bonita :D
 */

public class Client extends WebSocketClient {

    public Logger console;
    public String clientId;

    /**
     * Funcao para avisar o server e o usuario que a
     * conexao foi feita com sucesso
     *
     * @param serverUri data enviada para o servidor para começar a conexão
     */

    public Client(URI serverUri, Logger console, String clientId) {
        super(serverUri);
        this.console = console;
        this.clientId = clientId;
    }

    /**
     * Funcao para avisar o server e o usuario que a
     * conexao foi feita com sucesso
     *
     * @param handshakedata data enviada para o servidor para começar a conexão
     */

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        console.appendEffect("Uma nova conexão foi criada!!\n", Color.GREEN, Mode.NONE);
        console.print();
        send(clientId);
    }

    @Override
    public void onMessage(String message) {
        System.out.println(message);
    }

    /**
     * Funcao para indicar quando a conexao foi
     * encerrada e o motivo de ser encerrada
     *
     * @param code   codigo de resposta
     * @param reason razao do fim da conexao
     */

    @Override
    public void onClose(int code, String reason, boolean remote) {
        console.append("Código: ");
        if (code == 1000) {
            console.appendEffect(String.valueOf(code), Color.GREEN, Mode.NONE);
        } else {
            console.appendEffect(String.valueOf(code), Color.RED, Mode.NONE);
        }
        console.println();

        console.append("reason: ");
        console.appendEffect(reason, Color.RED, Mode.NONE);
        console.append("\n\nDigite 0 para prosseguir: ");
        console.print();
    }

    /**
     * Mostrar qual excessao ocorreu para
     * o usuario
     *
     * @param ex excessao que ocorreu
     */

    @Override
    public void onError(Exception ex) {
        console.clear();
        console.append("Uma excessão ocorreu!\n");
        console.appendEffect(ex.toString(), Color.RED, Mode.NONE);
        console.append("\n");
        console.println();
    }
}

