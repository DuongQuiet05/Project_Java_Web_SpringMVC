package ra.edu.service.location;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.edu.entity.Location;
import ra.edu.repository.location.LocationRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService{
    private final LocationRepo locationRepo;

    @Override
    public List<Location> getAllLocations() {
        return locationRepo.findAll();
    }

    @Override
    public boolean addLocation(Location location) {
        return locationRepo.insert(location);
    }
}
