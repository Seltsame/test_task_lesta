package ru.lesta.test_task.service.service_impl;

import org.springframework.stereotype.Service;
import ru.lesta.test_task.dto.MatchRequestDTO;
import ru.lesta.test_task.dto.MatchResponseDTO;
import ru.lesta.test_task.model.Player;
import ru.lesta.test_task.service.MatchMakerService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.lesta.test_task.validation.Validation.*;

@Service
public class MatchMakerServiceImpl implements MatchMakerService {

    //игроки, которые не попали в полноценную группу помещаются в очередь ожидания нового матча
    private Queue<Player> newPlayersQueue = new LinkedList<>();

    public MatchResponseDTO createTeams(MatchRequestDTO requestDTO) {
        var groupSize = requestDTO.getGroupSize();
        var players = requestDTO.getPlayers();

        checkPlayersList(players);
        checkPlayersUniqueName(players);
        checkGroup(players, groupSize);
        checkPlayers(players);

        int groupNumber;
        Map<Integer, TeamPlayers> readyTeams = new HashMap<>();
        List<Player> playersCopy = new ArrayList<>(sort(players));
        int num = 0;
        int groupQuantity = players.size() / groupSize;
        while (num != groupQuantity) {
            groupNumber = ++num;
            long startQueue = System.nanoTime();
            TeamPlayers createdTeam = createTeamPlayers(playersCopy, groupSize, startQueue);
            readyTeams.put(groupNumber, createdTeam);
        }

        readyTeams.forEach((groupNum, team) -> {
            var queueTime = team.getPlayerQueueList();
            addSkillStatistic(team);
            addLatencyStatistic(team);
            addQueueTimeStatistic(queueTime, team);
            sendingGroupStatistics(groupNum, team);
        });

        return new MatchResponseDTO(readyTeams);
    }

    private TeamPlayers createTeamPlayers(List<Player> playersToChoose, int groupSize, long queueStart) {
        TeamPlayers teamPlayers = new TeamPlayers();
        List<Player> newTeam = new ArrayList<>();
        List<Long> playerQueueTimeList = new ArrayList<>();
        for (int i = 0; i < groupSize; i++) {
            Player player = playersToChoose.get(0);
            newTeam.add(player);
            long playerQueue = System.nanoTime() - queueStart;
            playerQueueTimeList.add(playerQueue);
            playersToChoose.remove(player);
        }

        teamPlayers.setPlayers(newTeam);
        teamPlayers.setPlayerQueueList(playerQueueTimeList);
        newPlayersQueue.addAll(playersToChoose);
        return teamPlayers;
    }

    private List<Player> sort(List<Player> players) {
        Map<Player, Double> tmpMap = new HashMap<>();
        players.forEach(element -> tmpMap.put(element, element.getSkill() + element.getLatency()));
        LinkedHashMap<Player, Double> preSort = tmpMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        return new ArrayList<>(preSort.keySet());
    }

    private void addLatencyStatistic(TeamPlayers teamPlayers) {
        var players = teamPlayers.getPlayers();
        players.sort(Comparator.comparing(Player::getLatency));

        double minLatency = players.get(0).getLatency();
        double maxLatency = players.get(players.size() - 1).getLatency();
        double latencySum = 0.0;

        for (Player player : players) {
            latencySum += player.getLatency();
        }

        double avgLatency = latencySum / players.size();
        teamPlayers.setMinGroupLatency(minLatency);
        teamPlayers.setMaxGroupLatency(maxLatency);
        teamPlayers.setAvgGroupLatency(avgLatency);

    }

    private void addSkillStatistic(TeamPlayers teamPlayers) {
        var players = teamPlayers.getPlayers();
        players.sort(Comparator.comparing(Player::getSkill));

        double minSkill = players.get(0).getSkill();
        double maxSkill = players.get(players.size() - 1).getSkill();
        double skillSum = players.stream().mapToDouble(Player::getSkill).sum();

        double avgSkill = skillSum / players.size();
        teamPlayers.setMinGroupSkill(minSkill);
        teamPlayers.setMaxGroupSkill(maxSkill);
        teamPlayers.setAvgGroupSkill(avgSkill);
    }

    private void addQueueTimeStatistic(List<Long> queueTime, TeamPlayers teamPlayers) {
        queueTime.sort(Long::compareTo);

        Long minQueue = queueTime.get(0);
        Long maxQueue = queueTime.get(queueTime.size() - 1);
        long sumQueue = 0;

        for (Long time : queueTime) {
            sumQueue += time;
        }

        double avgQueue = (double) (sumQueue / teamPlayers.getPlayers().size());
        teamPlayers.setMinTimeInQueue(minQueue);
        teamPlayers.setMaxTimeInQueue(maxQueue);
        teamPlayers.setAvgTimeInQueue(avgQueue);
    }

    private void sendingGroupStatistics(int groupNumber, TeamPlayers teamPlayers) {
        System.out.println("Порядковый номер группы: " + groupNumber);
        System.out.println(String.format("Minimum player's skill in group is: %s, maximum player's skill in group is: %s, average player's skill in group is: %s",
                teamPlayers.getMinGroupSkill(), teamPlayers.getMaxGroupSkill(), teamPlayers.getAvgGroupSkill()));
        System.out.println(String.format("Minimum player's latency in group is: %s, maximum player's latency in group is: %s, average player's latency in group is: %s",
                teamPlayers.getMinGroupLatency(), teamPlayers.getMaxGroupLatency(), teamPlayers.getAvgGroupLatency()));
        System.out.println(String.format("Minimum time spent in queue is: %s, maximum time spent in queue is: %s, average time spent in queue is: %s",
                teamPlayers.getMinTimeInQueue(), teamPlayers.getMaxTimeInQueue(), teamPlayers.getAvgTimeInQueue()));
        System.out.print("Список игроков группы:");

        for (Player players2 : teamPlayers.getPlayers()) {
            System.out.print(" " + players2.getName());
        }

        System.out.println();
    }

}
