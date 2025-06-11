package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item toEntity(ItemDto dto) {
        return Item.builder()
                .name(dto.name())
                .description(dto.description())
                .isAvailable(dto.isAvailable())
                .build();
    }

    public static ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .build();
    }

    public static void patchFields(Item actual, ItemDto dto) {
        if (Objects.nonNull(dto.name()) && !dto.name().isBlank())
            actual.setName(dto.name());

        if (Objects.nonNull(dto.description()) && !dto.description().isBlank())
            actual.setDescription(dto.description());

        if (Objects.nonNull(dto.isAvailable()))
            actual.setIsAvailable(dto.isAvailable());
    }
}
