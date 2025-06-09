package ra.edu.repository.location;

import ra.edu.entity.Location;

import java.util.List;

public interface LocationRepo {
    boolean insert(Location location);
    List<Location> findAll();
}
