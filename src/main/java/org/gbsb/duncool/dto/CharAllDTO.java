package org.gbsb.duncool.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CharAllDTO {
    private String characterId;
    private String characterName;
    private String serverId;
    private String guildId;
    private String guildName;
    private String adventureName;
    private String jobId;
    private String jobName;
    private String jobGrowId;
    private String jobGrowName;
    private int fame;
    private String udate;
    private String data;
    private String equip;
    private String avatar;
    private String creature;
    private String switching;
}
