package com.url.shortner.dtos;

import com.url.shortner.models.UrlMapping;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClickEventDto {
    private LocalDate clickDate;
    private Long count;
}
