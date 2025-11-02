package com.softworks.joongworld.user.repository;

import com.softworks.joongworld.user.dto.UserAuth;
import com.softworks.joongworld.user.dto.UserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    boolean existsByEmail(@Param("email") String email);

    boolean existsByNickname(@Param("nickname") String nickname);

    int insertUser(@Param("email") String email,
                   @Param("passwordHash") String passwordHash,
                   @Param("name") String name,
                   @Param("nickname") String nickname,
                   @Param("isAdmin") boolean isAdmin);

    Long findIdByEmail(@Param("email") String email);

    UserResponse findById(@Param("id") Long id);

    UserResponse findByEmail(@Param("email") String email);

    UserResponse findByNickname(@Param("nickname") String nickname);

    UserAuth findAuthByEmail(@Param("email") String email);
}
