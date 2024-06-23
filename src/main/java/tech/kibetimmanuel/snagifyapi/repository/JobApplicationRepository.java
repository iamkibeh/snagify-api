package tech.kibetimmanuel.snagifyapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.enums.ApplicationStage;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {
    Page<JobApplication> findByUser(User user, Pageable pageable);

    // Additional methods based on your needs (e.g., find by stage, search by keywords)
    List<JobApplication> findJobApplicationByApplicationStage(ApplicationStage applicationStage);

    @Query("SELECT j FROM JobApplication j WHERE " +
            "j.user.id = :userId AND (" +
            "j.companyName LIKE %:search% OR " +
            "j.jobTitle LIKE %:search% OR " +
            "j.location LIKE %:search% OR " +
//            "CAST(j.applicationStage AS string) LIKE %:search% OR " +
            "j.source LIKE %:search%)")
    Page<JobApplication> searchJobApplications(@Param("userId") UUID userId, @Param("search") String keyword, Pageable pageable);
}
