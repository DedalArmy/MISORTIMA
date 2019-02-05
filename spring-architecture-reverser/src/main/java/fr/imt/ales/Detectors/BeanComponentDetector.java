package fr.imt.ales.Detectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import fr.imt.ales.ArchitecturalGraph.ArchitecturalVertex;
import fr.imt.ales.Visitors.AnnotationBeanVisitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BeanComponentDetector {

    final static Logger logger = LogManager.getLogger(BeanComponentDetector.class);
    private AnnotationBeanVisitor annotationBeanVisitor;

    public BeanComponentDetector(){
        annotationBeanVisitor = new AnnotationBeanVisitor();
    }

    public List<ArchitecturalVertex> detectBeans(String pathToJavaSource) throws URISyntaxException, IOException {
        //Clear the list of beans already visited
        annotationBeanVisitor.clearListBeanClassTypes();

        URI sourceJavaUrl = new File(pathToJavaSource).toURI();

        //Parse the Java source file
        CompilationUnit compilationUnit;
        try (InputStream inputStream = new ByteArrayInputStream(
                Files.readAllBytes(Paths.get(sourceJavaUrl)))) {
            compilationUnit = JavaParser.parse(inputStream);
            //Visit the compilationUnit
            annotationBeanVisitor.visit(compilationUnit,null);
        }catch (Exception e){
            logger.error(e.toString());
        }
        //Return the list of beans inside the source class
        return annotationBeanVisitor.getListBeanClassTypes();
    }

    /*public List<String> detectBeansFromFilesList(List<String> filesListJavaSource) throws IOException, URISyntaxException {
        List<String> listBeanClassTypes = new ArrayList<>();

        for (String pathToJavaSource : filesListJavaSource) {
            listBeanClassTypes.addAll(detectBeans(pathToJavaSource));
        }

        return listBeanClassTypes;
    }*/
}
