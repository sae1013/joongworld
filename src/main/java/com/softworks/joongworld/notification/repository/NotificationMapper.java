package com.softworks.joongworld.notification.repository;

import com.softworks.joongworld.notification.model.NotificationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    int insert(NotificationEntity notification);

    List<NotificationEntity> findRecentByRecipient(@Param("recipientId") Long recipientId,
                                                   @Param("limit") int limit);

    int countUnread(@Param("recipientId") Long recipientId);

    int markAsRead(@Param("notificationId") Long notificationId,
                   @Param("recipientId") Long recipientId);

    int markAllAsRead(@Param("recipientId") Long recipientId);
}
