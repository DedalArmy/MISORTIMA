import fr.imt.ales.ArchitecturalGraph.AnnotationsEnum;
import fr.imt.ales.ArchitecturalGraph.ArchitecturalVertex;
import fr.imt.ales.ArchitecturalGraph.AutowiringTypesEnum;
import fr.imt.ales.Detectors.AutowiredComponentDetector;
import fr.imt.ales.Detectors.BeanComponentDetector;
import fr.imt.ales.architectureBuilder.ArchitectureReverser;
import org.jgrapht.io.ExportException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ProcessBuilder processBuilder_1 = new ProcessBuilder();
        ProcessBuilder processBuilder_2 = new ProcessBuilder();
        ProcessBuilder processBuilder_3 = new ProcessBuilder();


        // -- Linux --

        // Run a shell command
        //processBuilder.command("bash", "-c", "find /home/quentin/Desktop/test-files -name '*.java' -type f -print -not -path \"*/test/*\" | grep -wo \"@Bean\"");
//grep -rnw '/path/to/somewhere/' -e 'pattern'
        processBuilder_1.command("bash", "-c", "grep --include=*.java --exclude-dir={test,target} -rnwl '/home/quentin/IdeaProjects/ExpeAutowiredBehaviour' -e '@Bean'");
        processBuilder_3.command("bash", "-c", "find /home/quentin/IdeaProjects/ExpeAutowiredBehaviour -name '*.java' -type f -print -not -path \"*/test/*\"");

        //processBuilder.command("bash", "-c", "grep --include=*.xml --exclude-dir={test,target} -rnwl '/home/quentin/Desktop/test-files' -e '<bean'");
        processBuilder_2.command("bash", "-c", "grep --include=*.java --exclude-dir={test,target} -rnwl '/home/quentin/IdeaProjects/ExpeAutowiredBehaviour' -e '@Component' -e '@Repository' -e '@Service' -e '@Controller' -e '@RestController'");

        try {
            List<String> listJavaSourceFiles = handlerProcessBuilder(processBuilder_1);
            List<String> listJavaComponents = handlerProcessBuilder(processBuilder_2);
            List<String> listJavaFiles = handlerProcessBuilder(processBuilder_3);

            AutowiredComponentDetector autowiredComponentDetector = new AutowiredComponentDetector();
            BeanComponentDetector beanComponentDetector = new BeanComponentDetector();

            ArchitectureReverser architectureReverser = new ArchitectureReverser(listJavaFiles,listJavaComponents,listJavaSourceFiles);
            architectureReverser.initSubGraphWithBean("");
            architectureReverser.getArchitecturalGraph().addArchitecturalVertex(
                    AnnotationsEnum.BEAN,
                    AutowiringTypesEnum.FIELD,
                    "rtest",
                    "tests"
            );
            architectureReverser.getArchitecturalGraph().exportGraph();
            //architectureReverser.createComponentGraph();

        } catch (IOException | URISyntaxException | ExportException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*public static void detectAndCreateComponentGraph(ArchitecturalGraph architecturalSubGraph, ArchitecturalVertex vertex, List<ArchitecturalVertex> listComponentAutowired, AutowiredComponentDetector autowiredComponentDetector){
        architecturalSubGraph.addArchitecturalVerticesFromVertex(listComponentAutowired);
        vertex.setDiscovered(true);
        for (DefaultEdge defaultEdge : architecturalSubGraph.getArchitecturalDirectedGraph().edgesOf(vertex)) {
            ArchitecturalVertex currentVertex = architecturalSubGraph.getArchitecturalDirectedGraph().getEdgeTarget(defaultEdge);
            if(!currentVertex.getDiscovered()){
                List<ArchitecturalVertex> lac = autowiredComponentDetector.detectAutowiredComponents(listJavaSourceFiles.get(0));
                detectAndCreateComponentGraph(architecturalSubGraph,currentVertex);
            }
        }*/
/*
* explorer(graphe G, sommet s)
      marquer le sommet s
      afficher(s)
      pour tout sommet t fils du sommet s
            si t n'est pas marqué alors
                   explorer(G, t);*/


    public static List<ArchitecturalVertex> convertListAutowiredCompoentToVertex(List<String> listC){
        List<ArchitecturalVertex> architecturalVertexList = new ArrayList<>();
        for (String c : listC) {
            architecturalVertexList.add(new ArchitecturalVertex(
                    AnnotationsEnum.AUTOWIRED,
                    AutowiringTypesEnum.FIELD,
                    c,"")
            );
        }
        return architecturalVertexList;
    }

    public static String findJavaPathFileByClassName(String className, List<String> listJavaFiles){
        for (String javaFile : listJavaFiles) {
            if(javaFile.contains(className)){
                return javaFile;
            }
        }
        return null;
    }

    public static void deleteBeansInCompo(List<String> listBeans, List<String> listComponents){
        for (String beanClassType : listBeans) {
            for (String listComponent : listComponents) {
                if (listComponent.contains(beanClassType)){
                    listComponents.remove(listComponent);
                    break;
                }
            }
        }

    }


    public static List<String> handlerProcessBuilder(ProcessBuilder processBuilder) throws IOException, InterruptedException {

        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        List<String> listFiles = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            listFiles.add(line);
            output.append(line).append("\n");
        }

        int exitVal = process.waitFor();
        if (exitVal == 0) {
            System.out.println("Success!");
            //System.out.println(output);
            //System.exit(0);
        } else {
            //abnormal...
        }
        return listFiles;
    }
}
