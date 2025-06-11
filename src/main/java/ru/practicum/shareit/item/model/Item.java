package ru.practicum.shareit.item.model;

import lombok.*;

import java.time.LocalDate;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;

    private String name;

    private String description;

    private Boolean isAvailable;

    private LocalDate startLock; // not longer than a day
}
