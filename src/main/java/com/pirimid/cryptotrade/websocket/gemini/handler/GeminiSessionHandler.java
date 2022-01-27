package com.pirimid.cryptotrade.websocket.gemini.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.model.Order;
import com.pirimid.cryptotrade.util.GeminiUtil;
import com.pirimid.cryptotrade.websocket.gemini.response.OrderResponse;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public class GeminiSessionHandler implements WebSocketHandler {
    private boolean isConnected=false;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Gemini connection established");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String payload = message.getPayload().toString();
        if(payload.startsWith("{\"type\":\"heartbeat\"")) return;
        if(payload.startsWith("{\"type\":\"subscription_ack\"")) {
            isConnected=true;
            return;
        }
        if(isConnected) {
            List<OrderResponse> orders =  new ObjectMapper().readValue(payload, new TypeReference<List<OrderResponse>>() {});
            for(OrderResponse order:orders){
                System.out.println("Order = "+order);
                System.out.println("Standard DTO:- "+GeminiUtil.getPlaceOrderResDTO(order));
                // TODO call service method to set the order based on the type of order
                if(order.getType().equals("accepted")){
                    // order created method
                }
                else if(order.getType().equals("fill")){
                    // call trade method
                }
                else if(order.getType().equals("rejected")){
                    // call method for rejected
                }
                else if(order.getType().equals("closed") && order.isCancelled()){
                    // call method for cancelled
                }
                else if(order.getType().equals("closed")){
                    // order completed successfully
                }

            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        isConnected=false;
        System.out.println("Gemini connection closed");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
