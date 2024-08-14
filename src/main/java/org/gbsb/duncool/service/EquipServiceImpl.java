package org.gbsb.duncool.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Log4j2
public class EquipServiceImpl implements EquipService {

    @Override
    public ObjectNode checkLvlBonusEquip(ObjectNode skill, ObjectNode equip, ObjectNode avatar) {
        JsonNode equipment = equip.path("equipment");
        JsonNode active = skill.path("active");
        JsonNode passive = skill.path("passive");
        JsonNode avt = avatar.path("avatar");
        int plusLvl = 0;
        ArrayList<String> names = new ArrayList<>();
        boolean step = false; // 내딛는 용기

        if (equipment.isArray()) {
            for (JsonNode eq : equipment) {
                String name = eq.path("itemName").asText();
                if (name.contains("근원을 삼킨")) {
                    plusLvl += 1;
                } else if (name.contains("내딛은 자의")) {
                    plusLvl += 2;
                } else if (name.contains("내딛는 용기")) {
                    step = true;
                }
            }
        }
        if (avt.isArray()) {
            for (JsonNode a : avt) {
                String slot = a.path("slotId").asText();
                JsonNode em = a.path("emblems");
                if (slot.equals("JACKET") || slot.equals("PANTS")) {
                    for (JsonNode ac : active) {

                        String option = a.path("optionAbility").asText();
                        String skillName = ac.path("name").asText();
                        if (option.equals(skillName + " 스킬Lv +1")) {
                            names.add(skillName);
                        }
                    }
                    for (JsonNode ps : passive) {

                        String option = a.path("optionAbility").asText();
                        String skillName = ps.path("name").asText();
                        if (option.equals(skillName + " 스킬Lv +1")) {
                            names.add(skillName);
                        }
                    }
                    for (JsonNode e : em) {
                        if (e.path("slotColor").asText().equals("플래티넘") &&
                                e.path("itemRarity").asText().equals("레전더리") &&
                                e.path("itemName").asText().startsWith("플래티넘 엠블렘[")) {
                            String emName = e.path("itemName").asText();
                            names.add(emName.substring(9, emName.length() - 1));
                        }
                    }
                }
            }
        }

        if (plusLvl == 0 && names.isEmpty()) return skill;

        if (active.isArray()) {
            for (JsonNode ac : active) {
                int acLvl = ac.path("level").asInt() + plusLvl;
                int reqLvl = ac.path("requiredLevel").asInt();
                ((ObjectNode) ac).put("level", acLvl);

                if (step && reqLvl <= 25) {
                    acLvl += 2;
                    ((ObjectNode) ac).put("level", acLvl);
                }
                if (!names.isEmpty()) {
                    for (String n : names) {
                        if (n.equals(ac.path("name").asText())) {
                            acLvl += 1;
                            ((ObjectNode) ac).put("level", acLvl);
                        }
                    }
                }
            }
        }
        if (passive.isArray()) {
            for (JsonNode ps : passive) {
                int psLvl = ps.path("level").asInt() + plusLvl;
                int reqLvl = ps.path("requiredLevel").asInt();
                ((ObjectNode) ps).put("level", psLvl);

                if (step && reqLvl <= 25) {
                    psLvl += 2;
                    ((ObjectNode) ps).put("level", psLvl);
                }
                if (!names.isEmpty()) {
                    for (String n : names) {
                        if (n.equals(ps.path("name").asText())) {
                            psLvl += 1;
                            ((ObjectNode) ps).put("level", psLvl);
                        }
                    }
                }
            }
        }

        return skill;
    }
}
