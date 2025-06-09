package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ra.edu.entity.enums.application.*;

import java.time.LocalDateTime;

    @Entity
    @Table(name = "application")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Application {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ApplicationProgress progress = ApplicationProgress.PENDING;

        private LocalDateTime canceledAt;

        @Column(columnDefinition = "text")
        private String canceledReason;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ApplicationStatus status = ApplicationStatus.ENABLED;

        @CreationTimestamp
        @Column(name = "createdAt", updatable = false)
        private LocalDateTime createdAt;

        @UpdateTimestamp
        @Column(name = "updatedAt")
        private LocalDateTime updatedAt;

        @Column(columnDefinition = "text")
        private String cvURL;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private CandidateConfirmStatus candidateConfirmed = CandidateConfirmStatus.NOT_CONFIRMED;

        private LocalDateTime appliedAt;

        @Column(columnDefinition = "text")
        private String interviewUrl;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private InterviewForm interviewForm = InterviewForm.ONLINE;

        private LocalDateTime interviewDate;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private InterviewResult result = InterviewResult.WAITING;

        @Column(columnDefinition = "text")
        private String resultNote;

        @ManyToOne
        @JoinColumn(name = "first_viewed_by")
        private Account firstViewedBy;

        @ManyToOne
        @JoinColumn(name = "candidate_id", nullable = false)
        private Account candidate;

        @ManyToOne
        @JoinColumn(name = "position_id", nullable = false)
        private Position position;
    }

