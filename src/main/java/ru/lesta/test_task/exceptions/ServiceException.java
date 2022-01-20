package ru.lesta.test_task.exceptions;

public class ServiceException extends RuntimeException {
    public ServiceException(String errMsg) {
        super(errMsg);
    }

}
