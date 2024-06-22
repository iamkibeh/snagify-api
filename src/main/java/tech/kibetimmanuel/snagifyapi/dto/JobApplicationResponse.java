package tech.kibetimmanuel.snagifyapi.dto;

import lombok.Builder;
import lombok.Data;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;

import java.util.List;

@Data
@Builder
public class JobApplicationResponse {
    private List<JobApplication> content;
    private int pageNo;
    private int pageSize;
    private int totalPages;
    private long totalRecords;
    private boolean last;
}
