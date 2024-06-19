package tech.kibetimmanuel.snagifyapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.enums.ApplicationStage;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUser(User user);

    // Additional methods based on your needs (e.g., find by stage, search by keywords)
    List<JobApplication> findJobApplicationByApplicationStage(ApplicationStage applicationStage);
}
