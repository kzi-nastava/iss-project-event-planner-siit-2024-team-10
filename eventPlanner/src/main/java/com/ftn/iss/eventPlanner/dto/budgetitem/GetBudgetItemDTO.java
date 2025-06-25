package com.ftn.iss.eventPlanner.dto.budgetitem;

import com.ftn.iss.eventPlanner.dto.offeringcategory.GetOfferingCategoryDTO;
import com.ftn.iss.eventPlanner.dto.product.GetProductDTO;
import com.ftn.iss.eventPlanner.dto.service.GetServiceDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetBudgetItemDTO {
    private int id;
    private double amount;
    private List<GetServiceDTO> services;
    private List<GetProductDTO> products;
    private GetOfferingCategoryDTO Category;
    private boolean isDeleted;
}
