package ra.edu.entity.weeks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.Account;
import ra.edu.entity.Technology;
import ra.edu.entity.weeks.ids.AccountTechnologyId;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "candidate_technology")
public class AccountTechnology {

    @EmbeddedId
    private AccountTechnologyId id;

    @ManyToOne
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id")
    private Account account;

    @ManyToOne
    @MapsId("technologyId")
    @JoinColumn(name = "technology_id")
    private Technology technology;


}
