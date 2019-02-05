package fr.imt.ales.Detectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import fr.imt.ales.ArchitecturalGraph.ArchitecturalVertex;
import fr.imt.ales.Visitors.AnnotationAutowiredVisitor;
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

public class AutowiredComponentDetector {
    final static Logger logger = LogManager.getLogger(AutowiredComponentDetector.class);
    private AnnotationAutowiredVisitor annotationAutowiredVisitor;

    public AutowiredComponentDetector(){
        annotationAutowiredVisitor = new AnnotationAutowiredVisitor();
    }

    public List<ArchitecturalVertex> detectAutowiredComponents(String pathToJavaSource) throws URISyntaxException, IOException {
        //Clear the list of beans already visited
        //annotationAutowiredVisitor.clearListAutowiredComponentTypes();
        annotationAutowiredVisitor.cleararchitecturalVertexAutowiredList();

        URI sourceJavaUrl = new File(pathToJavaSource).toURI();

        //Parse the Java source file
        CompilationUnit compilationUnit;
        try (InputStream inputStream = new ByteArrayInputStream(
                Files.readAllBytes(Paths.get(sourceJavaUrl)))) {
            compilationUnit = JavaParser.parse(inputStream);
            //Visit the compilationUnit
            annotationAutowiredVisitor.visit(compilationUnit,null);
        }catch (Exception e){
            logger.error(e.toString());
        }
        //Return the list of beans inside the source class
        return annotationAutowiredVisitor.getarchitecturalVertexAutowiredList();
    }

//    public List<String> detectAutowiredComponentsFromFilesList(List<String> filesListJavaSource) throws IOException, URISyntaxException {
//        List<String> listBeanClassTypes = new ArrayList<>();
//
//        for (String pathToJavaSource : filesListJavaSource) {
//            listBeanClassTypes.addAll(detectAutowiredComponents(pathToJavaSource));
//        }
//
//        return listBeanClassTypes;
//    }
}
