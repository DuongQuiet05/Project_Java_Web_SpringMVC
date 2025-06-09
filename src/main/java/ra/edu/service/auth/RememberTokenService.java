package ra.edu.service.auth;


import ra.edu.dto.auth.TokenWithExpiryDTO;
import ra.edu.entity.Account;

import java.util.Optional;

public interface RememberTokenService {
    Optional<TokenWithExpiryDTO> stayLoggedIn(Account account);

    Optional<Account> findAccountByToken(String token);

    boolean deleteTokenOnLogout(String token);
}
