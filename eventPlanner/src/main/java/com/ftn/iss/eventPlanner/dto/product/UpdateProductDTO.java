package com.ftn.iss.eventPlanner.dto.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateProductDTO {
    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @Positive(message = "Price must be greater than 0")
    private double price;

    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot be more than 100")
    private double discount;

    private List<String> photos;

    @JsonProperty("isVisible")
    private boolean isVisible;

    @JsonProperty("isAvailable")
    private boolean isAvailable;

    public UpdateProductDTO() {}
}
