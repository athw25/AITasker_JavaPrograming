package com.aitasker.commontest.mappertest;

import com.aitasker.common.mapper.GenericMapper;
import com.aitasker.user.entity.User;

public class UserMapper implements GenericMapper<User, UserDto> {

    @Override
    public UserDto toDto(User user) {
        return new UserDto(user.getName());
    }

    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setName(dto.getName());
        return user;
    }
}