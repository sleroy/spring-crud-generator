
package ch.demo.generation.model;

/**
* Light DTO Entity without relationships for {link com.byoskill.tools.example.Payment }
*/
public class PaymentLightDto {
        private long id;
        private String uuid;
        private String applicationCode;
        private String requestorId;
        private Long amount;
        private Integer baid;
        private String transactionId;
        private String msisdn;
        private String orderId;
        private String operatorId;
        private String channelCode;
        private LocalDateTime creationDate;
        private LocalDateTime lastUpdateDate;
        private String errorCode;
        private String errorMessage;
        private String errorDetail;

    /**
     * Instantiate a new DTO.
     */
    public PaymentLightDto() {
    super();
    }


}

