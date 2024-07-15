package tech.kibetimmanuel.snagifyapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationRequest;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationResponse;
import tech.kibetimmanuel.snagifyapi.dto.JobApplicationUpdateRequest;
import tech.kibetimmanuel.snagifyapi.entity.JobApplication;
import tech.kibetimmanuel.snagifyapi.service.AuthenticationService;
import tech.kibetimmanuel.snagifyapi.service.JobApplicationService;
import tech.kibetimmanuel.snagifyapi.service.impl.JobApplicationServiceImpl;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobApplicationService jobApplicationService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<JobApplication> createApplication(@RequestBody JobApplicationRequest application) {
        JobApplication newApplication = jobApplicationService.saveApplication(application);
        URI location = URI.create("/api/v1/applications/"+ newApplication.getId());
        return ResponseEntity.created(location).body(newApplication);
    }

    @GetMapping
    public ResponseEntity<JobApplicationResponse> getApplications(
            @RequestParam(value = "currentUser", required = false, defaultValue = "false") String currentUser,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size
    ){
        if (currentUser !=null && currentUser.equals("true")){
            return ResponseEntity.ok().body(jobApplicationService.getMyApplications(page, size));
        }
         else {
            return ResponseEntity.ok().body(jobApplicationService.getAllApplications(page, size));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<JobApplication>> getApplication(@PathVariable UUID id){
        Optional<JobApplication> application = jobApplicationService.getApplication(id);
        return ResponseEntity.ok().body(application);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplication> updateApplication(@PathVariable UUID id, @RequestBody JobApplicationUpdateRequest updateRequest) {
        JobApplication updatedApplication = jobApplicationService.updateApplication(id, updateRequest);
        return ResponseEntity.ok().body(updatedApplication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable UUID id) {
        jobApplicationService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JobApplicationResponse> searchJobApplications(
            @RequestParam String query,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        Pageable pageable = PageRequest.of(page, size);
        JobApplicationResponse jobApplicationResponse = jobApplicationService.searchJobApplications(authenticationService.currentUser().getId(), query, pageable);
        return ResponseEntity.ok(jobApplicationResponse);
    }
}
