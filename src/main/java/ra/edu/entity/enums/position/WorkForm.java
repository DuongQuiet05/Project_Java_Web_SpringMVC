package ra.edu.entity.enums.position;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum WorkForm {
    FULL_TIME("Full-time"),
    PART_TIME("Part-time"),
    REMOTE("Remote"),
    HYBRID("Hybrid");

    private final String displayName;

    WorkForm(String displayName) {
        this.displayName = displayName;
    }

}
