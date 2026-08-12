package com.account.persistence;

import com.account.domain.OutboxEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface OutboxMapper {

    int insert(OutboxEvent event);

    List<OutboxEvent> findUnpublishedForUpdate(@Param("limit") int limit);

    int markPublished(@Param("id") UUID id);

    int incrementAttempts(@Param("id") UUID id);
}
