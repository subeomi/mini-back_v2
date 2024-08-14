package org.gbsb.duncool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO {
    private int tradeNum;
    private String itemId;
    private long itemPrice;
    private String tradeDate;
}
