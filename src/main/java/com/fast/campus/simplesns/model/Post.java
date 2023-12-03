package com.fast.campus.simplesns.model;

import com.fast.campus.simplesns.model.entity.PostEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
public class Post {
    private Integer id = null;

    private String title;

    private String body;

    private User user;

    private Timestamp registeredAt;

    private Timestamp updatedAt;

    public static Post fromEntity(PostEntity entity) {
        return new Post(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                User.fromEntity(entity.getUser()),
                entity.getRegisteredAt(),
                entity.getUpdatedAt()
        );
    }
}
