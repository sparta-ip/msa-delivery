package com.msa_delivery.hub.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Location {
    @Column(name = "lat", nullable = true)
    private double latitude;
    @Column(name = "lng", nullable = true)
    private double longitude;


}
