package com.softworks.joongworld.notification.repository;

import com.softworks.joongworld.notification.model.NotificationOutboxEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationOutboxMapper {

    int insert(NotificationOutboxEntity outbox);

    List<NotificationOutboxEntity> findPending(@Param("limit") int limit);

    int markPublished(@Param("id") Long id);

    int markDelivered(@Param("id") Long id);

    int markFailed(@Param("id") Long id, @Param("message") String message);
}
