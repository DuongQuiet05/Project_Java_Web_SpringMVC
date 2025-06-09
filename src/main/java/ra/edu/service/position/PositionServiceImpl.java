package ra.edu.service.position;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import ra.edu.dto.application.ApplyApplicationDTO;
import ra.edu.dto.position.CreatePositionDTO;
import ra.edu.dto.position.PositionDisplayDTO;
import ra.edu.dto.position.UpdatePositionDTO;

import ra.edu.dto.position.candidate.PositionDetailForCandidateDTO;
import ra.edu.dto.position.candidate.PositionListForCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.Position;
import ra.edu.entity.Technology;

import ra.edu.entity.enums.application.ApplicationProgress;
import ra.edu.entity.enums.application.InterviewResult;
import ra.edu.entity.weeks.PositionTechnology;
import ra.edu.entity.weeks.ids.PositionTechnologyId;
import ra.edu.repository.application.ApplicationRepo;
import ra.edu.repository.position.PositionRepo;
import ra.edu.repository.position.PositionTechnologyRepo;

import ra.edu.service.tech.TechnologyService;
import ra.edu.utils.DateTimeUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {
    private final PositionRepo positionRepo;
    private final PositionTechnologyRepo positionTechnologyRepo;
    private final TechnologyService technologyService;
    private final DateTimeUtils dateTimeUtils;
    private final ApplicationRepo applicationRepo;

    @Override
    public Optional<Position> findByIdAllStatus(int id) {
        return positionRepo.findByIdAllStatus(id);
    }

    @Override
    public Optional<Position> findByNameAllStatus(String name) {
        return positionRepo.findByNameAllStatus(name);
    }

    @Override
    public Optional<Position> findById(int id) {
        return positionRepo.findById(id);
    }

    @Override
    public List<PositionDisplayDTO> getPaginate(int page, int size) {
        List<Position> list = positionRepo.getPaginate(page, size);
        return convertToDisplayList(list);
    }

    @Override
    public int countAll() {
        return positionRepo.countAll();
    }


    @Override
    public List<PositionDisplayDTO> search(String keyword, int page, int size) {
        List<Position> list = positionRepo.searchByKeyword(keyword, page, size);
        return convertToDisplayList(list);
    }

    @Override
    public int countSearch(String keyword) {
        return positionRepo.countByKeyword(keyword);
    }

    @Override
    public boolean create(CreatePositionDTO dto) {
        Position entity = convertCreateDtoToEntity(dto);

        boolean inserted = positionRepo.insert(entity);
        if (!inserted) return false;

        Optional<Position> recruitmentPositionOptional = positionRepo.getLastIndex();
        if (recruitmentPositionOptional.isEmpty()) return false;

        Position p = recruitmentPositionOptional.get();


        // Gán công nghệ
        if (dto.getTechnologyIds() != null && !dto.getTechnologyIds().isEmpty()) {
            List<PositionTechnology> links = dto.getTechnologyIds().stream()
                    .map(techId -> {
                        Technology tech = technologyService.findByIdAllStatus(techId).orElse(null);
                        if (tech == null) return null;

                        PositionTechnology link = new PositionTechnology();
                        link.setPosition(p); // đoạn này bị thế list ptech của p báo Unable to evaluate the expression Method threw 'org.hibernate.LazyInitializationException' exception.
                        link.setTechnology(tech);

                        PositionTechnologyId linkId = new PositionTechnologyId();
                        linkId.setPositionId(p.getId());
                        linkId.setTechnologyId(tech.getId());
                        link.setId(linkId);

                        return link; // dữ liệu đều ok chỉ có phần list của 2 đối tượng p và tech là như bên trên
                    })
                    .filter(Objects::nonNull)
                    .toList();

            try {
                positionTechnologyRepo.insertAll(links);
            } catch (Exception e) {
                System.err.println("Insert link failed: " + e.getMessage());
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean delete(int id) {
        return positionRepo.delete(id);
    }


    @Override
    public boolean update(UpdatePositionDTO dto) {
        Optional<Position> optional = positionRepo.findByIdAllStatus(dto.getId());
        if (optional.isEmpty()) {
            System.err.println("SERVICE-ERROR: Cannot find position to update");
            return false;
        }

        Position old = optional.get();

        convertUpdateDtoToEntity(dto, old);


        boolean deleted = positionTechnologyRepo.deleteByPositionId(dto.getId());
        if (!deleted) {
            System.err.println("SERVICE-ERROR: Failed to delete old tech links");
            return false;
        }

        if (dto.getTechnologyIds() != null && !dto.getTechnologyIds().isEmpty()) {
            List<PositionTechnology> newLinks = dto.getTechnologyIds().stream()
                    .filter(Objects::nonNull)
                    .map(techId -> {
                        Technology tech = technologyService.findByIdAllStatus(techId).orElse(null);
                        if (tech == null) return null;

                        PositionTechnology link = new PositionTechnology();
                        link.setPosition(old);
                        link.setTechnology(tech);

                        PositionTechnologyId id = new PositionTechnologyId();
                        id.setPositionId(old.getId());
                        id.setTechnologyId(tech.getId());
                        link.setId(id);

                        return link;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            boolean inserted = positionTechnologyRepo.insertAll(newLinks);
            if (!inserted) {
                System.err.println("SERVICE-ERROR: Failed to insert new tech links");
                return false;
            }
        }

        return positionRepo.update(old);
    }


    @Override
    public List<PositionDisplayDTO> convertToDisplayList(List<Position> positions) {
        List<Integer> ids = positions.stream().map(Position::getId).toList();

        Map<Integer, List<String>> techMap = positionTechnologyRepo.findByRecruitmentIds(ids).stream()
                .collect(Collectors.groupingBy(
                        pt -> pt.getPosition().getId(),
                        Collectors.mapping(pt -> pt.getTechnology().getName(), Collectors.toList())
                ));

        return positions.stream().map(p -> {
            PositionDisplayDTO dto = new PositionDisplayDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setDescription(p.getDescription());
            dto.setMinSalary(p.getMinSalary());
            dto.setMaxSalary(p.getMaxSalary());
            dto.setMinExperience(p.getMinExperience());
            dto.setExpiredDate(p.getExpiredDate());
            dto.setStatus(p.getStatus());
            dto.setCreatedAt(p.getCreatedAt());
            dto.setUpdatedAt(p.getUpdatedAt());
            dto.setTechnologies(techMap.getOrDefault(p.getId(), List.of()));
            return dto;
        }).toList();
    }

    @Override
    public Position convertCreateDtoToEntity(CreatePositionDTO dto) {
        Position p = new Position();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setMinSalary(dto.getMinSalary());
        p.setMaxSalary(dto.getMaxSalary());
        p.setMinExperience(dto.getMinExperience());
        p.setNumberOfVacancies(dto.getNumberOfVacancies());
        p.setField(dto.getField());
        p.setLocation(dto.getLocation());
        p.setFormOfWork(dto.getFormOfWork());
        p.setExpiredDate(dto.getExpiredDate());
        // hot default = false
        // status default = ENABLED

        // Xử lý công nghệ được chọn
        if (dto.getTechnologyIds() != null && !dto.getTechnologyIds().isEmpty()) {
            List<PositionTechnology> techLinks = dto.getTechnologyIds().stream()
                    .map(techId -> {
                        Technology tech = technologyService.findByIdAllStatus(techId).orElse(null);
                        if (tech == null) return null;
                        PositionTechnology link = new PositionTechnology();
                        link.setTechnology(tech);
                        link.setPosition(p); // gắn ngược để có khóa ngoại
                        return link;
                    })
                    .filter(Objects::nonNull)
                    .toList();
            p.setPositionTechnologies(techLinks);
        }

        return p;
    }

    @Override
    public ApplyApplicationDTO convertToApplyDTO(Position position, Account candidate) {
        ApplyApplicationDTO dto = new ApplyApplicationDTO();
        dto.setPositionId(position.getId());
        dto.setPositionName(position.getName());

        Optional<Application> latestAppOpt = applicationRepo.findLatestByCandidateAndPosition(candidate.getId(), position.getId());

        if (latestAppOpt.isPresent()) {
            Application app = latestAppOpt.get();
            ApplicationProgress progress = app.getProgress();

            boolean canApply = switch (progress) {
                case DONE, REJECTED, CANCELED -> true;
                default -> false;
            };

            dto.setCanApply(canApply);
        } else {
            dto.setCanApply(true);
        }

        return dto;
    }


    @Override
    public void convertUpdateDtoToEntity(UpdatePositionDTO dto, Position p) {
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setMinSalary(dto.getMinSalary());
        p.setMaxSalary(dto.getMaxSalary());
        p.setMinExperience(dto.getMinExperience());
        p.setNumberOfVacancies(dto.getNumberOfVacancies());
        p.setField(dto.getField());
        p.setLocation(dto.getLocation());
        p.setFormOfWork(dto.getFormOfWork());
        p.setExpiredDate(dto.getExpiredDate());
    }

    @Override
    public UpdatePositionDTO convertToUpdateDto(Position p) {
        UpdatePositionDTO dto = new UpdatePositionDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setMinSalary(p.getMinSalary());
        dto.setMaxSalary(p.getMaxSalary());
        dto.setMinExperience(p.getMinExperience());
        dto.setNumberOfVacancies(p.getNumberOfVacancies());
        dto.setField(p.getField());
        dto.setLocation(p.getLocation());
        dto.setFormOfWork(p.getFormOfWork());
        dto.setExpiredDate(p.getExpiredDate());

        // Lấy công nghệ
        List<Integer> techIds = positionTechnologyRepo.findByRecruitmentIds(List.of(p.getId()))
                .stream()
                .map(link -> link.getTechnology().getId())
                .toList();

        dto.setTechnologyIds(techIds);

        return dto;
    }



    @Override
    public List<PositionListForCandidateDTO> findAllActiveForCandidate(int page, int size) {
        List<Position> recruitmentPositions = positionRepo.findAllActiveForCandidate(page, size);

        return recruitmentPositions.stream()
                .map(p -> convertToCandidateDTO(p))
                .toList();
    }

    @Override
    public PositionListForCandidateDTO convertToCandidateDTO(Position p) {
        // Lấy danh sách tên công nghệ
        List<String> techNames = p.getPositionTechnologies()
                .stream()
                .map(link -> link.getTechnology().getName())
                .toList();

        // Convert createdAt → timeAgo
        String timeAgo = dateTimeUtils.getTimeAgo(p.getCreatedAt());

        return new PositionListForCandidateDTO(
                p.getId(),
                p.getName(),
                techNames,
                timeAgo
        );
    }

    @Override
    public List<PositionListForCandidateDTO> searchActiveForCandidate(String keyword, int page, int size) {
        List<Position> list = positionRepo.searchActiveForCandidateByKeyword(keyword, page, size);
        return list.stream()
                .map(p -> convertToCandidateDTO(p))
                .toList();
    }

    @Override
    public int countAllByKeyword(String keyword) {
        return positionRepo.countActiveForCandidateByKeyword(keyword);
    }

    @Override
    public PositionDetailForCandidateDTO convertToDetailForCandidateDTO(Position p) {
        PositionDetailForCandidateDTO dto = new PositionDetailForCandidateDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setMinSalary(p.getMinSalary());
        dto.setMaxSalary(p.getMaxSalary());
        dto.setMinExperience(p.getMinExperience());
        dto.setExpiredDate(p.getExpiredDate());
        dto.setNumberOfVacancies(p.getNumberOfVacancies());
        dto.setLocation(p.getLocation());
        dto.setFormOfWork(p.getFormOfWork());

        List<String> techNames = p.getPositionTechnologies()
                .stream()
                .map(link -> link.getTechnology().getName())
                .toList();
        dto.setTechNames(techNames);

        String timeAgo = dateTimeUtils.getTimeAgo(p.getCreatedAt());
        dto.setTimeAgo(timeAgo);

        return dto;
    }


    @Override
    public Optional<PositionDetailForCandidateDTO> getPositionDetail(int id) {
        return positionRepo.findByIdWithTechnologies(id)
                .map(p -> convertToDetailForCandidateDTO(p));
    }

    @Override
    public List<PositionListForCandidateDTO> getRecommendedPosition() {
        List<Position> recruitmentPositions = positionRepo.findSomeActiveForCandidate();

        return recruitmentPositions.stream()
                .map(p -> convertToCandidateDTO(p))
                .toList();
    }

    @Override
    public List<PositionListForCandidateDTO> getPositionForYou() {
        List<Position> recruitmentPositions = positionRepo.findPositionForYou();

        return recruitmentPositions.stream()
                .map(p -> convertToCandidateDTO(p))
                .toList();
    }

    @Override
    public List<PositionListForCandidateDTO> searchActiveForCandidateByKeywordAndLocation(String keyword, String city, int page, int size) {
        List<Position> recruitmentPositions = positionRepo.searchActiveForCandidateByKeywordAndLocation(keyword,city,page, size);

        return recruitmentPositions.stream()
                .map(p -> convertToCandidateDTO(p))
                .toList();
    }

    @Override
    public int countAllByKeywordAndLocation(String keyword, String city) {
        return positionRepo.countAllByKeywordAndLocation(keyword,city);
    }
}
