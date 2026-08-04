package com.project.urlshortener.specification;

import com.project.urlshortener.entity.ShortUrl;
import com.project.urlshortener.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Locale;

public final class ShortUrlSpecifications {

    private ShortUrlSpecifications() {
    }

    public static Specification<ShortUrl> createdBy(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdBy"), user);
    }

    public static Specification<ShortUrl> searchText(String queryText) {
        if (queryText == null || queryText.isBlank()) {
            return null;
        }

        String likeValue = "%" + queryText.trim().toLowerCase(Locale.ROOT) + "%";
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("originalUrl")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("shortCode")), likeValue),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("customAlias")), likeValue)
        );
    }

    public static Specification<ShortUrl> enabled(Boolean enabled) {
        if (enabled == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("enabled"), enabled);
    }

    public static Specification<ShortUrl> expired(Boolean expired) {
        if (expired == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            Instant now = Instant.now();
            if (expired) {
                return criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("expiresAt")),
                        criteriaBuilder.lessThan(root.<Instant>get("expiresAt"), now)
                );
            }
            return criteriaBuilder.or(
                    criteriaBuilder.isNull(root.get("expiresAt")),
                    criteriaBuilder.greaterThanOrEqualTo(root.<Instant>get("expiresAt"), now)
            );
        };
    }

    public static Specification<ShortUrl> hasCustomAlias(Boolean hasCustomAlias) {
        if (hasCustomAlias == null) {
            return null;
        }
        return (root, query, criteriaBuilder) -> hasCustomAlias
                ? criteriaBuilder.isNotNull(root.get("customAlias"))
                : criteriaBuilder.isNull(root.get("customAlias"));
    }

    public static Specification<ShortUrl> ownerView(User user, String queryText, Boolean enabled, Boolean expired, Boolean hasCustomAlias) {
        Specification<ShortUrl> specification = Specification.where(createdBy(user));
        Specification<ShortUrl> searchSpecification = searchText(queryText);
        Specification<ShortUrl> enabledSpecification = enabled(enabled);
        Specification<ShortUrl> expiredSpecification = expired(expired);
        Specification<ShortUrl> aliasSpecification = hasCustomAlias(hasCustomAlias);

        if (searchSpecification != null) {
            specification = specification.and(searchSpecification);
        }
        if (enabledSpecification != null) {
            specification = specification.and(enabledSpecification);
        }
        if (expiredSpecification != null) {
            specification = specification.and(expiredSpecification);
        }
        if (aliasSpecification != null) {
            specification = specification.and(aliasSpecification);
        }
        return specification;
    }
}
