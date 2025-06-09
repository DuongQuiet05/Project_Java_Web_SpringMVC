package ra.edu.service.position;



import ra.edu.dto.application.ApplyApplicationDTO;
import ra.edu.dto.position.CreatePositionDTO;
import ra.edu.dto.position.PositionDisplayDTO;
import ra.edu.dto.position.UpdatePositionDTO;
import ra.edu.dto.position.candidate.PositionDetailForCandidateDTO;
import ra.edu.dto.position.candidate.PositionListForCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.entity.Position;
import ra.edu.entity.Technology;
import ra.edu.entity.weeks.PositionTechnology;

import java.util.List;
import java.util.Optional;

    public interface PositionService {
        // todo : chung --------------------------------------------------------------------
        Optional<Position> findByIdAllStatus(int id);
        Optional<Position> findByNameAllStatus(String name);
        Optional<Position> findById(int id);

        // todo : convert -------------------------------------------------------------------
        List<PositionDisplayDTO> convertToDisplayList(List<Position> positions);
        Position convertCreateDtoToEntity(CreatePositionDTO dto);
        void convertUpdateDtoToEntity(UpdatePositionDTO dto, Position p);
        UpdatePositionDTO convertToUpdateDto(Position p);
        PositionListForCandidateDTO convertToCandidateDTO(Position p);
        PositionDetailForCandidateDTO convertToDetailForCandidateDTO(Position p);
        ApplyApplicationDTO convertToApplyDTO(Position position, Account candidate);

        // todo: admin -------------------------------------------------------------------------
        List<PositionDisplayDTO> getPaginate(int page, int size);
        int countAll();
        boolean create (CreatePositionDTO dto);
        boolean delete(int id);
        boolean update(UpdatePositionDTO dto);
        List<PositionDisplayDTO> search(String keyword, int page, int size);
        int countSearch(String keyword);

        // todo: candidate ----------------------------------------------------------------------
        List<PositionListForCandidateDTO> findAllActiveForCandidate(int page, int size);
        public List<PositionListForCandidateDTO> searchActiveForCandidate(String keyword, int page, int size);
        public int countAllByKeyword(String keyword);
        Optional<PositionDetailForCandidateDTO> getPositionDetail (int id);
        List<PositionListForCandidateDTO> getRecommendedPosition ();
        List<PositionListForCandidateDTO> getPositionForYou ();
        List<PositionListForCandidateDTO> searchActiveForCandidateByKeywordAndLocation(String keyword, String city, int page, int size);
        int countAllByKeywordAndLocation(String keyword, String city);
    }
