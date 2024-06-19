package tech.kibetimmanuel.snagifyapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tech.kibetimmanuel.snagifyapi.enums.ApplicationStage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "job_application")
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String jobTitle;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String source;
    private LocalDate applicationDate;
    private ApplicationStage applicationStage;
    private String location;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
    private User user;

    @JsonProperty("userId")
    public UUID getUserId() {
        return user != null ? user.getId() : null;
    }

    public String getFormattedApplicationDate() {
        return applicationDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }


}
