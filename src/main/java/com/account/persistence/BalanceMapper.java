package com.account.persistence;

import com.account.domain.Balance;
import com.account.domain.Currency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface BalanceMapper {

    int insert(Balance balance);

    int updateAmount(Balance balance);

    List<Balance> findByAccountId(UUID accountId);

    Optional<Balance> findByAccountIdAndCurrency(@Param("accountId") UUID accountId,
                                                 @Param("currency") Currency currency);

    Optional<Balance> findByAccountIdAndCurrencyForUpdate(@Param("accountId") UUID accountId,
                                                          @Param("currency") Currency currency);
}
