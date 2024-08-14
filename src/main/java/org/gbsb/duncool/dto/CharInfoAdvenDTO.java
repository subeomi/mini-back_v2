package org.gbsb.duncool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CharInfoAdvenDTO {
    private String characterId;
    private String characterName;
    private String serverId;
    private String jobGrowName;
    private String guildName;
    private int fame;
}
