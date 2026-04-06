package com.edu.university.modules.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class TimeSlotRequestDTO {
    @NotBlank(message = "Mã ca học không được để trống")
    private String slotCode;

    @NotBlank(message = "Tên ca học không được để trống")
    private String slotName;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime startTime;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime endTime;
}
