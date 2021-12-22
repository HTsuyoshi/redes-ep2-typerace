package br.usp.each.typerace.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TypeRaceTest {

    @Mock
    private Set<String> playersNameArray;

    @Mock
    private Map<String, Player> players;

    @Mock
    private Player player1;

    @Mock
    private Player player2;

    @Mock
    private Player player3;

    @InjectMocks
    private TypeRace typeRace;

    @BeforeEach
    public void setup() {
        this.typeRace = new TypeRace();

        this.player1 = new Player("clientId1");
        this.player2 = new Player("idClient2");
        this.player3 = new Player("idClient3");

        this.players = new HashMap<String,Player>();

        this.players.put(player1.getUser(), player1);
        this.players.put(player2.getUser(), player2);
        this.players.put(player3.getUser(), player3);

        this.playersNameArray = players.keySet();

    }

    @Test
    public void deveComecarOJogo() {
        typeRace.init(players.keySet());

        assertEquals(true, typeRace.isRunning());
    }

    @Test
    public void deveDarAMesmaListaParaOsJogadores() {
        typeRace.init(players.keySet());

        assertEquals(typeRace.getWord(player1), typeRace.getWord(player2));
    }

    @Test
    public void deveDarUmPontoParaAcerto() {
        typeRace.init(players.keySet());

        typeRace.verifyAnswer(player1.getUser(), typeRace.getWord(player1));

        assertEquals(1, typeRace.getPlayer(player1.getUser()).getScore());
        assertEquals(0, typeRace.getPlayer(player1.getUser()).getWrong());
    }

    @Test
    public void deveDarUmPontoParaErro() {
        typeRace.init(players.keySet());

        typeRace.verifyAnswer(player1.getUser(), "PALAVRAERRADAAAAAAAA1");

        assertEquals(0, typeRace.getPlayer(player1.getUser()).getScore());
        assertEquals(1, typeRace.getPlayer(player1.getUser()).getWrong());
    }

    @Test
    public void deveOrdenarCorretamenteORankingPorPontos() {
        typeRace.init(players.keySet());

        typeRace.verifyAnswer(player1.getUser(), "PALAVRAERRADAAAAAAAA1");
        typeRace.verifyAnswer(player2.getUser(), typeRace.getWord(player2));
        player2.addListIndex();
        typeRace.verifyAnswer(player2.getUser(), typeRace.getWord(player2));
        typeRace.verifyAnswer(player3.getUser(), typeRace.getWord(player3));

        PriorityQueue<Player> ranking = typeRace.getRanking();
        assertEquals(player2.getUser(), ranking.poll().getUser());
        assertEquals(player3.getUser(), ranking.poll().getUser());
        assertEquals(player1.getUser(), ranking.poll().getUser());
    }

    @Test
    public void deveOrdenarCorretamenteORankingPeloTempo() {
        typeRace.init(players.keySet());
        TimeUnit time = TimeUnit.SECONDS;
        long timeToSleep = 1L;


        try {
            typeRace.verifyAnswer(player3.getUser(), typeRace.getWord(player3));
            time.sleep(timeToSleep);
            typeRace.finishPlayer(typeRace.getPlayer(player3.getUser()));

            typeRace.verifyAnswer(player2.getUser(), typeRace.getWord(player2));
            time.sleep(timeToSleep);
            typeRace.finishPlayer(typeRace.getPlayer(player2.getUser()));

            typeRace.verifyAnswer(player1.getUser(), typeRace.getWord(player1));
            time.sleep(timeToSleep);
            typeRace.finishPlayer(typeRace.getPlayer(player1.getUser()));

        } catch (InterruptedException e){
            e.printStackTrace();
        }

        PriorityQueue<Player> ranking = typeRace.getRanking();
        assertEquals(player3.getUser(), ranking.poll().getUser());
        assertEquals(player2.getUser(), ranking.poll().getUser());
        assertEquals(player1.getUser(), ranking.poll().getUser());
    }
}