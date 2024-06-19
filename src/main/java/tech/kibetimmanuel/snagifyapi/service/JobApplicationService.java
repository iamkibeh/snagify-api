package tech.kibetimmanuel.snagifyapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationRequest;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationUpdateRequest;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.enums.ApplicationStage;
import tech.kibetimmanuel.snagifyapi.exceptions.ResourceNotFound;
import tech.kibetimmanuel.snagifyapi.repository.JobApplicationRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JobApplicationService {
    private final JobApplicationRepository jobApplicationRepo;
    private final JobApplicationRepository jobApplicationRepository;


    public JobApplication saveApplication(JobApplicationRequest application) {
        JobApplication applied = JobApplication.builder()
                .jobTitle(application.getJobTitle())
                .companyName(application.getCompanyName())
                .source(application.getSource())
                .location(application.getLocation())
                .applicationStage(ApplicationStage.valueOf("APPLIED"))
                .applicationDate(application.getApplicationDate())
                .user(currentUser())
                .build();

        return jobApplicationRepo.save(applied);
    }

    public List<JobApplication> getAllApplications() {
        return jobApplicationRepo.findAll();
    }

    public List<JobApplication> getMyApplications() {
        return jobApplicationRepo.findByUser(currentUser());
    }

    public Optional<JobApplication> getApplication(Long id) {
        Optional<JobApplication> optionalJobApplication = jobApplicationRepo.findById(id);
        if(optionalJobApplication.isEmpty()){
            throw new ResourceNotFound("Job application with ID " + id + " not found.");
        }
        return optionalJobApplication;
    }

    @Transactional
    public JobApplication updateApplication(Long id, JobApplicationUpdateRequest updateRequest) {
        Optional<JobApplication> optionalApplication = jobApplicationRepo.findById(id);
        if (optionalApplication.isPresent()) {
            JobApplication application = optionalApplication.get();
            BeanUtils.copyProperties(updateRequest, application, getNullPropertyNames(updateRequest));
            return jobApplicationRepository.save(application);
        } else {
            throw new ResourceNotFound("Job application not found with id " + id);
        }
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}
