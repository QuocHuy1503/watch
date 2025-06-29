package com.example.watch.controller;

import com.example.watch.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/vnpay")
@org.springframework.stereotype.Controller
public class VnPayController {
    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") long orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              HttpServletRequest request){
        String vnpayUrl = vnPayService.createOrder(orderTotal, orderInfo, request);
        return vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public ResponseEntity<?> handleVnPayReturn(HttpServletRequest request, HttpServletResponse response) {
        int paymentStatus = vnPayService.orderReturn(request); // 1 = success

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderInfo);
        result.put("totalPrice", totalPrice);
        result.put("paymentTime", paymentTime);
        result.put("transactionId", transactionId);
        result.put("status", paymentStatus == 1 ? "success" : "fail");
        if(paymentStatus == 1){
            try {
                response.sendRedirect("http://localhost:5173/order-success");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {

            try {
                response.sendRedirect("http://localhost:5173/cart");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseEntity.ok(result);
    }
}