package com.msa_delivery.hub.infrastrcture.kakao.dto;

import com.msa_delivery.hub.domain.model.LocationVO;
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

        public LocationVO toLocation() {
            return new LocationVO(this.y, this.x);
        }
    }
}
