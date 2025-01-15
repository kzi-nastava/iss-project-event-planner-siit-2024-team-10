package com.ftn.iss.eventPlanner.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateProductDTO {
    private int categoryId;

    private String categoryProposalName;
    private String categoryProposalDescription;

    @NotNull(message = "Provider ID cannot be null.")
    private int providerID;

    @NotBlank(message = "Product name cannot be blank.")
    private String name;

    @NotBlank(message = "Product description cannot be blank.")
    private String description;

    @NotNull(message = "Price cannot be null.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0.")
    private double price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be greater than or equal to 0.")
    private double discount;

    private List<String> photos;

    private boolean isVisible;

    private boolean isAvailable;

    public CreateProductDTO() {}
}
