package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestDto dto, User user) {
        return ItemRequest.builder()
                .title(dto.title())
                .description(dto.description())
                .created(LocalDateTime.now())
                .requester(user)
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .created(request.getCreated())
                .requester(UserMapper.toDto(request.getRequester()))
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest request, List<ItemDto> itemDtos) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .created(request.getCreated())
                .requester(UserMapper.toDto(request.getRequester()))
                .items(itemDtos)
                .build();
    }
}
