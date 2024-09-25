package org.gbsb.duncool.service.data;

import lombok.RequiredArgsConstructor;
import org.gbsb.duncool.dto.*;
import org.gbsb.duncool.mappers.ItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemDataService {

    private final ItemMapper itemMapper;

    @Transactional
    public void insertItemInfoData(ItemInfoDTO dto) {
        itemMapper.insertItemInfo(dto);
    }

    @Transactional
    public void insertPriceInfoData(PriceInfoDTO dto) {
        itemMapper.insertPriceInfo(dto);
    }

    @Transactional
    public void insertTradeHistoryData(TradeDTO dto) {
        itemMapper.insertTradeHistory(dto);
    }

    @Transactional
    public void insertReinforceSkillData(ItemReinforceDTO dto) {
        itemMapper.insertReinforceSkill(dto);
    }

}
