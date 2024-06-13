package tech.kibetimmanuel.snagifyapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorModel {

    private String code;
    private String detail;
    private String source;
}
