package com.scpfoundation.psybotic.disastercheckservice.FCM.Models;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
public class PushNotificationRequest {
    private String title;
    private String message;
    private String topic;
    private String token;
}
