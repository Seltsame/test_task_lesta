package ru.lesta.test_task;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import ru.lesta.test_task.dto.MatchRequestDTO;
import ru.lesta.test_task.dto.MatchResponseDTO;
import ru.lesta.test_task.dto.ResponseDTO;
import ru.lesta.test_task.model.Player;
import ru.lesta.test_task.service.service_impl.TeamPlayers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.lesta.test_task.utils.MatchMakerUtils.createPlayers;
import static ru.lesta.test_task.utils.MatchMakerUtils.getMatchRequestDTO;

public class MatchMakerControllerTest extends BaseApplicationTest {

    @Test
    public void testPrintStdOut() {
        List<Player> players = createPlayers();

        MatchRequestDTO requestDTO = getMatchRequestDTO(players, 2);
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(stream);
            System.setOut(printStream);

            ResponseEntity<ResponseDTO<MatchResponseDTO>> response = getResponseDTOResponseEntity(requestDTO);

            assertEquals(response.getStatusCode(), HttpStatus.OK);
            assertNotNull(response.getBody());
            assertNull(response.getBody().getError());

            String stdOut = stream.toString();
            assertTrue(stdOut.contains("Порядковый номер группы: 1\r\n" +
                    "Minimum player's skill in group is: 1.0, maximum player's skill in group is: 1.75, average player's skill in group is: 1.375\r\n" +
                    "Minimum player's latency in group is: 1.15, maximum player's latency in group is: 1.25, average player's latency in group is: 1.2"));
            assertTrue(stdOut.contains("Порядковый номер группы: 2\r\n" +
                    "Minimum player's skill in group is: 2.1, maximum player's skill in group is: 2.55, average player's skill in group is: 2.325\r\n" +
                    "Minimum player's latency in group is: 2.0, maximum player's latency in group is: 2.75, average player's latency in group is: 2.375"));
            assertTrue(stdOut.contains("Порядковый номер группы: 3\r\n" +
                    "Minimum player's skill in group is: 3.55, maximum player's skill in group is: 3.75, average player's skill in group is: 3.65\r\n" +
                    "Minimum player's latency in group is: 3.25, maximum player's latency in group is: 3.95, average player's latency in group is: 3.6"));
            assertTrue(stdOut.contains("Minimum time spent in queue is:"));
            assertTrue(stdOut.contains(", maximum time spent in queue is:"));
            assertTrue(stdOut.contains(", average time spent in queue is:"));
            assertTrue(stdOut.contains("Список игроков группы: Player_1 Player_2"));
            assertTrue(stdOut.contains("Список игроков группы: Player_4 Player_3"));
            assertTrue(stdOut.contains("Список игроков группы: Player_5 Player_6"));

        } finally {
            PrintStream oldPrintStream = System.out;
            System.out.flush();
            System.setOut(oldPrintStream);
        }
    }

    @Test
    public void testController() {
        List<Player> players = createPlayers();
        MatchRequestDTO requestDTO = getMatchRequestDTO(players, 2);

        ResponseEntity<ResponseDTO<MatchResponseDTO>> response = getResponseDTOResponseEntity(requestDTO);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertNull(response.getBody().getError());

        assertNotNull(response.getBody().getData());

        Map<Integer, TeamPlayers> readyTeams = response.getBody().getData().getReadyTeams();

        assertTrue(readyTeams.containsKey(1));
        assertTrue(readyTeams.containsKey(2));
        assertTrue(readyTeams.containsKey(3));

        TeamPlayers firstTeam = readyTeams.get(1);
        assertNotNull(firstTeam);
        List<Player> playerList = firstTeam.getPlayers();
        Player player1_1 = playerList.get(0);
        assertEquals(2, playerList.size());
        assertEquals("Player_1", player1_1.getName());
        assertEquals(1.0, player1_1.getSkill());
        assertEquals(1.15, player1_1.getLatency());
        Player player1_2 = playerList.get(1);
        assertEquals("Player_2", player1_2.getName());
        assertEquals(1.75, player1_2.getSkill());
        assertEquals(1.25, player1_2.getLatency());

        assertEquals(1.0, firstTeam.getMinGroupSkill());
        assertEquals(1.75, firstTeam.getMaxGroupSkill());
        assertEquals(1.375, firstTeam.getAvgGroupSkill());
        assertEquals(1.15, firstTeam.getMinGroupLatency());
        assertEquals(1.25, firstTeam.getMaxGroupLatency());
        assertEquals(1.2, firstTeam.getAvgGroupLatency());

        TeamPlayers secondTeam = readyTeams.get(2);
        assertNotNull(secondTeam);
        List<Player> playerList2 = secondTeam.getPlayers();
        assertEquals(2, playerList2.size());
        Player player2_1 = playerList2.get(0);
        assertEquals("Player_4", player2_1.getName());
        assertEquals(2.1, player2_1.getSkill());
        assertEquals(2.0, player2_1.getLatency());
        Player player2_2 = playerList2.get(1);
        assertEquals("Player_3", player2_2.getName());
        assertEquals(2.55, player2_2.getSkill());
        assertEquals(2.75, player2_2.getLatency());

        assertEquals(2.1, secondTeam.getMinGroupSkill());
        assertEquals(2.55, secondTeam.getMaxGroupSkill());
        assertEquals(2.325, secondTeam.getAvgGroupSkill());
        assertEquals(2.0, secondTeam.getMinGroupLatency());
        assertEquals(2.75, secondTeam.getMaxGroupLatency());
        assertEquals(2.375, secondTeam.getAvgGroupLatency());

        TeamPlayers thirdTeam = readyTeams.get(3);
        assertNotNull(thirdTeam);
        List<Player> playerList3 = thirdTeam.getPlayers();
        assertEquals(2, playerList3.size());
        Player player3_1 = playerList3.get(0);
        assertEquals("Player_5", player3_1.getName());
        assertEquals(3.75, player3_1.getSkill());
        assertEquals(3.25, player3_1.getLatency());
        Player player3_2 = playerList3.get(1);
        assertEquals("Player_6", player3_2.getName());
        assertEquals(3.55, player3_2.getSkill());
        assertEquals(3.95, player3_2.getLatency());

        assertEquals(3.55, thirdTeam.getMinGroupSkill());
        assertEquals(3.75, thirdTeam.getMaxGroupSkill());
        assertEquals(3.65, thirdTeam.getAvgGroupSkill());
        assertEquals(3.25, thirdTeam.getMinGroupLatency());
        assertEquals(3.95, thirdTeam.getMaxGroupLatency());
        assertEquals(3.6, thirdTeam.getAvgGroupLatency());

    }

    private ResponseEntity<ResponseDTO<MatchResponseDTO>> getResponseDTOResponseEntity(MatchRequestDTO requestDTO) {
        RequestEntity<MatchRequestDTO> requestEntity =
                RequestEntity.post(URI.create(matchMakerUrl)).contentType(MediaType.APPLICATION_JSON).
                        body(requestDTO);

        return testRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<ResponseDTO<MatchResponseDTO>>() {
        });
    }
}
