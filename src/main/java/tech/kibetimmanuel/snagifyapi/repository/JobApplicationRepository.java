package tech.kibetimmanuel.snagifyapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.enums.ApplicationStage;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    Page<JobApplication> findByUser(User user, Pageable pageable);

    // Additional methods based on your needs (e.g., find by stage, search by keywords)
    List<JobApplication> findJobApplicationByApplicationStage(ApplicationStage applicationStage);
}
