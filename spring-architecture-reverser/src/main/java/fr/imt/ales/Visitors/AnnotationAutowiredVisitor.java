package fr.imt.ales.Visitors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import fr.imt.ales.ArchitecturalGraph.AnnotationsEnum;
import fr.imt.ales.ArchitecturalGraph.ArchitecturalVertex;
import fr.imt.ales.ArchitecturalGraph.AutowiringTypesEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AnnotationAutowiredVisitor extends VoidVisitorAdapter {
    final static Logger logger = LogManager.getLogger(AnnotationBeanVisitor.class);
    //private List<String> listAutowiredComponentTypes;
    private List<ArchitecturalVertex> architecturalVertexAutowiredList;

    public AnnotationAutowiredVisitor(){
        architecturalVertexAutowiredList = new ArrayList<>();
    }

    @Override
    public void visit(FieldDeclaration n, Object arg) {
        super.visit(n, arg);
        /*if(n.toString().contains(AnnotationsEnum.AUTOWIRED.toString())){
            System.out.println(n.getElementType());
            //listAutowiredComponentTypes.add(n.getType().asString());
        }*/
        if(n.toString().contains(AnnotationsEnum.AUTOWIRED.toString())){
            architecturalVertexAutowiredList.add(new ArchitecturalVertex(
                    AnnotationsEnum.AUTOWIRED,
                    AutowiringTypesEnum.FIELD,
                    n.getElementType().asString(),
                    n.getVariables().toString()
            ));
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
        //System.out.println("YOUHOU ==> " + n.getImplementedTypes().get(0));
    }

    @Override
    public void visit(MarkerAnnotationExpr n, Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
        /*if(n.toString().contains("@Bean")){
            System.out.println(n.getType().asString());
            listAutowiredComponentTypes.add(n.getType().asString());
        }*/
    }

    public void cleararchitecturalVertexAutowiredList(){
        architecturalVertexAutowiredList.clear();
    }

    public List<ArchitecturalVertex> getarchitecturalVertexAutowiredList() {
        return architecturalVertexAutowiredList;
    }

//    public void clearListAutowiredComponentTypes(){
//        listAutowiredComponentTypes.clear();
//    }
//
//    public List<String> getListAutowiredComponentTypes() {
//        return listAutowiredComponentTypes;
//    }
//
//    public void setListAutowiredComponentTypes(List<String> listAutowiredComponentTypes) {
//        this.listAutowiredComponentTypes = listAutowiredComponentTypes;
//    }
}
