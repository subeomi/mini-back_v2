package org.gbsb.duncool.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.gbsb.duncool.dto.CharAllDTO;
import org.gbsb.duncool.mappers.InfoMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class InfoDataService {

    private final InfoMapper infoMapper;

    public void saveCharInfoData(CharAllDTO dto, CharAllDTO setDto){

        if (dto == null) {
            log.info("dto null");
            infoMapper.insertCharInfo(setDto);
            infoMapper.insertData(setDto);
            infoMapper.insertEquip(setDto);
            infoMapper.insertAvatar(setDto);
            infoMapper.insertCreature(setDto);
            infoMapper.insertSwitching(setDto);
        } else {
            log.info("dto update");
            infoMapper.updateCharInfo(setDto);
            infoMapper.updateData(setDto);
            infoMapper.updateEquip(setDto);
            infoMapper.updateAvatar(setDto);
            infoMapper.updateCreature(setDto);
            infoMapper.updateSwitching(setDto);
        }
    }
}
