package mx.net.asp.asp_pago_onb.ws.asp;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import mx.net.asp.asp_pago_onb.ws.asp.request.GeneraClabeRequest;
import mx.net.asp.asp_pago_onb.ws.asp.response.GeneraClabeResponse;



@WebService(targetNamespace = "http://generaClabe.spei.ws.izel.net/", name = "GeneraClabeIfz")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface GeneraClabeIfz {

    @WebResult(name = "GeneraClabeResponse", targetNamespace = "http://generaClabe.spei.ws.izel.net/", partName = "parameters")
    @WebMethod
    GeneraClabeResponse procesar(
            @WebParam(partName = "parameters", name = "GeneraClabeRequest", targetNamespace = "http://generaClabe.spei.ws.izel.net/")
            GeneraClabeRequest parameters
    );
}
