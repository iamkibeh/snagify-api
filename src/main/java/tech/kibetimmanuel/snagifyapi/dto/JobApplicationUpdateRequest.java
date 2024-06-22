package tech.kibetimmanuel.snagifyapi.dto;

import lombok.Data;
import tech.kibetimmanuel.snagifyapi.enums.ApplicationStage;

import java.time.LocalDate;

@Data
public class JobApplicationUpdateRequest {
    private String jobTitle;
    private String companyName;
    private String source;
    private LocalDate applicationDate;
    private ApplicationStage applicationStage;
    private String location;
    private String jobDescription;
}
