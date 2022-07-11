package io.target365.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

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
    private static final long serialVersionUID = -1243675398750972678L;

		/**
		* Info message sent before preauth message
		*/
		private String infoText { get; set; }

		/**
		* Sender of info message
		*/
		private String infoSender { get; set; }

		/**
		* Text inserted before preauth text
		*/
		private String prefixMessage { get; set; }

		/**
		* Text inserted after preauth text
		*/
		private String postfixMessage { get; set; }

		/**
		* Delay in minutes between info message and preauth message
		*/
		public Double delay { get; set; }

		/**
		* MerchantId to perform preauth on
		*/
		private String merchantId { get; set; }

		/**
		* Service description for Strex "Min Side"
		*/
		private String serviceDescription { get; set; }
		
		/**
		* If settings are active
		*/
		public Boolean active { get; set; }
}
