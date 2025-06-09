package ra.edu.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ra.edu.dto.auth.LoginAccountDTO;
import ra.edu.dto.auth.RegisterAccountDTO;
import ra.edu.dto.candidate.*;
import ra.edu.entity.Account;
import ra.edu.entity.Technology;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Gender;
import ra.edu.entity.enums.account.Role;
import ra.edu.entity.weeks.AccountTechnology;
import ra.edu.entity.weeks.ids.AccountTechnologyId;
import ra.edu.repository.account.AccountRepo;
import ra.edu.repository.account.AccountTechnologyRepo;
import ra.edu.repository.tech.TechnologyRepo;
import ra.edu.service.application.ApplicationService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountRepo accountRepo;
    private final AccountTechnologyRepo accountTechnologyRepo;
    private final TechnologyRepo technologyRepo;

    @Override
    public boolean createAccount(RegisterAccountDTO dto) {
        try {
            Account acc = RegisterDTOConvertToAccount(dto);
            return accountRepo.insertAccount(acc);
        } catch (Exception e) {
            System.err.println("SERVICE-ERROR: " + e.getMessage());
            return false;
        }
    }


    @Override
    public boolean updateAccount(UpdateCandidateProfileDTO dto) {
        Optional<Account> optional = accountRepo.findByIdAllStatus(dto.getId());
        if (optional.isEmpty()) {
            System.err.println("SERVICE-ERROR: Cannot find account to update");
            return false;
        }

        Account old = optional.get();

        // Update các trường thông tin cơ bản
        if (!old.getName().equals(dto.getName())) old.setName(dto.getName());
        if (!old.getEmail().equals(dto.getEmail())) old.setEmail(dto.getEmail());
        if (old.getExperience() != dto.getExperience()) old.setExperience(dto.getExperience());
        if (old.getGender() != dto.getGender()) old.setGender(dto.getGender());
        if (!old.getDob().equals(dto.getDob())) old.setDob(dto.getDob());
        if (!old.getDescription().equals(dto.getDescription())) old.setDescription(dto.getDescription());

        // Xử lý avatar nếu có file mới
        if (dto.getFile() != null && !dto.getFile().isEmpty()) {
            old.setAvatar(dto.getAvatar());
        }

        // Xóa các liên kết công nghệ cũ
        boolean deleted = accountTechnologyRepo.deleteByAccountId(dto.getId());
        if (!deleted) {
            System.err.println("SERVICE-ERROR: Failed to delete old tech links");
            return false;
        }

        // Thêm liên kết công nghệ mới
        if (dto.getTechnologyIds() != null && !dto.getTechnologyIds().isEmpty()) {
            List<AccountTechnology> newLinks = dto.getTechnologyIds().stream()
                    .filter(Objects::nonNull)
                    .map(techId -> {
                        Technology tech = technologyRepo.findByIdAllStatus(techId).orElse(null);
                        if (tech == null) return null;

                        AccountTechnology link = new AccountTechnology();
                        link.setAccount(old);
                        link.setTechnology(tech);

                        AccountTechnologyId id = new AccountTechnologyId();
                        id.setCandidateId(old.getId());
                        id.setTechnologyId(tech.getId());
                        link.setId(id);

                        return link;
                    })
                    .filter(Objects::nonNull)
                    .toList();

            boolean inserted = accountTechnologyRepo.insertAll(newLinks);
            if (!inserted) {
                System.err.println("SERVICE-ERROR: Failed to insert new tech links");
                return false;
            }
        }

        return accountRepo.updateAccount(old);
    }


    @Override
    public Account RegisterDTOConvertToAccount(RegisterAccountDTO dto) {
        Account acc = new Account();
        acc.setName(dto.getName());
        acc.setEmail(dto.getEmail());

        String hashed = passwordEncoder.encode(dto.getPassword());
        acc.setPassword(hashed);

        acc.setPhone("not yet");
        acc.setExperience(0);
        acc.setGender(Gender.OTHER);
        acc.setRole(Role.CANDIDATE);
        acc.setStatus(AccountStatus.ENABLE);
        acc.setDescription("not yet");
        acc.setAvatar("default_avatar.jpg");
        acc.setDob(LocalDate.of(2000, 1, 1));

        return acc;
    }

    @Override
    public boolean login(LoginAccountDTO dto) {
        Optional<Account> accountOptional = accountRepo.findByEmailAllStatus(dto.getEmail());
        if (accountOptional.isEmpty()) return false;

        Account acc = accountOptional.get();
        String storedPassword = acc.getPassword();
        String rawInputPassword = dto.getPassword();

        // Nếu stored password là chuỗi băm hợp lệ của BCrypt thì dùng passwordEncoder
        if (storedPassword != null && storedPassword.startsWith("$2a$")) {
            return passwordEncoder.matches(rawInputPassword, storedPassword);
        }

        // Còn nếu chưa băm thì so sánh thẳng
        return rawInputPassword.equals(storedPassword);
    }


    @Override
    public Optional<Account> findByEmailAllStatus(String email) {
        email = email.trim();
        return accountRepo.findByEmailAllStatus(email);
    }

    @Override
    public Optional<Account> findByIdAllStatus(int id) {
        return accountRepo.findByIdAllStatus(id);
    }

    @Override
    public Optional<Account> findByIdWithTech(int id) {
        return accountRepo.findByIdWithTech(id);
    }


    @Override
    public boolean changePassword(ChangePasswordCandidateDTO dto) {
        Optional<Account> accOpt = accountRepo.findByIdAllStatus(dto.getAccountId());
        if (accOpt.isEmpty()){
            System.err.println("SERVICE-ERROR : không tìm thấy id để reset mật khẩu");
            return false;
        }

        String hashed = passwordEncoder.encode(dto.getNewPassword());
        dto.setNewPassword(hashed);
        if(!accountRepo.changePassword(dto)){
            return false;
        }
        return true;
    }


    @Override
    public DisplayProfileDTO convertToDisplayProfileDTO(Account account) {
        if (account == null) {
            return null;
        }
        DisplayProfileDTO dto = new DisplayProfileDTO();

        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setEmail(account.getEmail());
        dto.setPhone(account.getPhone());
        dto.setExperience(account.getExperience());
        dto.setGender(account.getGender());
        dto.setStatus(account.getStatus());
        dto.setDescription(account.getDescription());
        dto.setAvatar(account.getAvatar());
        dto.setDob(account.getDob());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());

        // Convert technologies to List<String> name
        if (account.getAccountTechnologies() != null) {
            List<String> techNames = account.getAccountTechnologies().stream()
                    .map(at -> at.getTechnology().getName())
                    .collect(Collectors.toList());
            dto.setTechnologies(techNames);
        }

        return dto;
    }

    @Override
    public UpdateCandidateProfileDTO convertToUpdateCandidateProfileDTO(Account account) {
        if (account == null) {
            return null;
        }

        UpdateCandidateProfileDTO dto = new UpdateCandidateProfileDTO();

        dto.setId(account.getId());
        dto.setName(account.getName());
        dto.setEmail(account.getEmail());
        dto.setExperience(account.getExperience());
        dto.setGender(account.getGender());
        dto.setDob(account.getDob());
        dto.setDescription(account.getDescription());
        dto.setAvatar(account.getAvatar());

        // Convert danh sách công nghệ sang list Integer id
        if (account.getAccountTechnologies() != null && !account.getAccountTechnologies().isEmpty()) {
            List<Integer> techIds = account.getAccountTechnologies().stream()
                    .map(at -> at.getTechnology().getId())
                    .collect(Collectors.toList());
            dto.setTechnologyIds(techIds);
        }

        return dto;
    }


    @Override
    public boolean changeStatus(int accountId, AccountStatus status) {
        return accountRepo.changeStatus(accountId, status);
    }


    @Override
    public List<CandidateDisplayDTO> getFilteredCandidates(CandidateFilterDTO dto, int page, int size) {
        String keyword = dto.getKeyword();
        Integer techId = dto.getTechId();
        Gender gender = dto.getGender();
        String ageRange = dto.getAgeRange();
        String expRange = dto.getExpRange();

        List<Account> accounts = accountRepo.findFilteredCandidates(keyword, techId, gender, ageRange, expRange, page, size);
        List<Integer> ids = accounts.stream().map(Account::getId).toList();

        List<AccountTechnology> list = accountTechnologyRepo.findByAccountIds(ids);

        Map<Integer, List<String>> techMap = list.stream()
                .collect(Collectors.groupingBy(
                        ct -> ct.getAccount().getId(),
                        Collectors.mapping(ct -> ct.getTechnology().getName(), Collectors.toList())
                ));

        return accounts.stream().map(acc -> {
            List<String> techList = techMap.getOrDefault(acc.getId(), new ArrayList<>());
            return new CandidateDisplayDTO(
                    acc.getId(),
                    acc.getName(),
                    acc.getEmail(),
                    acc.getPhone(),
                    acc.getExperience(),
                    acc.getGender(),
                    acc.getStatus(),
                    acc.getDob(),
                    acc.getCreatedAt(),
                    acc.getUpdatedAt(),
                    techList
            );
        }).toList();
    }


    @Override
    public int countFilteredCandidates(CandidateFilterDTO dto) {
        return accountRepo.countFilteredCandidates(
                dto.getKeyword(),
                dto.getTechId(),
                dto.getGender(),
                dto.getAgeRange(),
                dto.getExpRange()
        );
    }

}
