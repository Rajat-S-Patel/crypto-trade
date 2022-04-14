package com.pirimid.cryptotrade.websocket.publicWS.gemini.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TickerGeminiDto {
    private String type;
    private String eventId;
    @JsonProperty("socket_sequence")
    private Long socketSequence;
    @JsonProperty("events")
    private List<TickerGeminiEvents> events;
}
