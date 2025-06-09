package ra.edu.service.location;

import ra.edu.entity.Location;

import java.util.List;

public interface LocationService {
    List<Location> getAllLocations();
    boolean addLocation(Location location);
}
