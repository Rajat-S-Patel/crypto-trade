package com.pirimid.cryptotrade.helper.exchange.coinbase.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ToString
public class ProfileResDTO {
    private UUID id;    //profile id
    private String user_id;
    private String name;
    private boolean active;
    private boolean is_default;
    private Date created_at;
}
