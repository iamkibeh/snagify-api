package tech.kibetimmanuel.snagifyapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponseModel {
    private String type;
    private List<ValidationErrorModel> errors;
    private int status;
}