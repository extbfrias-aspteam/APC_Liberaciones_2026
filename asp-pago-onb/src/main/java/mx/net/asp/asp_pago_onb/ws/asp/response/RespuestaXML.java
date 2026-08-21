package mx.net.asp.asp_pago_onb.ws.asp.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Java class for respuestaXML complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="respuestaXML">
 *   &lt;complexContent>
 *     &lt;restriction base="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}anyType">
 *       &lt;sequence>
 *         &lt;element name="body" type="{<a href="http://grupoGuardar.cat.ws.izel.net/">...</a>}respuestaBodyXML" minOccurs="0"/>
 *         &lt;element name="errores" type="{<a href="http://grupoGuardar.cat.ws.izel.net/">...</a>}respuestaErrorXML" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaXML", propOrder = {
        "body",
        "errores"
})
public class RespuestaXML {

    /**
     * -- GETTER --
     *  Gets the value of the body property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the body property.
     *
     */
    protected RespuestaBodyXML body;
    /**
     * -- GETTER --
     *  Gets the value of the errores property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the errores property.
     *
     */
    protected RespuestaErrorXML errores;

    public RespuestaXML(){
        body = new RespuestaBodyXML();
        errores = new RespuestaErrorXML();
    }

}
