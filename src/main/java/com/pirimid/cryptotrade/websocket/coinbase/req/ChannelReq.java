package com.pirimid.cryptotrade.websocket.coinbase.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelReq {
    @JsonProperty("type")
    private ReqType type;
    @JsonProperty("signature")
    private String signature;
    @JsonProperty("key")
    private String key;
    @JsonProperty("passphrase")
    private String passphrase;
    @JsonProperty("timestamp")
    private Date timestamp;
    @JsonProperty("product_ids")
    private List<String> productIds;
    @JsonProperty("channels")
    private List<ReqChannel> channels;
}
