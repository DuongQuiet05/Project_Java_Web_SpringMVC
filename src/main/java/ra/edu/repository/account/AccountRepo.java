package ra.edu.repository.account;

import ra.edu.dto.candidate.ChangePasswordCandidateDTO;
import ra.edu.entity.Account;
import ra.edu.entity.enums.account.AccountStatus;
import ra.edu.entity.enums.account.Gender;

import java.util.List;
import java.util.Optional;

public interface AccountRepo {
    boolean insertAccount (Account account);
    Optional<Account> findByIdAllStatus(int id);
    Optional<Account> findByEmailAllStatus(String email);

    //todo: profile ---------------------------------------------------------------
    Optional<Account> findByIdWithTech(int id);
    boolean updateAccount(Account account);
    boolean changePassword (ChangePasswordCandidateDTO dto);

    // todo: admin -----------------------------------------------------------------
    List<Account> getPaginateAccounts(int page, int size);

    boolean changeStatus(int accountId, AccountStatus status);


    List<Account> findByNameKeyword(String keyword, int page, int size);

    int countAllStatus();

    int countAllStatusForSearch(String keyword);


    List<Account> findFilteredCandidates(String keyword, Integer techId, Gender gender, String ageRange, String expRange, int page, int size);

    int countFilteredCandidates(String keyword, Integer techId, Gender gender, String ageRange, String expRange);


}
