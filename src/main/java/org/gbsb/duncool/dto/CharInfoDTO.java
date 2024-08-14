package org.gbsb.duncool.dto;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CharInfoDTO {

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
    private int totalPrice;
    private int hidden;
}
