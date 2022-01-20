package ru.lesta.test_task.dto;

import lombok.*;
import ru.lesta.test_task.model.Player;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchRequestDTO {

    List<Player> players;
    int groupSize;

}
