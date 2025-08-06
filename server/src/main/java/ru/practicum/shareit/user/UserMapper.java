package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toEntity(UserDto dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .build();
    }

    public static UserDto toDto(User user) {
        if (Objects.isNull(user))
            return null;
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static void patchFields(User user, UserDto dto) {
        if (Objects.nonNull(dto.name()) && !dto.name().isBlank())
            user.setName(dto.name());

        if (Objects.nonNull(dto.email()) && !dto.email().isBlank()) {
            user.setEmail(dto.email());
        }
    }
}
