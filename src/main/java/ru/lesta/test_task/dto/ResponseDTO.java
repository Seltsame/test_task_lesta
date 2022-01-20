package ru.lesta.test_task.dto;

import lombok.Value;

@Value
public class ResponseDTO<T> {
    String error;
    T data;
}
