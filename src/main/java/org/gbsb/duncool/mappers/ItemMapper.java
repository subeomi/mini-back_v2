package org.gbsb.duncool.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.gbsb.duncool.dto.ItemInfoDTO;
import org.gbsb.duncool.dto.ItemReinforceDTO;
import org.gbsb.duncool.dto.PriceInfoDTO;
import org.gbsb.duncool.dto.TradeDTO;

import java.util.List;

@Mapper
public interface ItemMapper {

    int insertItemInfo(ItemInfoDTO dto);

    ItemInfoDTO getOneItem(String itemId);

    int insertPriceInfo(PriceInfoDTO dto);

    PriceInfoDTO getOnePrice(String itemId);

    int insertTradeHistory(TradeDTO dto);

    List<TradeDTO> getRecentFiveTrades(String itemId);

    TradeDTO getRecentOneTrade(String itemId);

    int insertReinforceSkill(ItemReinforceDTO dto);

    ItemReinforceDTO getReinforceSkill(String itemId);
}
