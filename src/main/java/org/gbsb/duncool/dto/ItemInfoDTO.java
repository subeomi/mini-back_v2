package org.gbsb.duncool.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // convert 시 DTO에 정의되지 않은 속성은 무시
public class ItemInfoDTO {
    private String itemId;
    private String itemName;
    private String itemType;
    private String itemTypeDetail;
    private String itemRarity;
    private int itemAvailableLevel;
    private int itemFame;
}
