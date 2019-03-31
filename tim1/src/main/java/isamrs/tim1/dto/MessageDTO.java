package isamrs.tim1.dto;

import java.io.Serializable;

public class MessageDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6747235972929476490L;
	private String message;
	private String header;

	public MessageDTO(String message, String header) {
		super();
		this.message = message;
		this.setHeader(header);
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

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

}
