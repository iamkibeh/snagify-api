package tech.kibetimmanuel.snagifyapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationRequest;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationUpdateRequest;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;
import tech.kibetimmanuel.snagifyapi.service.JobApplicationService;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobApplicationService  jobApplicationService;

    @PostMapping
    public ResponseEntity<JobApplication> createApplication(@RequestBody JobApplicationRequest application) {
        JobApplication newApplication = jobApplicationService.saveApplication(application);
        URI location = URI.create("/api/v1/applications/"+ newApplication.getId());
        return ResponseEntity.created(location).body(newApplication);
    }

    @GetMapping
    public ResponseEntity<List<JobApplication>> getApplications(@RequestParam(value = "currentUser", required = false) Boolean currentUser){
        if (currentUser !=null && currentUser){
            List<JobApplication> myApplications = jobApplicationService.getMyApplications();
            return ResponseEntity.ok().body(myApplications);
        }
         else {
            return ResponseEntity.ok().body(jobApplicationService.getAllApplications());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<JobApplication>> getApplication(@PathVariable Long id){
        Optional<JobApplication> application = jobApplicationService.getApplication(id);
        return ResponseEntity.ok().body(application);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplication> updateApplication(@PathVariable Long id, @RequestBody JobApplicationUpdateRequest updateRequest) {
        JobApplication updatedApplication = jobApplicationService.updateApplication(id, updateRequest);
        return ResponseEntity.ok().body(updatedApplication);
    }

}