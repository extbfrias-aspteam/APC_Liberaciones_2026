package mx.net.asp.asp_pago_onb.ws.asp.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Java class for GeneraClabeRequest complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="GeneraClabeRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}anyType">
 *       &lt;sequence>
 *         &lt;element name="header" type="{<a href="http://generaClabe.spei.ws.izel.net/">...</a>}headerWS" minOccurs="0"/>
 *         &lt;element name="productoId" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}long" minOccurs="0"/>
 *         &lt;element name="claveAplicacion" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="referencia" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="claveRegion" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="obtenerClabe" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}int" minOccurs="0"/>
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
@XmlType(name = "GeneraClabeRequest", propOrder = {
        "header",
        "productoId",
        "claveAplicacion",
        "referencia",
        "claveRegion",
        "obtenerClabe"
})
public class GeneraClabeRequest {

    /**
     * -- GETTER --
     *  Gets the value of the header property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the header property.
     *
     */
    protected HeaderWS header;
    /**
     * -- GETTER --
     *  Gets the value of the productoId property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the productoId property.
     *
     */
    protected Long productoId;
    /**
     * -- GETTER --
     *  Gets the value of the claveAplicacion property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the claveAplicacion property.
     *
     */
    protected String claveAplicacion;
    /**
     * -- GETTER --
     *  Gets the value of the referencia property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the referencia property.
     *
     */
    protected String referencia;
    /**
     * -- GETTER --
     *  Gets the value of the claveRegion property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the claveRegion property.
     *
     */
    protected String claveRegion;
    /**
     * -- GETTER --
     *  Gets the value of the obtenerClabe property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the obtenerClabe property.
     *
     */
    protected Integer obtenerClabe;

}
