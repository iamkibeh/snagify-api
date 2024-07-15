package tech.kibetimmanuel.snagifyapi.service;

import org.springframework.data.domain.Pageable;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationRequest;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationResponse;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationUpdateRequest;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;

import java.util.Optional;
import java.util.UUID;

public interface JobApplicationService {
    JobApplication saveApplication(JobApplicationRequest application);
    JobApplicationResponse getAllApplications(int pageNo, int pageSize);
    JobApplicationResponse getMyApplications(int pageNo, int pageSize);
    JobApplicationResponse  searchJobApplications(UUID userId, String search, Pageable pageable);
    JobApplication updateApplication(UUID id, JobApplicationUpdateRequest updateRequest);
    void delete(UUID id);
    Optional<JobApplication> getApplication(UUID id);
}
