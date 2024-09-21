package com.ams.api.payload.response;

import com.ams.api.payload.request.User;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationGetResponse implements Response, Serializable {

	private static final long serialVersionUID = 1252283855499376366L;

	private String responseCode;
	private String responseMessage;
	private User user;

	@Override
	public String toString() {
		return "RegistrationResponse{" +
				"responseCode='" + responseCode + '\'' +
				", responseMessage='" + responseMessage + '\'' +
				", user=" + user +
				'}';
	}
}
