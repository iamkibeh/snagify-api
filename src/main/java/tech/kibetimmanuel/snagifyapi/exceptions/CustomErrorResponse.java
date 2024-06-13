package tech.kibetimmanuel.snagifyapi.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomErrorResponse {
    private int status;
    private String message;
    private long timestamp;

}
