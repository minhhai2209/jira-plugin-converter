package minhhai2209.jirapluginconverter.plugin.descriptor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="workflow-function")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkflowPostFunctionModule {
    @XmlAttribute(name="class")
    private String clazz;

    @XmlAttribute(required=true)
    private String key;

    @XmlAttribute
    private String name;

    @XmlElement
    private Description description;

    @XmlElement(name="function-class")
    private FunctionClass functionClass;

    @XmlElement
    private List<Resource> resource = new ArrayList<Resource>();

    @XmlElement
    private boolean orderable;

    @XmlElement
    private boolean unique;

    @XmlElement
    private boolean deletable;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

    public FunctionClass getFunctionClass() {
        return functionClass;
    }

    public void setFunctionClass(FunctionClass functionClass) {
        this.functionClass = functionClass;
    }

    public List<Resource> getResource() {
        return resource;
    }

    public void addResource(Resource resource) {
        this.resource.add(resource);
    }

    public boolean getOrderable() {
        return orderable;
    }

    public void setOrderable(boolean orderable) {
        this.orderable = orderable;
    }

    public boolean getUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

}
