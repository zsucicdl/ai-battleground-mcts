package mcts.api;

import java.util.Scanner;

public class InteractiveTesting {
    private static final int GAME_ID = 1;
    private static final String URL = "http://localhost:9080/";

    public static void main(String[] args) {
        int playerId = 1;
        Scanner sc = new Scanner(System.in);
        String stringJson = HttpHelper.GET(URL + "train/play?playerID=" + playerId + "&gameID=" + GAME_ID);
        while(true){
            String line = sc.nextLine().strip().replaceAll(" ", "%20");
            HttpHelper.GET(URL + "train/doAction?playerID=" + playerId + "&gameID=" + GAME_ID + "&action=" + line);
        }
    }
}
//initial 30 29
//initial 70 71
