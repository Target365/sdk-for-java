package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* Preauth settings
*/
@Getter
@Setter
@ToString
@Accessors(chain = true)
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreAuthSettings implements Serializable
{
    private static final long serialVersionUID = -5140033498878289265L;

		/**
		* Info message sent before preauth message
		*/
		private String infoText;

		/**
		* Sender of info message
		*/
		private String infoSender;

		/**
		* Text inserted before preauth text
		*/
		private String prefixMessage;

		/**
		* Text inserted after preauth text
		*/
		private String postfixMessage;

		/**
		* Delay in minutes between info message and preauth message
		*/
		public Double delay;

		/**
		* MerchantId to perform preauth on
		*/
		private String merchantId;

		/**
		* Service description for Strex "Min Side"
		*/
		private String serviceDescription;
		
		/**
		* If settings are active
		*/
		public Boolean active;
}
