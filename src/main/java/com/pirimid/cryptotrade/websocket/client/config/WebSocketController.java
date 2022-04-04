package com.pirimid.cryptotrade.websocket.client.config;

import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.model.Side;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/message")
    @SendTo("/channel/order")
    public String sendOrderData(){
        System.out.println("@@@");
//        messagingTemplate.convertAndSend("/channel/order","message here");
        return "message here";
    }

}
