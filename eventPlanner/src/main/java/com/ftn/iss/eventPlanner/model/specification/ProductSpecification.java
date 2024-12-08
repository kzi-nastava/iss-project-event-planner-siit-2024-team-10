package com.ftn.iss.eventPlanner.model.specification;

import com.ftn.iss.eventPlanner.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name != null && !name.isEmpty()
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%")
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Product> hasEventTypeId(Integer eventTypeId) {
        return (root, query, criteriaBuilder) ->
                eventTypeId != null
                        ? criteriaBuilder.equal(root.get("eventType").get("id"), eventTypeId)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Product> hasCategoryId(Integer categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId != null
                        ? criteriaBuilder.equal(root.get("category").get("id"), categoryId)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Product> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                location != null && !location.isEmpty()
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("location").get("city")), "%" + location.toLowerCase() + "%")
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Product> betweenPrices(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return criteriaBuilder.conjunction();
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
            return minPrice != null
                    ? criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice)
                    : criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    public static Specification<Product> minDiscount(Integer minDiscount) {
        return (root, query, criteriaBuilder) ->
                minDiscount != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("discount"), minDiscount) : criteriaBuilder.conjunction();
    }

    public static Specification<Product> minRating(Double minRating) {
        return (root, query, criteriaBuilder) ->
                minRating != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating) : criteriaBuilder.conjunction();
    }
}
