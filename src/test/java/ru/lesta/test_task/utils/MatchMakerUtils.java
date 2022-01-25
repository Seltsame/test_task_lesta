package ru.lesta.test_task.utils;

import ru.lesta.test_task.dto.MatchRequestDTO;
import ru.lesta.test_task.model.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchMakerUtils {

    public static ArrayList<Player> createPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Player_5", 3.75, 3.25));
        players.add(new Player("Player_2", 1.75, 1.25));
        players.add(new Player("Player_7", 4.55, 4.25));
        players.add(new Player("Player_4", 2.10, 2.00));
        players.add(new Player("Player_3", 2.55, 2.75));
        players.add(new Player("Player_6", 3.55, 3.95));
        players.add(new Player("Player_1", 1.00, 1.15));
        return players;
    }


    public static MatchRequestDTO getMatchRequestDTO(List<Player> players, int groupSize) {
     return new MatchRequestDTO(players, groupSize);
    }

}
