package greencity.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsFilterDto {
    private String searchRequest;
    private String city;
    private Boolean hasMutualFriends = false;
}
