package com.pirimid.cryptotrade.websocket.gemini.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.GeminiUtil;
import com.pirimid.cryptotrade.websocket.WSExchange;
import com.pirimid.cryptotrade.websocket.gemini.WSGemini;
import com.pirimid.cryptotrade.websocket.gemini.response.OrderResponse;
import com.pirimid.cryptotrade.websocket.gemini.response.RestypeGemini;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.EventListener;
import java.util.List;

public class GeminiSessionHandler implements WebSocketHandler {
    private boolean isConnected=false;
    private OrderService orderService;
    private Account account;
    private WSGemini gemini;
    public GeminiSessionHandler(Account account, OrderService orderService, WSGemini gemini){
        this.orderService = orderService;
        this.account=account;
        this.gemini=gemini;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
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

                if(RestypeGemini.valueOf(order.getType().toUpperCase()) == RestypeGemini.ACCEPTED){
                    // order created method
                    OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                    orderResDTO.setAccountId(account.getAccountId());
                    orderService.createOrder(orderResDTO);
                }
                else if(RestypeGemini.valueOf(order.getType().toUpperCase()) == RestypeGemini.FILL){
                    // call trade method
                    TradeDto tradeDto = GeminiUtil.getTradeDTO(order);
                    orderService.addTrade(tradeDto, EXCHANGE.GEMINI);
                }
                else if(RestypeGemini.valueOf(order.getType().toUpperCase()) == RestypeGemini.REJECTED){
                    // call method for rejected
                    OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                    orderResDTO.setAccountId(account.getAccountId());
                    orderService.rejectOrderByExchangeOrderId(orderResDTO.getExchangeOrderId(),EXCHANGE.GEMINI,orderResDTO.getEndAt());
                }
                else if(RestypeGemini.valueOf(order.getType().toUpperCase()) == RestypeGemini.CLOSED ){
                    if(order.isCancelled()) {
                        // call method for cancelled
                        OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                        orderResDTO.setAccountId(account.getAccountId());
                        orderService.cancelOrderByExchangeOrderId(orderResDTO.getExchangeOrderId(), EXCHANGE.GEMINI, orderResDTO.getEndAt());
                    }else{
                        // order completed successfully
                        OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                        orderResDTO.setAccountId(account.getAccountId());
                        orderService.completeOrder(orderResDTO);
                    }
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
        gemini.establishConnection(account);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
