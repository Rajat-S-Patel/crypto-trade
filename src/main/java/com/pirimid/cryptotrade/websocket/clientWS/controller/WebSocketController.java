package com.pirimid.cryptotrade.websocket.clientWS.controller;

import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.model.Side;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @MessageMapping("/orders")
    public void sendOrderData(){
        messagingTemplate.convertAndSend("/topic/order/"+userService.getDefaultUser().getUserId().toString(),orderService.getOrdersByUserId(userService.getDefaultUser().getUserId()));
    }

}
