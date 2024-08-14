package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gbsb.duncool.dto.CharInfoAdvenDTO;

import java.util.List;
import java.util.Map;

public interface DuncoolService {

    JsonNode getCharList(String characterName);

    JsonNode getCharListByAdventure(String adventureName);

    ObjectNode getOneInfo(String serverId, String characterId);

    ObjectNode getEquipment(String serverId, String characterId);

    ObjectNode getCharInfo(String serverId, String characterId);

    ObjectNode getAvatar(String serverId, String characterId);

    ObjectNode getCreature(String serverId, String characterId);

    ObjectNode getSwitching(String serverId, String characterId, String jobId);
}
