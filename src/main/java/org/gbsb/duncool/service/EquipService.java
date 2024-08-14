package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface EquipService {

    ObjectNode checkLvlBonusEquip(ObjectNode skill, ObjectNode equip, ObjectNode avatar);
}
