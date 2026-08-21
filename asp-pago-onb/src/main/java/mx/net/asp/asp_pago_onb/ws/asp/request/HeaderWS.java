package mx.net.asp.asp_pago_onb.ws.asp.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Java class for headerWS complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="headerWS">
 *   &lt;complexContent>
 *     &lt;restriction base="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}anyType">
 *       &lt;sequence>
 *         &lt;element name="idEmpresa" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}long"/>
 *         &lt;element name="idResponsabilidad" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}long"/>
 *         &lt;element name="idSesion" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="idUsuario" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}long"/>
 *         &lt;element name="ipHost" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="nameHost" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="usuarioClave" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}long"/>
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
@XmlType(name = "headerWS", propOrder = {
        "idEmpresa",
        "idResponsabilidad",
        "idSesion",
        "idUsuario",
        "idClaseCanalAtencion",
        "idCanalAtencion",
        "idPuntoAtencion",
        "idUbicacion",
        "idSucursal",
        "ipHost",
        "nameHost",
        "usuarioClave"
})
public class HeaderWS {

    /**
     * -- GETTER --
     *  Gets the value of the idEmpresa property.
     * -- SETTER --
     *  Sets the value of the idEmpresa property.

     */
    protected long idEmpresa;
    /**
     * -- GETTER --
     *  Gets the value of the idResponsabilidad property.
     * -- SETTER --
     *  Sets the value of the idResponsabilidad property.

     */
    protected long idResponsabilidad;
    /**
     * -- GETTER --
     *  Gets the value of the idSesion property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the idSesion property.
     *
     */
    protected String idSesion;
    protected String usuarioClave;
    /**
     * -- GETTER --
     *  Gets the value of the idUsuario property.
     * -- SETTER --
     *  Sets the value of the idUsuario property.

     */
    protected long idUsuario;
    protected long idClaseCanalAtencion;
    protected long idCanalAtencion;
    protected long idPuntoAtencion;
    protected long idUbicacion;
    protected long idSucursal;
    protected String ipHost;
    protected String nameHost;

}