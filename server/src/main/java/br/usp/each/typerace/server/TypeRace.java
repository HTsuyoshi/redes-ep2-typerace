package br.usp.each.typerace.server;

import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * TypeRace classe que controla a logica do jogo
 */

public class TypeRace {

    private long startTime;
    private boolean running;
    private int playersPlaying;
    private int maxScore;
    private final int wordListSize;
    private Map<String, Player> players;
    private String[] wordList;

    TypeRace() {
        this.startTime = 0;
        this.running = false;
        this.playersPlaying = 0;
        this.maxScore = 10;
        this.wordListSize = 500;
        this.players = new HashMap<>();
    }

    /**
     * init comeca o jogo, adiciona os players conectados ao jogo
     * e comeca o timer para contar a duracao do jogo
     *
     * @param playerList lista dos jogadores conectados, usada para
     *                   adicionar os jogadores no jogo
     */

    public void init(Set<String> playerList) {
        this.setRunning(true);
        this.generateList();
        this.resetPlayers();
        this.setPlayers(playerList);
        this.startTimer();
    }

    /**
     * finish termina o jogo, tira os jogadores antigos,
     * cria uma nova lista para os proximos jogadores
     */

    public void finish() {
        this.setRunning(false);
        this.generateList();
    }

    /**
     * verifyAnswer compara a palavra digitada pelo usuario
     * se chegou no fim da lista volta a lista para o comeco
     * se acertou todas as palavras termina o jogo
     *
     * @param user nome do usuario usado para recuperar a
     *             palavra atual
     * @param word resposta do usuario
     */

    public void verifyAnswer(String user, String word) {
        Player player = players.get(user);
        int listIndex = player.getListIndex();

        player.compareWord(wordList[listIndex], word);

        if (player.getListIndex() == getWordListSize() - 1) {
            player.resetListIndex();
        }

        if (player.getScore() == this.getMaxScore()) {
            finishPlayer(player);
            if (playersPlaying == 0) setRunning(false);
        }
    }

    public void finishPlayer(Player player) {
        player.finish(startTime);
        this.playersPlaying--;
    }

    /**
     * generateList gera uma lista pseudoaleatoria de palavras
     * usando o arquivo roteiroShrek.txt
     */

    public void generateList() {
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("roteiroShrek.txt");

        assert is != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String[] text;
        try {
            text = reader.readLine().split(" ");
        } catch(IOException e) {
            System.err.println("Não foi possível gerar a lista");
            e.printStackTrace();
            return;
        }

        this.wordList = new String[this.wordListSize];
        Random rand = new Random();
        for (int i=0, listIndex = rand.nextInt(); i<this.wordListSize; i++, listIndex = rand.nextInt()) {
            if (listIndex < 0) listIndex = -listIndex;
            this.wordList[i] = text[listIndex % text.length];
        }
    }

    /**
     * scoreboard devolve o scoreboard da partida,
     * usa uma priority queue para criar o ranking
     */

    public String scoreboard() {

        StringBuilder table = new StringBuilder();
        table.append("                   PONTUAÇÃO FINAL\n");
        table.append(" ___________________________________________________________________\n");
        table.append("|_rank_|_name_______________|_word/sec_|_score_|_wrong_|_time_(sec)_|\n");
        //            |_4.___|_Ricardo____________|_1,23_____|_80____|_20____|____________|


        int i = 1;
        float duracaoPartida = 0;
        PriorityQueue<Player> orderedPlayer = getRanking();
        while (!orderedPlayer.isEmpty()) {
            Player player = orderedPlayer.poll();
            float duracaoPlayer = player.getTimeSeconds();
            if (duracaoPlayer > duracaoPartida) {
                duracaoPartida = duracaoPlayer;
            }

            table.append(String.format("| %-4d | %-18s | %-8.2f | %-5d | %-5d | %-10.1f |\n",
                    i++,
                    player.getUser(),
                    player.getVelocity(),
                    player.getScore(),
                    player.getWrong(),
                    duracaoPlayer)
                    .replace(" ", "_"));
        }

        table.append(String.format("\nDuração da partida: %10.2f seg\n", duracaoPartida));
        table.append("\nDigite h para continuar:\n");

        return table.toString();
    }

    /* Getter and Setters */

    public void setPlayers(Set<String> playerList) {
        for (String playerName : playerList) {
            players.put(playerName, new Player(playerName));
        }
        setPlayersPlaying(playerList.size());
    }

    public void resetPlayers() {
        this.players = new HashMap<>();
        setPlayersPlaying(0);
    }

    public String getMessage(String user) {
        Player player = players.get(user);
        return String.format("Acertos: %4d/%d%nErros:   %4d%nPalavra: %s",
                player.getScore(),
                getMaxScore(),
                player.getWrong(),
                getWord(player));
    }

    /**
     * getWord devolve a palavra, acertos e erros
     * do usuario especificado
     */

    public String getWord(Player player) {
        return wordList[player.getListIndex()];
    }

    public boolean isRunning() { return this.running; }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getMaxScore() {
        return this.maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public int getWordListSize() {
        return this.wordListSize;
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

    public void setPlayersPlaying(int playersInGame) {
        this.playersPlaying= playersInGame;
    }

    public int getPlayersPlaying() {
        return this.playersPlaying;
    }

    public PriorityQueue<Player> getRanking() {
        PriorityQueue<Player> ranking;
        ranking = new PriorityQueue<Player>(new PlayerComparator());
        for (Player player : players.values()) {
            ranking.add(player);
        }
        return ranking;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

}

/**
 * PlayerComparator e usado para criar o ranking dos jogadores.
 */

class PlayerComparator implements Comparator<Player> {
    @Override
    public int compare(Player player1, Player player2) {
        if(player1.getScore() > player2.getScore()) return -1;
        if(player1.getScore() < player2.getScore()) return 1;
        if(player1.getTime() > player2.getTime()) return 1;
        if(player1.getTime() < player2.getTime()) return -1;
        return 0;
    }
}

