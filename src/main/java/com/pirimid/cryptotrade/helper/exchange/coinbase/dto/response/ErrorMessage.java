package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorMessage {
    private String message;
}
