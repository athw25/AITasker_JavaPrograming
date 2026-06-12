package com.aitasker.mappertest;

import com.aitasker.user.entity.User;
import java.util.List;



public class TestMapper {

    public static void main(String[] args) {

        UserMapper mapper = new UserMapper();

        User user = new User();
        user.setName("Thu");

        UserDto dto = mapper.toDto(user);

        System.out.println(dto.getName());

        List<User> users = List.of(user);

        List<UserDto> dtos = mapper.toDtoList(users);

        System.out.println(dtos.get(0).getName());
        System.out.println(mapper.toDtoList(null));

        

        }
}