package com.example.watch.dto;


import lombok.*;

@Getter
@Setter
public class CancelOrUpdateReceiverRequest {
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private Boolean cancel;
}