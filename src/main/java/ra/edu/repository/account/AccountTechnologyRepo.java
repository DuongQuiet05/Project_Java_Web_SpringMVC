package ra.edu.repository.account;


import ra.edu.entity.weeks.AccountTechnology;

import java.util.List;

public interface AccountTechnologyRepo {
    List<AccountTechnology> findByAccountIds(List<Integer> accountIds);

    boolean deleteByAccountId(int accountId);
    boolean insertAll(List<AccountTechnology> list);
}
