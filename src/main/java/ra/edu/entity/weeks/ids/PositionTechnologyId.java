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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PositionTechnologyId implements Serializable {

    @Column(name = "position_id")
    private int positionId;

    @Column(name = "technology_id")
    private int technologyId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionTechnologyId that)) return false;
        return positionId == that.positionId &&
                technologyId == that.technologyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(positionId, technologyId);
    }
}
