package fr.imt.ales.Visitors;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import fr.imt.ales.ArchitecturalGraph.AnnotationsEnum;
import fr.imt.ales.ArchitecturalGraph.ArchitecturalVertex;
import fr.imt.ales.ArchitecturalGraph.AutowiringTypesEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AnnotationBeanVisitor extends VoidVisitorAdapter {

    final static Logger logger = LogManager.getLogger(AnnotationBeanVisitor.class);
    public static final DataKey<String> NAME = new DataKey<String>() {};
    private List<ArchitecturalVertex> listBeanClassTypes;

    public AnnotationBeanVisitor(){
        listBeanClassTypes = new ArrayList<>();
    }

    @Override
    public void visit(MarkerAnnotationExpr n, Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(NormalAnnotationExpr n, Object arg){
        super.visit(n,arg);
        //System.out.println(n.getPairs().get(0).);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Object arg) {
        super.visit(n, arg);
    }

    @Override
    public void visit(MethodDeclaration n, Object arg) {
        super.visit(n, arg);
            if(n.toString().contains(AnnotationsEnum.BEAN.toString())){
                //System.out.println(n.getAnnotations());

                String beanName = null;
                for (AnnotationExpr annotation : n.getAnnotations()) {
                    //System.out.println(annotation.getData(NAME));
                    if(annotation instanceof NormalAnnotationExpr){
                        NormalAnnotationExpr normalAnnotationExpr = (NormalAnnotationExpr)annotation;
                        System.out.println(normalAnnotationExpr.getPairs());
                        //normalAnnotationExpr.get
                        for (MemberValuePair pair : normalAnnotationExpr.getPairs()) {
                            if(pair.getName().asString().equals("name")){
                                System.out.println(pair.getValue());
                                beanName = pair.getValue().toString().replace("\"","");
                            }
                        }
                    }

                    if(annotation.toString().contains(AnnotationsEnum.BEAN.toString())){
                    }
                }
                System.out.println(n.getType().asString());
                ArchitecturalVertex architecturalVertex = new ArchitecturalVertex(
                        AnnotationsEnum.BEAN,
                        AutowiringTypesEnum.BEAN_DECLARATION,
                        n.getTypeAsString(),
                        (beanName != null ? n.getNameAsString()  + "-" + beanName : n.getNameAsString())
                        );

                listBeanClassTypes.add(architecturalVertex);
            }
    }

    public void clearListBeanClassTypes(){
        listBeanClassTypes.clear();
    }

    public List<ArchitecturalVertex> getListBeanClassTypes() {
        return listBeanClassTypes;
    }

    public void setListBeanClassTypes(List<ArchitecturalVertex> listBeanClassTypes) {
        this.listBeanClassTypes = listBeanClassTypes;
    }
}
