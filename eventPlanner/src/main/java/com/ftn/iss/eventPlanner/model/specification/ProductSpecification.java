package com.ftn.iss.eventPlanner.model.specification;

import com.ftn.iss.eventPlanner.model.Product;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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

            Subquery<Double> subquery = query.subquery(Double.class);
            Root<Product> subRoot = subquery.from(Product.class);
            Join<Object, Object> ratingsJoin = subRoot.join("ratings");

            subquery.select(criteriaBuilder.avg(ratingsJoin.get("score")))
                    .where(criteriaBuilder.equal(subRoot.get("id"), root.get("id")));

            return criteriaBuilder.greaterThanOrEqualTo(subquery, minRating);
        };
    }







    public static Specification<Product> isAvailable(Boolean searchByAvailability) {
        return (root, query, criteriaBuilder) ->
                searchByAvailability != null && searchByAvailability ? criteriaBuilder.isTrue(root.get("currentDetails").get("isAvailable")) : criteriaBuilder.conjunction();
    }


}
