package fr.imt.ales.ArchitecturalGraph;

public class ArchitecturalVertex {
    private AnnotationsEnum annotationType;
    private AutowiringTypesEnum autowiringType;
    private String componentClassType;
    private String componentName;
    private Boolean isDiscovered;

    public ArchitecturalVertex(AnnotationsEnum annotationType, AutowiringTypesEnum autowiringType, String componentClassType, String componentName) {
        this.annotationType = annotationType;
        this.autowiringType = autowiringType;
        this.componentClassType = componentClassType;
        this.componentName = componentName;
        this.isDiscovered = false;
    }

    public AnnotationsEnum getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(AnnotationsEnum annotationType) {
        this.annotationType = annotationType;
    }

    public AutowiringTypesEnum getAutowiringType() {
        return autowiringType;
    }

    public void setAutowiringType(AutowiringTypesEnum autowiringType) {
        this.autowiringType = autowiringType;
    }

    public String getComponentClassType() {
        return componentClassType;
    }

    public void setComponentClassType(String componentClassType) {
        this.componentClassType = componentClassType;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Boolean getDiscovered() {
        return isDiscovered;
    }

    public void setDiscovered(Boolean discovered) {
        isDiscovered = discovered;
    }
}
