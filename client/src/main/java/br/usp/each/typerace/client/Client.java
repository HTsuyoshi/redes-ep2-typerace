package br.usp.each.typerace.client;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import br.usp.each.typerace.client.Logger.*;

/**
 * Client gerenciar a conexao com o server
 */

public class Client extends WebSocketClient {

    public Logger console;

    public Client(URI serverUri, Logger console) {
        super(serverUri);
        this.console = console;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        console.appendEffect("Uma nova conexão foi criada!!\n", Color.GREEN, Mode.NONE);
        console.appendEffect("Digite alguma mensagem para continuar\n", Color.GREEN, Mode.NONE);
        console.print();
    }

    @Override
    public void onMessage(String message) {
        console.clear();
        console.append(message);
        console.append("\nSua mensagem: ");
        console.print();
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        console.clear();
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

    @Override
    public void onError(Exception ex) {
        console.clear();
        console.append("Uma excessão ocorreu!\n");
        console.appendEffect(ex.toString(), Color.RED, Mode.NONE);
        console.append("\n");
        console.println();
    }
}

