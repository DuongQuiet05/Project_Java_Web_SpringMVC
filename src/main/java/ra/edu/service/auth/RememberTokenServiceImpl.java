package ra.edu.service.auth;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.edu.dto.auth.TokenWithExpiryDTO;
import ra.edu.entity.Account;
import ra.edu.entity.auth.RememberToken;
import ra.edu.repository.auth.RememberTokenRepo;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RememberTokenServiceImpl implements RememberTokenService {

    private final RememberTokenRepo rememberTokenRepo;

    @Override
    public Optional<TokenWithExpiryDTO> stayLoggedIn(Account account) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusDays(3);

        RememberToken rememberToken = new RememberToken();
        rememberToken.setToken(token);
        rememberToken.setExpiryTime(expiry);
        rememberToken.setAccount(account);

        if(rememberTokenRepo.insert(rememberToken)){
            return Optional.of(new TokenWithExpiryDTO(token,expiry));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Account> findAccountByToken(String token) {
        return rememberTokenRepo.findByToken(token)
                .filter(rt -> rt.getExpiryTime().isAfter(LocalDateTime.now())) // kiểm tra token chưa hết hạn
                .map(RememberToken::getAccount);
    }

    @Override
    public boolean deleteTokenOnLogout(String token) {
        return rememberTokenRepo.deleteByToken(token);
    }
}
