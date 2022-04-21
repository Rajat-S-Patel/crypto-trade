package com.pirimid.cryptotrade.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pirimid.cryptotrade.model.Exchange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private Exchange exchange;
    private String accountLabel;
    private String apiKey;
    private String secretKey;
    private String passPhrase;
    private UUID userId;
    private UUID accountId;
}
