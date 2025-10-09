package com.notification.Notification.mapper;

import com.notification.Notification.dto.InternalNotificationDTO;
import com.notification.Notification.dto.NotificationDeliveryDTO;
import com.notification.Notification.dto.NotificationTxDTO;
import com.notification.Notification.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "senderId", target = "senderId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "body", target = "body")
    @Mapping(source = "resourceId", target = "resourceId")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "level", target = "level")
    @Mapping(target = "delivered", constant = "false")
    @Mapping(target = "read", constant = "false")
    @Mapping(target = "retryCount", constant = "3")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "actionUrl", source = "actionUrl")
    @Mapping(target = "sendDate", ignore = true)
    @Mapping(source = "translationArgs", target = "translationArgs")
    Notification toEntity(InternalNotificationDTO dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "senderId", target = "senderId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "body", target = "body")
    @Mapping(source = "resourceId", target = "resourceId")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "level", target = "level")
    @Mapping(source = "delivered", target = "delivered")
    @Mapping(source = "read", target = "read")
    @Mapping(source = "sendDate", target = "sendDate")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(target = "actionUrl", source = "actionUrl")
    @Mapping(source = "translationArgs", target = "translationArgs")
    NotificationDeliveryDTO toDto(Notification entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "senderId", target = "senderId")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "body", target = "body")
    @Mapping(source = "resourceId", target = "resourceId")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "level", target = "level")
    @Mapping(source = "delivered", target = "delivered")
    @Mapping(source = "read", target = "read")
    @Mapping(source = "sendDate", target = "sendDate")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "translationArgs", target = "translationArgs")
    NotificationTxDTO toTxDTO(Notification entity);
}
