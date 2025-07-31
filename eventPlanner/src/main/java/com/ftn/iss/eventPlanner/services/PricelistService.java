package com.ftn.iss.eventPlanner.services;

import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatePricelistItemDTO;
import com.ftn.iss.eventPlanner.dto.pricelistitem.UpdatedPricelistItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class PricelistService {
    @Autowired
    private ServiceService serviceService;
    @Autowired
    private ProductService productService;
    public UpdatedPricelistItemDTO updatePricing(int id, UpdatePricelistItemDTO dto) {
        try {
            return serviceService.updatePrice(id, dto);
        } catch (IllegalArgumentException e) {
            return productService.updatePrice(id, dto);
        }
    }
}
