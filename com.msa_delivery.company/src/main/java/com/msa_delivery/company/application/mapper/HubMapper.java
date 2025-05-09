package com.msa_delivery.company.application.mapper;

import java.util.HashMap;
import java.util.Map;

public class HubMapper {

    private static final Map<String, String> HUB_MAP = new HashMap<>();
    private static final Map<String, String> GYEONGGI_MAPPING = new HashMap<>();

    static {
        // 주요 허브 매핑
        HUB_MAP.put("서울특별시", "서울특별시 센터");
        HUB_MAP.put("부산광역시", "부산광역시 센터");
        HUB_MAP.put("대구광역시", "대구광역시 센터");
        HUB_MAP.put("인천광역시", "인천광역시 센터");
        HUB_MAP.put("광주광역시", "광주광역시 센터");
        HUB_MAP.put("대전광역시", "대전광역시 센터");
        HUB_MAP.put("울산광역시", "울산광역시 센터");
        HUB_MAP.put("세종특별자치시", "세종특별자치시 센터");
        HUB_MAP.put("강원특별자치도", "강원특별자치도 센터");
        HUB_MAP.put("충청북도", "충청북도 센터");
        HUB_MAP.put("충청남도", "충청남도 센터");
        HUB_MAP.put("전북특별자치도", "전북특별자치도 센터");
        HUB_MAP.put("전라남도", "전라남도 센터");
        HUB_MAP.put("경상북도", "경상북도 센터");
        HUB_MAP.put("경상남도", "경상남도 센터");

        // 경기도 북부/남부 매핑
        GYEONGGI_MAPPING.put("고양시", "경기 북부 센터");
        GYEONGGI_MAPPING.put("의정부시", "경기 북부 센터");
        GYEONGGI_MAPPING.put("양주시", "경기 북부 센터");
        GYEONGGI_MAPPING.put("파주시", "경기 북부 센터");
        GYEONGGI_MAPPING.put("연천군", "경기 북부 센터");
        GYEONGGI_MAPPING.put("동두천시", "경기 북부 센터");
        GYEONGGI_MAPPING.put("남양주시", "경기 북부 센터");
        GYEONGGI_MAPPING.put("가평군", "경기 북부 센터");

        GYEONGGI_MAPPING.put("성남시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("수원시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("용인시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("평택시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("화성시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("안성시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("오산시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("안산시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("광주시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("시흥시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("군포시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("의왕시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("안양시", "경기 남부 센터");
        GYEONGGI_MAPPING.put("이천시", "경기 남부 센터");
    }

    /**
     * 입력된 주소를 기반으로 허브 이름을 반환.
     *
     * @param address 입력 주소
     * @return 허브 이름
     */
    public static String getHubByAddress(String address) {
        // 경기도 내부 주소 구분
        if (address.contains("경기도")) {
            for (Map.Entry<String, String> entry : GYEONGGI_MAPPING.entrySet()) {
                if (address.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        // 일반 주소 매핑
        for (Map.Entry<String, String> entry : HUB_MAP.entrySet()) {
            if (address.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("허브를 찾을 수 없습니다: " + address);
    }
}
