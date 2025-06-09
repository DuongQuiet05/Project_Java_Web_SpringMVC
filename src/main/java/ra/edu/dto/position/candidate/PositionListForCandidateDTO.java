package ra.edu.dto.position.candidate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PositionListForCandidateDTO {
    private int id;
    private String name;
    private List<String> techNames = new ArrayList<>();
    private String timeAgo;

}
