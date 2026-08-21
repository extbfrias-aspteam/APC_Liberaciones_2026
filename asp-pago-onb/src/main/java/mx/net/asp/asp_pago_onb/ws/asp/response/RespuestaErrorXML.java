package mx.net.asp.asp_pago_onb.ws.asp.response;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Java class for respuestaErrorXML complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="respuestaErrorXML">
 *   &lt;complexContent>
 *     &lt;restriction base="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}anyType">
 *       &lt;sequence>
 *         &lt;element name="codigoError" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}long"/>
 *         &lt;element name="descError" type="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}string" minOccurs="0"/>
 *         &lt;element name="errores" type="{<a href="http://grupoGuardar.cat.ws.izel.net/">...</a>}respuestaErrorXML" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaErrorXML", propOrder = {
        "codigoError",
        "descError",
        "errores"
})
public class RespuestaErrorXML {

    /**
     * -- GETTER --
     *  Gets the value of the codigoError property.
     * -- SETTER --
     *  Sets the value of the codigoError property.

     */
    @Setter
    @Getter
    protected long codigoError;
    @Setter
    @Getter
    private String descError;
    @XmlElement(nillable = true)
    protected List<RespuestaErrorXML> errores;

    public RespuestaErrorXML(){
        codigoError = 0L;
        descError = "";
        errores = new ArrayList<>();
    }

    public RespuestaErrorXML(Long id){
        codigoError = id;
    }

    public RespuestaErrorXML(Long id, String descError){
        this.codigoError = id;
        this.descError = descError;
    }

    /**
     * Gets the value of the errores property.
     *
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     */
    public List<RespuestaErrorXML> getErrores() {
        if (errores == null) {
            errores = new ArrayList<>();
        }
        return this.errores;
    }

    public void addCodigo(long id, String descripcion) {
        if (errores == null) {
            errores = new ArrayList<>();
        }
        codigoError = id;
        errores.add(new RespuestaErrorXML(id, descripcion));
    }

    public Long obtenerUltimoError() {
        if (errores == null || errores.isEmpty())
            return null;

        return errores.getFirst().codigoError;
    }

    public String obtenerDescUltimoError() {
        if (errores == null || errores.isEmpty())
            return null;

        return errores.getFirst().descError;
    }

}
