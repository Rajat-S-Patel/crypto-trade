package com.pirimid.cryptotrade.controller;

import com.pirimid.cryptotrade.DTO.ExchangeDto;
import com.pirimid.cryptotrade.DTO.SymbolResDTO;
import com.pirimid.cryptotrade.model.Exchange;
import com.pirimid.cryptotrade.service.ExchangeService;
import com.pirimid.cryptotrade.util.ExchangeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
public class ExchangeController {

    @Autowired
    ExchangeService exchangeService;

    @GetMapping("/exchanges")
    public ResponseEntity<?> getAllExchanges(){
        List<Exchange> exchangeList = exchangeService.getAllExchanges();
        if(exchangeList==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(exchangeList);
    }
    @GetMapping("/exchanges/{userid}")
    public ResponseEntity<?> getAllExchangesByUserid(@PathVariable(required = true) UUID userid){
        List<ExchangeDto> exchangeList = exchangeService.getAllExchanges(userid);
        if(exchangeList==null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(exchangeList);
    }
    @GetMapping("/pairs") //parirs?exchange=AAX
    public ResponseEntity<?> getPairs(@RequestParam(required = true,name = "exchange") String exchangeName){
       List<SymbolResDTO> symbols=exchangeService.getPairs(exchangeName);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(symbols);
    }

}
