package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="function-class")
@XmlAccessorType(XmlAccessType.FIELD)
public class FunctionClass {
    @XmlValue
    private String value;
    public FunctionClass() {
    }
    public FunctionClass(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
