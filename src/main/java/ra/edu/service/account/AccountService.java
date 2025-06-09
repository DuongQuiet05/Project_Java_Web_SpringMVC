package ra.edu.service.account;

import ra.edu.dto.auth.LoginAccountDTO;
import ra.edu.dto.auth.RegisterAccountDTO;
import ra.edu.dto.candidate.*;
import ra.edu.entity.Account;
import ra.edu.entity.Application;
import ra.edu.entity.enums.account.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    // todo: auth ------------------------------------------------------
    boolean createAccount (RegisterAccountDTO dto);
    Account RegisterDTOConvertToAccount(RegisterAccountDTO dto);
    boolean login(LoginAccountDTO dto);


    // todo:find and filter -------------------------------------------------------------
    Optional<Account> findByEmailAllStatus(String email);
    Optional<Account> findByIdAllStatus(int id);

    //todo:profile --------------------------------------------------------------------
    Optional<Account> findByIdWithTech(int id);
    boolean updateAccount(UpdateCandidateProfileDTO dto);
    boolean changePassword (ChangePasswordCandidateDTO dto);

    //todo: admin ------------------------------------------------------------------------
    List<CandidateDisplayDTO> getFilteredCandidates(CandidateFilterDTO dto, int page, int size);

    int countFilteredCandidates(CandidateFilterDTO dto);

    boolean changeStatus(int accountId, AccountStatus status);

    //todo:convert ------------------------------------------------------------------------
    DisplayProfileDTO convertToDisplayProfileDTO(Account account);
    UpdateCandidateProfileDTO convertToUpdateCandidateProfileDTO(Account account);
}
