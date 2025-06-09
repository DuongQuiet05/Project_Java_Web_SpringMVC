package ra.edu.entity.weeks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ra.edu.entity.Position;
import ra.edu.entity.Technology;
import ra.edu.entity.weeks.ids.PositionTechnologyId;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "recruitment_position_technology")
public class PositionTechnology {

    @EmbeddedId
    private PositionTechnologyId id;

    @ManyToOne
    @MapsId("positionId")
    @JoinColumn(name = "position_id")
    private Position position;

    @ManyToOne
    @MapsId("technologyId")
    @JoinColumn(name = "technology_id")
    private Technology technology;
}
