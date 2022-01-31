package com.pirimid.cryptotrade.websocket.gemini.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirimid.cryptotrade.DTO.OrderResDTO;
import com.pirimid.cryptotrade.DTO.TradeDto;
import com.pirimid.cryptotrade.helper.exchange.EXCHANGE;
import com.pirimid.cryptotrade.model.Account;
import com.pirimid.cryptotrade.service.OrderService;
import com.pirimid.cryptotrade.util.GeminiUtil;
import com.pirimid.cryptotrade.websocket.gemini.response.OrderResponse;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public class GeminiSessionHandler implements WebSocketHandler {
    private boolean isConnected=false;
    private OrderService orderService;
    private Account account;

    public GeminiSessionHandler(Account account,OrderService orderService){
        this.orderService = orderService;
        this.account=account;
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

                if(order.getType().equals("accepted")){
                    // order created method
                    OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                    orderResDTO.setAccountId(account.getAccountId());
                    orderService.createOrder(orderResDTO);
                }
                else if(order.getType().equals("fill")){
                    // call trade method
                    TradeDto tradeDto = GeminiUtil.getTradeDTO(order);
                    orderService.addTrade(tradeDto, EXCHANGE.GEMINI);
                }
                else if(order.getType().equals("rejected")){
                    // call method for rejected
                    OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                    orderResDTO.setAccountId(account.getAccountId());
                    orderService.rejectOrderByExchangeOrderId(orderResDTO.getExchangeOrderId(),EXCHANGE.GEMINI,orderResDTO.getEndAt());
                }
                else if(order.getType().equals("closed") && order.isCancelled()){
                    // call method for cancelled
                    OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                    orderResDTO.setAccountId(account.getAccountId());
                    orderService.cancelOrderByExchangeOrderId(orderResDTO.getExchangeOrderId(),EXCHANGE.GEMINI,orderResDTO.getEndAt());
                }
                else if(order.getType().equals("closed")){
                    // order completed successfully
                    OrderResDTO orderResDTO = GeminiUtil.getPlaceOrderResDTO(order);
                    orderResDTO.setAccountId(account.getAccountId());
                    orderService.completeOrder(orderResDTO);
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
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
