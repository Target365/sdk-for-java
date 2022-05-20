package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.*;

/**
 * Delivery report
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryReport implements Serializable {

    private static final long serialVersionUID = 2931317188369600566L;

   /**
    * Correlation id associated with the message.
    */
    private String correlationId;

   /**
    * Transaction id associated with the message.
    */
    private String transactionId;

   /**
    * Price associated with the message.
    */
    private Double price;

   /**
    * Sender associated with the message.
    */
    private String sender;

   /**
    * Recipient associated with the message.
    */
    private String recipient;

   /**
    * Operator associated with the message. Can be 'no.telenor', 'no.telia', 'no.ice' etc.
    */
    private String operator;

   /**
    * Delivery status code.
    */
    private String statusCode;

   /**
    * Detailed status code.
    */
    private String detailedStatusCode;

   /**
    * Whether message was delivered. Null if status is unknown.
    */
    private Boolean delivered;

   /**
    * Whether billing was performed. Null if status is unknown.
    */
    private Boolean billed;

   /**
    * SMSC message parts.
    */
    private int smscMessageParts;

    /**
     * Associated custom properties.
     */
    private Map<String, Object> properties;
}
