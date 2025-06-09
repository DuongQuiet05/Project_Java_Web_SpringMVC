package ra.edu.service.tech;

//import com.duong.projectrm.repository.account.CandidateTechnologyRepo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CandidateTechnologyServiceImpl implements CandidateTechnologyService {
//
//    private final CandidateTechnologyRepo candidateTechnologyRepo;
//
//    @Override
//    public Map<Integer, List<String>> findTechnologyNamesByAccountIds(List<Integer> accountIds) {
//        List<CandidateTechnology> list = candidateTechnologyRepo.findByAccountIds(accountIds);
//
//        return list.stream()
//                .collect(Collectors.groupingBy(
//                        ct -> ct.getAccount().getId(),
//                        Collectors.mapping(ct -> ct.getTechnology().getName(), Collectors.toList())
//                ));
//    }
//}
