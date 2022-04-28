package com.pirimid.cryptotrade.websocket.clientWS.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Service
public class PublicWebsocketService {
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    private Map<String,Integer> subscribedPairs = new HashMap<>();

    public Map<String,Integer> getSubscribedPairs(){
        return subscribedPairs;
    }

    @EventListener
    public void subscribeEventListener(SessionSubscribeEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String key = headerAccessor.getDestination();
        if(!key.contains("price")) return;
        if(subscribedPairs.containsKey(key))
            subscribedPairs.compute(key,(k,v)->v+1);
        else
            subscribedPairs.put(key,1);
    }
    @EventListener
    public void unsubscribeEventListener(SessionUnsubscribeEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String key = headerAccessor.getNativeHeader("id").get(0);
        if(!key.contains("price")) return;
        if(subscribedPairs.containsKey(key))
            subscribedPairs.compute(key,(k,v)->(v==1)?null:v-1);
    }

}
