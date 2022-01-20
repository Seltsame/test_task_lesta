package ru.lesta.test_task.service.service_impl;

import lombok.*;
import ru.lesta.test_task.model.Player;

import java.util.List;

@Data
@NoArgsConstructor
public class TeamPlayers {

    List<Player> players;
    List<Long> playerQueueList;

    long minTimeInQueue;
    long maxTimeInQueue;
    double avgTimeInQueue;

    double minGroupSkill;
    double maxGroupSkill;
    double avgGroupSkill;

    double minGroupLatency;
    double maxGroupLatency;
    double avgGroupLatency;
}
