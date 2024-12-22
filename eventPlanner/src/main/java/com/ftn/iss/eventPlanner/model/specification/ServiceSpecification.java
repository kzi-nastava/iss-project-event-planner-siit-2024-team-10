package com.ftn.iss.eventPlanner.model.specification;

import org.springframework.data.jpa.domain.Specification;
import com.ftn.iss.eventPlanner.model.Service;
import java.time.LocalDate;

public class ServiceSpecification {

    public static Specification<Service> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                name != null && !name.isEmpty()
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("currentDetails").get("name")), "%" + name.toLowerCase() + "%")
                        : criteriaBuilder.conjunction();
    }


    public static Specification<Service> hasCategoryId(Integer categoryId) {
        return (root, query, criteriaBuilder) ->
                categoryId != null
                        ? criteriaBuilder.equal(root.get("currentDetails").get("category").get("id"), categoryId)
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Service> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                location != null && !location.isEmpty()
                        ? criteriaBuilder.like(criteriaBuilder.lower(root.get("currentDetails").get("location").get("city")), "%" + location.toLowerCase() + "%")
                        : criteriaBuilder.conjunction();
    }

    public static Specification<Service> betweenPrices(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return criteriaBuilder.conjunction();
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            }
            return minPrice != null
                    ? criteriaBuilder.greaterThanOrEqualTo(root.get("currentDetails").get("price"), minPrice)
                    : criteriaBuilder.lessThanOrEqualTo(root.get("currentDetails").get("price"), maxPrice);
        };
    }

    public static Specification<Service> minDiscount(Integer minDiscount) {
        return (root, query, criteriaBuilder) ->
                minDiscount != null ? criteriaBuilder.greaterThanOrEqualTo(root.get("currentDetails").get("discount"), minDiscount) : criteriaBuilder.conjunction();
    }

    public static Specification<Service> minRating(Double minRating) {
        return (root, query, criteriaBuilder) -> {
            if (minRating == null) {
                return criteriaBuilder.conjunction();
            }

            var ratingsJoin = root.join("currentDetails").join("ratings");

            var avgRating = criteriaBuilder.avg(ratingsJoin.get("score"));
            return criteriaBuilder.greaterThanOrEqualTo(avgRating, minRating);
        };
    }


    public static Specification<Service> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) return criteriaBuilder.conjunction();
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("serviceDate"), startDate, endDate);
            }
            return startDate != null
                    ? criteriaBuilder.greaterThanOrEqualTo(root.get("serviceDate"), startDate)
                    : criteriaBuilder.lessThanOrEqualTo(root.get("serviceDate"), endDate);
        };
    }

    public static Specification<Service> hasServiceDuration(Integer serviceDuration) {
        return (root, query, criteriaBuilder) ->
                serviceDuration != null ? criteriaBuilder.equal(root.get("currentDetails").get("duration"), serviceDuration) : criteriaBuilder.conjunction();
    }

    public static Specification<Service> isAvailable(Boolean searchByAvailability) {
        return (root, query, criteriaBuilder) ->
                searchByAvailability != null && searchByAvailability ? criteriaBuilder.isTrue(root.get("currentDetails").get("isAvailable")) : criteriaBuilder.conjunction();
    }
}