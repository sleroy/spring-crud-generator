package com.byoskill.tools.example;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "PC_RO_PAYMENT")
//, uniqueConstraints = @UniqueConstraint(columnNames = "EXTERNAL_ID")
public class Payment implements java.io.Serializable {
    /**
     * The payment id.
     */
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "PAYMENT_ID", unique = true, nullable = false, precision = 18)
    private long id;

    /**
     * The payment id.
     */
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PAYMENT_UUID", unique = true, nullable = false, precision = 36, updatable = false)
    private String uuid;


    @Column(name = "APPLICATION_CODE", length = 50, nullable = false)
    @NotBlank
    private String applicationCode = "";

    @Column(name = "REQUESTOR_ID", nullable = true, length = 60)

    private String requestorId;

    @Column(name = "AMOUNT", nullable = true, scale = 0)
    @PositiveOrZero
    private Long amount;

    @Column(name = "BAID", nullable = true, length = 60)
    @PositiveOrZero

    private Integer baid;

    @Column(name = "TRANSACTION_ID", nullable = true, length = 32)

    private String transactionId;

    @Column(name = "MSISDN", nullable = true, length = 11)

    private String msisdn;


    @Column(name = "ORDER_ID", nullable = true, length = 60)

    private String orderId;
    @Column(name = "OPERATOR_ID", nullable = true, length = 50)

    private String operatorId;
    @Column(name = "CHANNEL_CODE", nullable = false, length = 25)
    @NotBlank
    private String channelCode;


    /**
     * The creation date.
     */
    @Column(name = "CREATION_DATE", length = 7)
    private LocalDateTime creationDate;

    /**
     * The last update date.
     */
    @Column(name = "LAST_UPDATE_DATE", length = 7)
    private LocalDateTime lastUpdateDate;

    /**
     * The error code.
     */
    @Column(name = "ERROR_CODE", length = 10)
    private String errorCode;

    /**
     * The error message.
     */
    @Column(name = "ERROR_MESSAGE")
    private String errorMessage;

    /**
     * The error detail.
     */
    @Column(name = "ERROR_DETAIL")
    private String errorDetail;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Credentials> credentials;
}
