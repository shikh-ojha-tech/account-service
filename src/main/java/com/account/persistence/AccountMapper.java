package com.account.persistence;

import com.account.domain.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface AccountMapper {

    int insert(Account account);

    Optional<Account> findById(UUID accountId);
}
