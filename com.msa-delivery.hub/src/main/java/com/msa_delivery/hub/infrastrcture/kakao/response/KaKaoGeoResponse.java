package com.msa_delivery.hub.infrastrcture.kakao.response;

import com.msa_delivery.hub.domain.model.Location;
import lombok.Getter;

import java.util.List;

@Getter
public class KaKaoGeoResponse {
    private List<Document> documents;

    @Getter
    public static class Document {
        private String hubAddress;
        private double y;
        private double x;

        public Location toLocation() {
            return new Location(this.x, this.y);
        }
    }
}
