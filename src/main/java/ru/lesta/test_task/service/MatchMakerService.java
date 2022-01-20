package ru.lesta.test_task.service;

import ru.lesta.test_task.dto.MatchRequestDTO;
import ru.lesta.test_task.dto.MatchResponseDTO;

public interface MatchMakerService {

    MatchResponseDTO createTeams(MatchRequestDTO requestDTO);
}
