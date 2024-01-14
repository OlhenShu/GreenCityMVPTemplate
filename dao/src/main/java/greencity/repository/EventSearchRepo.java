package greencity.repository;

import greencity.entity.Event;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class EventSearchRepo {
    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    /**
     * Initialization constructor.
     */
    public EventSearchRepo(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    /**
     * Method for search events by title, description and tag name.
     *
     * @param pageable - {@link Pageable}.
     * @param searchQuery - text criteria for searching.
     * @param languageCode  - code of needed language for finding tag.
     * @return all finding eco news, their tags and also count of finding eco news.
     */
    public Page<Event> find(Pageable pageable, String searchQuery, String languageCode) {
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        searchQuery = formatSearchingText(searchQuery);

        Predicate predicate = getPredicate(criteriaQuery, searchQuery, languageCode, root);

        List<Order> orderList = getOrderListFromPageable(pageable, root);

        criteriaQuery.select(root)
            .distinct(true)
            .where(predicate)
            .orderBy(orderList);

        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());
        typedQuery.setHint(QueryHints.PASS_DISTINCT_THROUGH, false);
        List<Event> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, getEcoNewsCount(predicate));
    }

    private List<Order> getOrderListFromPageable(Pageable pageable, Root<Event> root) {
        List<Order> orderList = new ArrayList<>();
        pageable.getSort()
            .get()
            .forEach(o -> {
                if (o.getProperty().equalsIgnoreCase("relevance")) {
                    //TODO : implement relevance sort
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
        Root<Event> countEcoNewsRoot = countQuery.from(Event.class);
        countQuery.select(criteriaBuilder.count(countEcoNewsRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private List<Predicate> formEcoNewsLikePredicate(String searchingText, Root<Event> root) {
        Expression<String> title = root.get("title").as(String.class);
        Expression<String> text = root.get("description").as(String.class);

        List<Predicate> predicateList = new ArrayList<>();
        Arrays.stream(searchingText.split(" ")).forEach(partOfSearchingText -> predicateList.add(
            criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(title), "%" + partOfSearchingText.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(text), "%" + partOfSearchingText.toLowerCase() + "%"))));
        return predicateList;
    }

    private Predicate formTagTranslationsPredicate(CriteriaQuery<Event> criteriaQuery, String searchingText,
                                                   String languageCode, Root<Event> root) {
        Subquery<Tag> tagSubquery = criteriaQuery.subquery(Tag.class);
        Root<Tag> tagRoot = tagSubquery.from(Tag.class);
        Join<Event, Tag> ecoNewsTagJoin = tagRoot.join("events");

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

    private Predicate getPredicate(CriteriaQuery<Event> criteriaQuery, String searchingText,
                                   String languageCode, Root<Event> root) {
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
