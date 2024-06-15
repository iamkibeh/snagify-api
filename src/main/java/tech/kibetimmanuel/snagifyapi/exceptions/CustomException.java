package tech.kibetimmanuel.snagifyapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private int status;

    public CustomException(String message, Integer status) {
        super(message);
        this.status = status;
    }

}