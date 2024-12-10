package com.msa_delivery.hub.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;


@Embeddable
public record LocationVO(@Column(name = "lat", nullable = true) double latitude,
                         @Column(name = "lng", nullable = true) double longitude) {

}
