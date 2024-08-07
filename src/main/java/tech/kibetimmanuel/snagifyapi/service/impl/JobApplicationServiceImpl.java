package tech.kibetimmanuel.snagifyapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationRequest;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationResponse;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationUpdateRequest;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;
import tech.kibetimmanuel.snagifyapi.entity.User;
import tech.kibetimmanuel.snagifyapi.enums.ApplicationStage;
import tech.kibetimmanuel.snagifyapi.exceptions.ResourceNotFound;
import tech.kibetimmanuel.snagifyapi.repository.JobApplicationRepository;
import tech.kibetimmanuel.snagifyapi.service.JobApplicationService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl  implements JobApplicationService {
    private final JobApplicationRepository jobApplicationRepo;
    private final JobApplicationRepository jobApplicationRepository;


    public JobApplication saveApplication(JobApplicationRequest application) {
        JobApplication applied = JobApplication.builder()
                .jobTitle(application.getJobTitle())
                .companyName(application.getCompanyName())
                .source(application.getSource())
                .location(application.getLocation())
                .applicationStage(ApplicationStage.valueOf(application.getApplicationStage()))
                .applicationDate(application.getApplicationDate())
                .jobDescription(application.getJobDescription())
                .user(currentUser())
                .build();

        return jobApplicationRepo.save(applied);
    }

    @Override
    public JobApplicationResponse getAllApplications(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<JobApplication> jobApplications = jobApplicationRepo.findAll(pageable);
        return JobApplicationResponse.builder()
                .content(jobApplications.getContent())
                .pageNo(jobApplications.getNumber())
                .pageSize(jobApplications.getSize())
                .totalPages(jobApplications.getTotalPages())
                .totalRecords(jobApplications.getTotalElements())
                .last(jobApplications.isLast())
                .build();
    }

    @Override
    public JobApplicationResponse getMyApplications(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<JobApplication> jobApplications = jobApplicationRepo.findByUser(currentUser(), pageable);
        return JobApplicationResponse.builder()
                .content(jobApplications.getContent())
                .pageNo(jobApplications.getNumber())
                .pageSize(jobApplications.getSize())
                .totalPages(jobApplications.getTotalPages())
                .totalRecords(jobApplications.getTotalElements())
                .last(jobApplications.isLast())
                .build();
    }
    @Override
    public Optional<JobApplication> getApplication(UUID id) {
        Optional<JobApplication> optionalJobApplication = optionalJobApplication(id);
        if(optionalJobApplication.isEmpty()){
            throw new ResourceNotFound("Job application with ID " + id + " not found.");
        }
        return optionalJobApplication;
    }

    @Transactional
    @Override
    public JobApplication updateApplication(UUID id, JobApplicationUpdateRequest updateRequest) {
        Optional<JobApplication> optionalApplication = optionalJobApplication(id);
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

    @Override
    public void delete(UUID id) {
        Optional<JobApplication> optionalJobApplication = optionalJobApplication(id);
        if(optionalJobApplication.isEmpty()){
            throw new ResourceNotFound("Job application with ID " + id + " not found.");
        }
        jobApplicationRepo.deleteById(id);
    }


    @Override
    public JobApplicationResponse  searchJobApplications(UUID userId, String search, Pageable pageable) {
        Page<JobApplication> jobApplications = jobApplicationRepository.searchJobApplications(userId, search, pageable);
        return JobApplicationResponse.builder()
                .content(jobApplications.getContent())
                .pageNo(jobApplications.getNumber())
                .pageSize(jobApplications.getSize())
                .totalPages(jobApplications.getTotalPages())
                .totalRecords(jobApplications.getTotalElements())
                .last(jobApplications.isLast())
                .build();
    }

    private Optional<JobApplication> optionalJobApplication(UUID id) {
        return  jobApplicationRepo.findById(id);
    }
}

