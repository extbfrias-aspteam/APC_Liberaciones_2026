package mx.net.asp.asp_pago_onb.ws.asp.response;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for respuestaBodyXML complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="respuestaBodyXML">
 *   &lt;complexContent>
 *     &lt;restriction base="{<a href="http://www.w3.org/2001/XMLSchema">...</a>}anyType">
 *       &lt;sequence>
 *         &lt;element name="params" type="{<a href="http://grupoGuardar.cat.ws.izel.net/">...</a>}parametroBody" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "respuestaBodyXML", propOrder = {
        "params"
})
public class RespuestaBodyXML {

    @XmlElement(nillable = true)
    protected List<ParametroBody> params;

    /**
     * Gets the value of the params property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the params property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParams().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParametroBody }
     *
     *
     */
    public List<ParametroBody> getParams() {
        if (params == null) {
            params = new ArrayList<>();
        }
        return this.params;
    }

    public void addParametro(String nombreParametro, String valor){
        if(params == null)
            params = new ArrayList<>();

        params.add(new ParametroBody(nombreParametro, valor));
    }

    public String obtenerParametro(String nombreParametro){
        if(params == null || nombreParametro == null)
            return null;

        for(ParametroBody pb : params){
            if(nombreParametro.equals(pb.getNombre()))
                return pb.getValor();
        }

        return null;
    }

}
