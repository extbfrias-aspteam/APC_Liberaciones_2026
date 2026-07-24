package mx.net.asp.asp_pago_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentOBJ {
	private byte[] theAttachmentBytes;
	private String theFilename;
	private String theContentType;
	private Boolean send;
}
