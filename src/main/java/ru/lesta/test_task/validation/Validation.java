package ru.lesta.test_task.validation;

import org.apache.commons.lang3.StringUtils;
import ru.lesta.test_task.exceptions.ServiceException;
import ru.lesta.test_task.model.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Validation {

    public static void checkPlayersList(List<Player> players) {
        if (players == null) {
            throw new ServiceException("Список игроков не может быть null.");
        }
        if (players.size() == 0) {
            throw new ServiceException("Список игроков не может быть пустым.");
        }
    }

    public static void checkPlayersUniqueName(List<Player> players) {
        List<String> playersNameList = new ArrayList<>();
        players.forEach(player -> playersNameList.add(player.getName()));
        Set<String> playersNameSet = new HashSet<>(playersNameList);

        if (playersNameList.size() == playersNameSet.size()) {
            return;
        }

        Map<String, Long> doublesNames = players.stream()
                .collect(Collectors.groupingBy(Player::getName, Collectors.counting()));

        doublesNames.entrySet()
                .removeIf(element -> element.getValue() == 1);

        StringBuilder error = new StringBuilder("Имена игроков не являются уникальными: ");
        doublesNames.keySet().forEach(element -> error.append(element).append(" "));

        throw new ServiceException(error.toString());
    }

    public static void checkGroup(List<Player> players, int groupSize) {
        if (groupSize <= 0) {
            throw new ServiceException("Команда игроков не может быть меньше или равна 0.");
        }
        if (groupSize > players.size()) {
            throw new ServiceException("Команда игроков не может больше количества заявленных участников.");
        }
    }

    public static void checkPlayers(List<Player> players) {
        players.forEach(player ->
        {
            if (player.getName() == null && player.getSkill() == null && player.getLatency() == null) {
                throw new ServiceException("Имя игрока не может быть null.");
            }
            if (StringUtils.isBlank(player.getName())) {
                throw new ServiceException("Имя игрока не может быть пустым.");
            }
            if (player.getSkill() < 0) {
                throw new ServiceException(String.format("Скилл игрока с именем: %s, не быть быть меньше 0.", player.getName()));
            }
            if (player.getLatency() < 0) {
                throw new ServiceException(String.format("Пинг игрока с именем: %s, не может быть меньше 0.", player.getName()));
            }
        });
    }
}
