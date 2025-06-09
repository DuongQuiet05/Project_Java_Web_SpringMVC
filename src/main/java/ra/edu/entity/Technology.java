package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ra.edu.entity.enums.tech.TechnologyStatus;
import ra.edu.entity.weeks.AccountTechnology;
import ra.edu.entity.weeks.PositionTechnology;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "technology")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class    Technology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechnologyStatus status = TechnologyStatus.ENABLED;

    @Column(columnDefinition = "text")
    private String image;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "is_featured")
    private boolean featured = false;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "technology")
    private List<AccountTechnology> accountTechnologies;

    @OneToMany(mappedBy = "technology")
    private List<PositionTechnology> positionTechnologies;
}
