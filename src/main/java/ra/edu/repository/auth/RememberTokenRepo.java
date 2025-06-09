package ra.edu.repository.auth;

import ra.edu.entity.auth.RememberToken;

import java.util.Optional;

public interface RememberTokenRepo {
    boolean insert(RememberToken token);

    Optional<RememberToken> findByToken(String token);

    boolean deleteByToken(String token);
}
