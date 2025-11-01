package com.softworks.joongworld.user.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    int insertUser(@Param("email") String email,
                   @Param("passwordHash") String passwordHash,
                   @Param("name") String name,
                   @Param("nickname") String nickname);

    Long findIdByEmail(@Param("email") String email);
}
