package ru.lesta.test_task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import ru.lesta.test_task.dto.MatchRequestDTO;
import ru.lesta.test_task.dto.MatchResponseDTO;
import ru.lesta.test_task.dto.ResponseDTO;
import ru.lesta.test_task.model.Player;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static ru.lesta.test_task.utils.MatchMakerUtils.getMatchRequestDTO;

public class MatchMakerServiceTest extends BaseApplicationTest {

    @Test
    public void testErrorNotUniqueName() {

        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Player_1", 1.00, 1.00));
        players.add(new Player("Player_2", 2.50, 0.75));
        players.add(new Player("Player_1", 1.50, 0.5));
        players.add(new Player("Player_3", 1.50, 0.5));
        players.add(new Player("Player_2", 1.50, 0.5));
        players.add(new Player("Player_2", 1.50, 0.5));

        MatchRequestDTO requestDTO = getMatchRequestDTO(players, 3);

        ResponseEntity<ResponseDTO<MatchResponseDTO>> response = getResponseDTOResponseEntity(requestDTO);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getError());
        assertEquals(response.getBody().getError(), "Ошибка приложения. Ошибка формирования списка групп. Имена игроков не являются уникальными: Player_2 Player_1 ");

    }

    @ParameterizedTest
    @CsvSource(value = {"4", "0"})
    public void testErrorInvalidGroups(int groupSize) {
        MatchRequestDTO requestDTO = getMatchRequestDTO(new ArrayList<>(), groupSize);

        ResponseEntity<ResponseDTO<MatchResponseDTO>> response = getResponseDTOResponseEntity(requestDTO);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        String error = response.getBody().getError();
        assertNotNull(error);

        if (groupSize == 4) {
            assertEquals(error, "Ошибка приложения. Ошибка формирования списка групп. Список игроков не может быть пустым.");
        }

        if (groupSize < 0) {
            assertEquals(error, "Ошибка приложения. Ошибка формирования списка групп. Команда игроков не может быть меньше или равна 0.");
        }
    }


    @Test
    public void testEmptyPlayersList() {
        MatchRequestDTO requestDTO = getMatchRequestDTO(new ArrayList<>(), 5);

        ResponseEntity<ResponseDTO<MatchResponseDTO>> response = getResponseDTOResponseEntity(requestDTO);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        String error = response.getBody().getError();
        assertNotNull(error);

        assertEquals(error, "Ошибка приложения. Ошибка формирования списка групп. Список игроков не может быть пустым.");
    }

    @Test
    public void testNullPlayersList() {
        MatchRequestDTO requestDTO = getMatchRequestDTO(null, 5);

        ResponseEntity<ResponseDTO<MatchResponseDTO>> response = getResponseDTOResponseEntity(requestDTO);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        String error = response.getBody().getError();

        assertEquals(error, "Ошибка приложения. Ошибка формирования списка групп. Список игроков не может быть null.");
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "-0.12|0.75", "1.55|-0.1"})
    public void test(double skill, double latency) {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Player_1", 1.00, 1.00));
        players.add(new Player("Player_2", skill, latency));
        players.add(new Player("Player_3", 1.55, 1.25));

        MatchRequestDTO requestDTO = getMatchRequestDTO(players, 3);

        ResponseEntity<ResponseDTO<MatchResponseDTO>> response = getResponseDTOResponseEntity(requestDTO);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
        String error = response.getBody().getError();

        if (skill < 0) {
            assertEquals(error, "Ошибка приложения. Ошибка формирования списка групп. Скилл игрока с именем: Player_2, не быть быть меньше 0.");
        }
        if (latency < 0) {
            assertEquals(error, "Ошибка приложения. Ошибка формирования списка групп. Пинг игрока с именем: Player_2, не может быть меньше 0.");
        }
    }

    private ResponseEntity<ResponseDTO<MatchResponseDTO>> getResponseDTOResponseEntity(MatchRequestDTO requestDTO) {
        RequestEntity<MatchRequestDTO> requestEntity =
                RequestEntity.post(URI.create(matchMakerUrl)).contentType(MediaType.APPLICATION_JSON).
                        body(requestDTO);

        return testRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<ResponseDTO<MatchResponseDTO>>() {
        });
    }

}
