package ru.lesta.test_task.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.lesta.test_task.dto.MatchRequestDTO;
import ru.lesta.test_task.dto.MatchResponseDTO;
import ru.lesta.test_task.dto.ResponseDTO;
import ru.lesta.test_task.exceptions.ServiceException;
import ru.lesta.test_task.service.MatchMakerService;

@Slf4j
@RestController
@RequestMapping("matchmaker")
public class MatchMakerController {

    private final MatchMakerService matchMakerService;

    @Autowired
    public MatchMakerController(MatchMakerService matchMakerService) {
        this.matchMakerService = matchMakerService;
    }

    @PostMapping("users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<MatchResponseDTO> setPlayers(@RequestBody MatchRequestDTO requestDTO) {
        log.debug("setPlayers: started with data: {}", requestDTO);
        MatchResponseDTO response = matchMakerService.createTeams(requestDTO);
        log.debug("setPlayers: finished for data: {} with result {}", requestDTO, response);
        return new ResponseDTO<>(null, response);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDTO<MatchResponseDTO> handlerServiceException(ServiceException ex) {
        String errMessage = ex.getMessage();
        log.error("handlerServiceException: finished with exception: + {}", errMessage);
        return new ResponseDTO<>("Ошибка приложения. Ошибка формирования списка групп. " + errMessage, null);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseDTO<MatchResponseDTO> handlerArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errMessage = ex.getMessage();
        log.error("handlerArgumentTypeMismatchException: finished with exception: + {}", errMessage);
        return new ResponseDTO<>("Значение должно быть указано числом! "
                + "Ошибка ввода в: " + ex.getParameter().getParameterName()
                + ", со значением value: " + ex.getValue(), null);
    }

}
