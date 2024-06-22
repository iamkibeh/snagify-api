package tech.kibetimmanuel.snagifyapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class JobApplicationRequest {
    private String jobTitle;
    private String companyName;
    private String source;
    private LocalDate applicationDate;
    private String location;
    private String applicationStage;
    private String jobDescription;
}
