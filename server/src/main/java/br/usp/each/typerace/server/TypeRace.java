package br.usp.each.typerace.server;

import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TypeRace {

    private long startTime;
    private boolean running;
    private int playersPlaying;
    private int maxScore;
    private int wordListSize;
    private final Map<String, Player> players;
    private String[] wordList;

    TypeRace() {
        this.startTime = 0;
        this.running = false;
        this.playersPlaying = 0;
        this.maxScore = 10;
        this.wordListSize = 100;
        this.players = new HashMap<>();
        this.generateList();
    }

    public void init(Set<String> playerList) {
        this.setRunning(true);
        this.setPlayers(playerList);
        this.startTimer();
    }

    public void setPlayers(Set<String> playerList) {
        for (String playerName : playerList) {
            players.put(playerName, new Player(playerName));
        }
        setPlayersPlaying(playerList.size());
    }

    public void setPlayersPlaying(int playersInGame) {
        this.playersPlaying= playersInGame;
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

    public void setWordListSize(int listSize) {
        this.wordListSize = listSize;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void verifyAnswer(String user, String word) {
        Player player = players.get(user);
        int listIndex = player.getIndex();

        player.compareWord(wordList[listIndex], word);

        if (listIndex == this.wordListSize - 1 ||
            player.getScore() == this.getMaxScore()) {
            playerFinished(player);
            if (playersPlaying == 0) setRunning(false);
        }
    }

    public void playerFinished(Player player) {
        player.finish(startTime);
        this.playersPlaying--;
    }

    public Player getPlayer(String name) {
        return players.get(name);
    }

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

    public String getWord(String user) {
        Player player = players.get(user);
        return String.format("Acertos: %4d/%d%nErros:   %4d/%d%nPalavra: %s",
                player.getScore(),
                this.maxScore,
                player.getWrong(),
                this.wordListSize,
                wordList[player.getIndex()]);
    }

    public String scoreboard() {

        PriorityQueue<Player> orderedList = new PriorityQueue<>(new PlayerComparator());
        StringBuilder table = new StringBuilder();
        table.append("                   PONTUAÇÃO FINAL\n");
        table.append(" _________________________________________________________\n");
        table.append("|_rank_|_name_______________|_palavra/sec_|_score_|_wrong_|\n");
        //            |_4.___|_Ricardo____________|_1,23________|_80____|_20____|

        orderedList.addAll(players.values());

        int i = 1;
        float duracaoPartida = 0;
        for(Player player : orderedList) {
            float duracaoPlayer = player.getTimeSeconds();
            if (duracaoPlayer > duracaoPartida) {
                duracaoPartida = duracaoPlayer;
            }

            table.append(String.format("| %-4d.| %-19s| %-12.2f| %-6d| %-6d|\n",
                    i++,
                    player.getUser(),
                    player.getVelocity(),
                    player.getScore(),
                    player.getWrong())
                    .replace(" ", "_"));
        }

        table.append(String.format("\nDuração da partida: %10.2f seg\n", duracaoPartida));
        table.append("\nDigite h para continuar:\n");

        return table.toString();
    }
}

class PlayerComparator implements Comparator<Player> {
    @Override
    public int compare(Player player1, Player player2) {
        if(player1.getScore() > player2.getScore()) return -1;
        if(player1.getScore() < player2.getScore()) return 1;

        return Long.compare(player1.getTime(), player2.getTime());
    }
}

