package com.msa_delivery.user.infrastructure.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DeliveryManager
 * {
 *     "status": 200,
 *     "message": "검색 조회가 완료되었습니다.",
 *     "data": {
 *         "content": [
 *             {
 *                 "deliveryMangerId": 4,
 *                 "slackId": "deliverySlack0",
 *                 "type": "HUB_DELIVERY_MANAGER",
 *                 "sequence": 2,
 *                 "createdBy": "master0",
 *                 "createdAt": "2024-12-16T18:42:00.697351",
 *                 "updatedAt": "2024-12-16T19:18:30.733477"
 *             }
 *         ],
 *         "pageable": {
 *             "pageNumber": 0,
 *             "pageSize": 20,
 *             "sort": [],
 *             "offset": 0,
 *             "paged": true,
 *             "unpaged": false
 *         },
 *         "last": true,
 *         "totalPages": 1,
 *         "totalElements": 1,
 *         "size": 20,
 *         "number": 0,
 *         "sort": [],
 *         "first": true,
 *         "numberOfElements": 1,
 *         "empty": false
 *     }
 * }
 */

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetUUIDDto {

//    @JsonProperty("hub_id")
//    private List<UUID> hubId;
//
//    @JsonProperty("delivery_id")
//    private List<UUID> deliveryId;
//
//    @JsonProperty("company_id")
//    private List<UUID> companyId;

    private List<UUIDListDto> content;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UUIDListDto {
        private Long deliveryManagerId;
    }
}
