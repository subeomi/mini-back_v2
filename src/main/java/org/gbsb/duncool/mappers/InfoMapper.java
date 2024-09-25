package org.gbsb.duncool.mappers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.gbsb.duncool.dto.CharAllDTO;
import org.gbsb.duncool.dto.CharInfoAdvenDTO;
import org.gbsb.duncool.dto.CharInfoDTO;

import java.util.List;

@Mapper
public interface InfoMapper {

    int insertCharInfo(CharAllDTO dto);

    List<CharInfoDTO> getCharList(String characterName);

    List<CharInfoAdvenDTO> getCharListByAdventure(String adventureName);

    CharAllDTO getCharAll(String characterId);

    @Select("select * from char_info where characterId = #{characterId}")
    CharInfoDTO getOneInfo(String CharacterId);

    @Insert("INSERT INTO char_data (characterId, data) VALUES (#{characterId}, #{data})")
    int insertData(CharAllDTO dto);

    @Insert("INSERT INTO char_equip (characterId, equip) VALUES (#{characterId}, #{equip})")
    int insertEquip(CharAllDTO dto);

    @Insert("INSERT INTO char_avatar (characterId, avatar) VALUES (#{characterId}, #{avatar})")
    int insertAvatar(CharAllDTO dto);

    @Insert("INSERT INTO char_creature (characterId, creature) VALUES (#{characterId}, #{creature})")
    int insertCreature(CharAllDTO dto);

    @Insert("INSERT INTO char_switching (characterId, switching) VALUES (#{characterId}, #{switching})")
    int insertSwitching(CharAllDTO dto);

    // Update ---------------------------------------------
    int updateCharInfo(CharAllDTO dto);

    @Update("UPDATE char_data SET data = #{data} WHERE characterId = #{characterId}")
    int updateData(CharAllDTO dto);

    @Update("UPDATE char_equip SET equip = #{equip} WHERE characterId = #{characterId}")
    int updateEquip(CharAllDTO dto);

    @Update("UPDATE char_avatar SET avatar = #{avatar} WHERE characterId = #{characterId}")
    int updateAvatar(CharAllDTO dto);

    @Update("UPDATE char_creature SET creature = #{creature} WHERE characterId = #{characterId}")
    int updateCreature(CharAllDTO dto);

    @Update("UPDATE char_switching SET switching = #{switching} WHERE characterId = #{characterId}")
    int updateSwitching(CharAllDTO dto);
}
