package ra.edu.dto.tech;

import lombok.Data;
import ra.edu.entity.Technology;

import java.util.List;

@Data
public class TechnologyListPageDTO {
    private List<Technology> technologies;
    private int currentPage;
    private int totalPages;
    private int size;
    private String keyword;
}
