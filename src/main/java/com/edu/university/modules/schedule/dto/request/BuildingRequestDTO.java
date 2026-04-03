package com.edu.university.modules.schedule.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BuildingRequestDTO {
    @NotBlank(message = "Mã tòa nhà không được để trống")
    private String buildingCode;

    @NotBlank(message = "Tên tòa nhà không được để trống")
    private String buildingName;

    private String address;
    private Integer totalFloors;
    private String buildingType;
    private String description;
    private String note;
}
