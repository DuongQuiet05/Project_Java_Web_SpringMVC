package ra.edu.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ra.edu.entity.auth.RememberToken;


//import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Gender;
import ra.edu.entity.enums.account.Role;
import ra.edu.entity.weeks.AccountTechnology;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "account")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 12)
    private String phone;

    private int experience = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender; //MALE, FEMALE, OTHER

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.CANDIDATE; // ADMIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status = AccountStatus.ENABLE;

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String avatar;

    @Column(nullable = false)
    private LocalDate dob;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RememberToken> tokens = new ArrayList<>();


    @OneToMany(mappedBy = "account")
    private List<AccountTechnology> accountTechnologies;

    // liên kết application
    @OneToMany(mappedBy = "candidate")
    private List<Application> candidateApplications;

    @OneToMany(mappedBy = "firstViewedBy")
    private List<Application> viewedApplications;


}