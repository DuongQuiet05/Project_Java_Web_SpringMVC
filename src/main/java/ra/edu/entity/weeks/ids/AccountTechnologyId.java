package ra.edu.entity.weeks.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountTechnologyId implements Serializable {

    @Column(name = "candidate_id")
    private int candidateId;

    @Column(name = "technology_id")
    private int technologyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountTechnologyId that)) return false;
        return candidateId == that.candidateId &&
                technologyId == that.technologyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidateId, technologyId);
    }
}
