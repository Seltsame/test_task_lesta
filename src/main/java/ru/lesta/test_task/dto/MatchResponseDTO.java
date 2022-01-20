package ru.lesta.test_task.dto;


import lombok.*;
import ru.lesta.test_task.service.service_impl.TeamPlayers;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponseDTO {

    Map<Integer, TeamPlayers> readyTeams;
}
