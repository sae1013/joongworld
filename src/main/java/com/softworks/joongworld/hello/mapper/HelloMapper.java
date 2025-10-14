package com.softworks.joongworld.hello.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HelloMapper {
    @Select("SELECT 1")
    int ping();
}