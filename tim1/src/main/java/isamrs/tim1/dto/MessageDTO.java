package isamrs.tim1.dto;

import java.io.Serializable;

public class MessageDTO implements Serializable {

	private static final long serialVersionUID = -6747235972929476490L;
	private String message;
	private String toastType;

	public MessageDTO(String message, String toastType) {
		super();
		this.message = message;
		this.toastType = toastType;
	}

	public MessageDTO() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public enum ToasterType {
		SUCCESS("success"), ERROR("error");

		private final String text;

		ToasterType(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public String getToastType() {
		return toastType;
	}

	public void setToastType(String toastType) {
		this.toastType = toastType;
	}
}
