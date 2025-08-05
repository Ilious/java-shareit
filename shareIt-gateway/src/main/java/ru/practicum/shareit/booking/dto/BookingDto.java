package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.ValidateGroups;

import java.time.LocalDateTime;

@Builder
public record BookingDto(

        Long id,

        @NotNull(groups = ValidateGroups.OnCreate.class)
        @FutureOrPresent(groups = {ValidateGroups.OnCreate.class, ValidateGroups.OnPatch.class})
        LocalDateTime start,

        @NotNull(groups = ValidateGroups.OnCreate.class)
        @Future(groups = {ValidateGroups.OnCreate.class, ValidateGroups.OnPatch.class})
        LocalDateTime end,

        Long itemId,

        ItemDto item,

        UserDto booker,

        BookingState status
) {
}
