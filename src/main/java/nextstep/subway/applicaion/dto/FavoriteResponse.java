package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Favorite;

import java.util.List;
import java.util.stream.Collectors;

public class FavoriteResponse {
    private Long id;
    private StationResponse source;
    private StationResponse target;

    private FavoriteResponse() {
    }

    private FavoriteResponse(Long id, StationResponse source, StationResponse target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }

    public static FavoriteResponse of(Long id, StationResponse source, StationResponse target) {
        return new FavoriteResponse(id, source, target);
    }

    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(favorite.getId(), StationResponse.from(favorite.getSource()),
            StationResponse.from(favorite.getTarget()));
    }

    public static List<FavoriteResponse> ofList(List<Favorite> favoriteList) {
        return favoriteList.stream().map(FavoriteResponse::from).collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public StationResponse getSource() {
        return source;
    }

    public StationResponse getTarget() {
        return target;
    }
}