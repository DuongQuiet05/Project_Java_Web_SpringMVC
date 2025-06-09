package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ra.edu.entity.enums.position.FieldType;
import ra.edu.entity.enums.position.PositionStatus;
import ra.edu.entity.enums.position.WorkForm;
import ra.edu.entity.weeks.PositionTechnology;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recruitment_position")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(nullable = false)
    private double minSalary;

    @Column(nullable = false)
    private double maxSalary;

    @Column(nullable = false)
    private int minExperience = 0;

    @Column(nullable = false)
    private LocalDate expiredDate;

    @Column(nullable = false)
    private int numberOfVacancies;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FieldType field;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkForm formOfWork;

    @Column(nullable = false)
    private boolean hot = false;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PositionStatus status = PositionStatus.ENABLED;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "position")
    private List<PositionTechnology> positionTechnologies;

    @OneToMany(mappedBy = "position")
    private List<Application> applications;

}
