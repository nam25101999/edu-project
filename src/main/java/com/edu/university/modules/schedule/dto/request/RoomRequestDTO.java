package com.edu.university.modules.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class RoomRequestDTO {
    @NotBlank(message = "Mã phòng không được để trống")
    private String roomCode;

    @NotBlank(message = "Tên phòng không được để trống")
    private String roomName;

    @NotNull(message = "ID tòa nhà không được để trống")
    private UUID buildingId;

    private Integer floor;
    private Integer capacity;
    private String roomType;
    private String status;
    private boolean hasProjector;
    private boolean hasAirConditioner;
    private boolean hasComputer;
    private String description;
}
