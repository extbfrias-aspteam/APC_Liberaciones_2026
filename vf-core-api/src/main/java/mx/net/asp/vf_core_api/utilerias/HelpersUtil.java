package mx.net.asp.vf_core_api.utilerias;

import org.springframework.stereotype.Component;

@Component
public class HelpersUtil {
    // Helpers seguros
    public String trim(String s) {
        return s == null ? null : s.trim();
    }

    public Double asDouble(Object o) {
        if (o == null) return 0.0;
        return Double.valueOf(o.toString());
    }

    public Integer asInt(Object o) {
        if (o == null) return 0;
        return Integer.valueOf(o.toString());
    }
}