package greencity.repository;

import greencity.entity.EcoNews;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.localization.TagTranslation;
import java.util.*;
import javax.persistence.Tuple;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

@Repository
public class EcoNewsSearchRepo {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    /**
     * Initialization constructor.
     */
    public EcoNewsSearchRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    /**
     * Method for search eco news by title,text,short info and tag name.
     *
     * @param pageable      {@link Pageable}.
     * @param searchingText text criteria for searching.
     * @param languageCode  code of needed language for finding tag.
     * @return all finding eco news, their tags and also count of finding eco news.
     */
    public Page<EcoNews> find(Pageable pageable, String searchingText, String languageCode) {
        CriteriaQuery<EcoNews> criteriaQuery =
            criteriaBuilder.createQuery(EcoNews.class);
        Root<EcoNews> root = criteriaQuery.from(EcoNews.class);
        searchingText = formatSearchingText(searchingText);

        Predicate predicate = getPredicate(criteriaQuery, searchingText, languageCode, root);

        List<Order> orderList = getOrderListFromPageable(pageable, root);

        criteriaQuery.select(root)
            .distinct(true)
            .where(predicate)
            .orderBy(orderList);

        TypedQuery<EcoNews> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
        typedQuery.setHint(QueryHints.PASS_DISTINCT_THROUGH, false);
        List<EcoNews> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, getEcoNewsCount(predicate));
    }

    private List<Order> getOrderListFromPageable(Pageable pageable, Root<EcoNews> root) {
        List<Order> orderList = new ArrayList<>();
        pageable.getSort()
            .get()
            .forEach(o -> {
                if (o.getProperty().equalsIgnoreCase("relevance")) {
                    orderList.add(criteriaBuilder.desc(criteriaBuilder.size(root.get("usersLikedNews"))));
                } else {
                    orderList.add(o.isAscending()
                        ? criteriaBuilder.asc(root.get(o.getProperty()))
                        : criteriaBuilder.desc(root.get(o.getProperty())));
                }
            });
        return orderList;
    }

    private long getEcoNewsCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<EcoNews> countEcoNewsRoot = countQuery.from(EcoNews.class);
        countQuery.select(criteriaBuilder.count(countEcoNewsRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> formEcoNewsLikePredicate(String searchingText, Root<EcoNews> root) {
        Expression<String> title = root.get("title").as(String.class);
        Expression<String> text = root.get("text").as(String.class);
        Expression<String> shortInfo = root.get("shortInfo").as(String.class);

        List<Predicate> predicateList = new ArrayList<>();
        Arrays.stream(searchingText.split(" ")).forEach(partOfSearchingText -> predicateList.add(
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(title), "%" + partOfSearchingText.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(text), "%" + partOfSearchingText.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(shortInfo),
                    "%" + partOfSearchingText.toLowerCase() + "%"))));
        return predicateList;
    }

    private Predicate formTagTranslationsPredicate(CriteriaQuery<EcoNews> criteriaQuery, String searchingText,
                                                   String languageCode, Root<EcoNews> root) {
        Subquery<Tag> tagSubquery = criteriaQuery.subquery(Tag.class);
        Root<Tag> tagRoot = tagSubquery.from(Tag.class);
        Join<EcoNews, Tag> ecoNewsTagJoin = tagRoot.join("ecoNews");

        Subquery<TagTranslation> tagTranslationSubquery = criteriaQuery.subquery(TagTranslation.class);
        Root<Tag> tagTranslationRoot = tagTranslationSubquery.correlate(tagRoot);

        Join<TagTranslation, Tag> tagTranslationTagJoin = tagTranslationRoot.join("tagTranslations");

        Predicate predicate = predicateForTags(searchingText, languageCode, tagTranslationTagJoin);
        tagTranslationSubquery.select(tagTranslationTagJoin.get("name"))
            .where(predicate);

        tagSubquery.select(ecoNewsTagJoin).where(criteriaBuilder.exists(tagTranslationSubquery));
        return criteriaBuilder.in(root.get("id")).value(tagSubquery);
    }

    private Predicate predicateForTags(String searchingText, String languageCode,
                                       Join<TagTranslation, Tag> tagTranslationTagJoin) {
        List<Predicate> predicateList = new ArrayList<>();
        Arrays.stream(searchingText.split(" ")).forEach(partOfSearchingText -> predicateList.add(criteriaBuilder.and(
            criteriaBuilder.like(criteriaBuilder.lower(tagTranslationTagJoin.get("name")),
                "%" + partOfSearchingText.toLowerCase() + "%"),
            criteriaBuilder.like(criteriaBuilder.lower(tagTranslationTagJoin.get("language").get("code")),
                "%" + languageCode.toLowerCase() + "%"))));
        return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }

    private Predicate getPredicate(CriteriaQuery<EcoNews> criteriaQuery, String searchingText,
                                   String languageCode, Root<EcoNews> root) {
        List<Predicate> predicateList = formEcoNewsLikePredicate(searchingText, root);
        predicateList.add(formTagTranslationsPredicate(criteriaQuery, searchingText, languageCode, root));
        return criteriaBuilder.or(predicateList.toArray(new Predicate[0]));
    }

    private String formatSearchingText(String criteria) {
        return criteria
            .trim()
            .replace("_", "\\_")
            .replace("%", "\\%")
            .replace("\\", "\\\\");
    }
}
