package greencity.mapping;

import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.entity.EcoNewsComment;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static greencity.ModelUtils.getAddEcoNewsCommentDtoRequest;
import static greencity.ModelUtils.getEcoNewsComment;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsMapperTest {

    private final EcoNewsComment ecoNewsComment = getEcoNewsComment();

    @Test
    void map() {

        AddEcoNewsCommentDtoRequest expected = getAddEcoNewsCommentDtoRequest();
        AddEcoNewsCommentDtoRequest actual = UtilsMapper.map(ecoNewsComment, AddEcoNewsCommentDtoRequest.class);

        assertEquals(expected.getText(), actual.getText());
    }

    @Test
    void mapAllToList() {

        List<EcoNewsComment> ecoNewsCommentList = getEcoNewsCommentList();
        List<AddEcoNewsCommentDtoRequest> expected = getAddEcoNewsCommentDtoRequestList();
        List<AddEcoNewsCommentDtoRequest> actual = UtilsMapper.mapAllToList(ecoNewsCommentList, AddEcoNewsCommentDtoRequest.class);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected.get(0).getText(), actual.get(0).getText());
    }

    @Test
    void mapAllToSet() {

        List<EcoNewsComment> ecoNewsCommentList = getEcoNewsCommentList();
        List<AddEcoNewsCommentDtoRequest> expected = getAddEcoNewsCommentDtoRequestList();
        Set<AddEcoNewsCommentDtoRequest> actual = UtilsMapper.mapAllToSet(ecoNewsCommentList, AddEcoNewsCommentDtoRequest.class);

        assertEquals(1, actual.size());
        assertEquals(actual.stream()
                        .map(AddEcoNewsCommentDtoRequest::getText)
                        .collect(Collectors.joining()),
                expected.get(0).getText());
    }

    @NotNull
    private static List<AddEcoNewsCommentDtoRequest> getAddEcoNewsCommentDtoRequestList() {
        return List.of(getAddEcoNewsCommentDtoRequest(), getAddEcoNewsCommentDtoRequest());
    }

    @NotNull
    private List<EcoNewsComment> getEcoNewsCommentList() {
        return List.of(ecoNewsComment, ecoNewsComment);
    }
}