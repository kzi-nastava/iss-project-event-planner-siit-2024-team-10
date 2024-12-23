package com.ftn.iss.eventPlanner.model.specification;

import com.ftn.iss.eventPlanner.model.Product;
import com.ftn.iss.eventPlanner.model.Service;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name != null && !name.isEmpty()
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("currentDetails").get("name")), "%" + name.toLowerCase() + "%")
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
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("provider").get("location").get("city")), "%" + location.toLowerCase() + "%")
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Product> betweenPrices(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) {
                return criteriaBuilder.conjunction();
            }

            var currentDetails = root.join("currentDetails");

            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(currentDetails.get("price"), minPrice, maxPrice);
            }

            return minPrice != null
                    ? criteriaBuilder.greaterThanOrEqualTo(currentDetails.get("price"), minPrice)
                    : criteriaBuilder.lessThanOrEqualTo(currentDetails.get("price"), maxPrice);
        };
    }


    public static Specification<Product> minDiscount(Integer minDiscount) {
        return (root, query, criteriaBuilder) ->
                minDiscount != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("currentDetails").get("discount"), minDiscount) : criteriaBuilder.conjunction();
    }

    public static Specification<Product> minRating(Double minRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null) {
                return criteriaBuilder.conjunction();
            }

            var ratingsJoin = root.join("ratings");

            query.groupBy(root.get("id"));

            query.having(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.avg(ratingsJoin.get("score")), minRating));

            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Product> isAvailable(Boolean searchByAvailability) {
        return (root, query, criteriaBuilder) ->
                searchByAvailability != null && searchByAvailability ? criteriaBuilder.isTrue(root.get("currentDetails").get("isAvailable")) : criteriaBuilder.conjunction();
    }


}
