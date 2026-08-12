package com.account.persistence;

import com.account.domain.Transaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TransactionMapper {

    int insert(Transaction transaction);

    List<Transaction> findByAccountId(UUID accountId);
}
