package org.gbsb.duncool.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class SingletonManager {

    private static final ObjectMapper om = new ObjectMapper();

    private SingletonManager() {
        // private 생성자를 통해 외부에서 인스턴스를 생성하지 못하게 합니다.
    }

    public static ObjectMapper getObjectMapper() {
        return om;
    }

    // 장비 부위 기준 배열
    public static final List<String> EQUIP_PARTS =
            List.of("무기", "칭호", "상의", "하의", "머리어깨", "하의", "신발", "벨트",
                    "목걸이", "팔찌", "반지", "보조장비", "마법석", "귀걸이");
}
