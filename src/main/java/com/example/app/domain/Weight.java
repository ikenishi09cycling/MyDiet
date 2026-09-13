package com.example.app.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Weight {

    private Integer userId;

    @NotNull
    private LocalDate weightDt;

    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "1000", inclusive = false)
    @Digits(integer = 3, fraction = 1)
    private BigDecimal weight;

    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "1000", inclusive = false)
    @Digits(integer = 3, fraction = 1)
    private BigDecimal fat;

    @Size(max = 200)
    private String memo;

    private LocalDateTime createDt;

    private LocalDateTime updateDt;

}
