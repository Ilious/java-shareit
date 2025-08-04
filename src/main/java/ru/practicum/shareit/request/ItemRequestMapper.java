package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestDto dto) {
        return ItemRequest.builder()
                .title(dto.title())
                .description(dto.description())
                .created(dto.created())
                .build();
    }

    public static ItemRequestDto toDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }
}
