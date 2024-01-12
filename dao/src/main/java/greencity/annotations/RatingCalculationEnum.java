package greencity.annotations;

import lombok.Getter;

@Getter
public enum RatingCalculationEnum {
    ADD_COMMENT(2),
    DELETE_COMMENT(-2),
    LIKE_COMMENT(1),
    UNLIKE_COMMENT(-1),
    ADD_ECO_NEWS(20),
    DELETE_ECO_NEWS(-20),
    ADD_EVENT(10),
    DELETE_EVENT(-10),
    SHARE_ECO_NEWS(10),
    SHARE_EVENT(10),
    DAY_OF_ECO_HABIT(1);

    private final float ratingPoints;

    RatingCalculationEnum(float ratingPoints) {
        this.ratingPoints = ratingPoints;
    }
}
