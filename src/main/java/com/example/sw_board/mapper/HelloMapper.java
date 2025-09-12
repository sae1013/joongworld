package com.example.sw_board.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface HelloMapper {
    @Select("SELECT 1")
    int ping();
}