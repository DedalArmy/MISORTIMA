package fr.imt.ales.ArchitecturalGraph;

public enum AnnotationsEnum {
    BEAN("@Bean"),
    AUTOWIRED("@Autowired");

    private String value;

    AnnotationsEnum(String value){
        this.value = value;
    }

    public String toString(){
        return this.value;
    }

}
