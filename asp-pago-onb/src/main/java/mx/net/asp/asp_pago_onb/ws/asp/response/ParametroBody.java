package mx.net.asp.asp_pago_onb.ws.asp.response;

import java.io.Serial;
import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Java class for parametroBody complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="parametroBody">
 *   &lt;complexContent>
 *     &lt;restriction base="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}anyType">
 *       &lt;sequence>
 *         &lt;element name="nombre" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="valor" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
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
@XmlType(name = "parametroBody", propOrder = {
        "nombre",
        "valor"
})
public class ParametroBody implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * -- GETTER --
     *  Gets the value of the nombre property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the nombre property.
     *
     */
    protected String nombre;
    /**
     * -- GETTER --
     *  Gets the value of the valor property.
     * <p>
     *
     * -- SETTER --
     *  Sets the value of the valor property.
     *
     */
    protected String valor;

    public ParametroBody() {}

    public ParametroBody(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

}
