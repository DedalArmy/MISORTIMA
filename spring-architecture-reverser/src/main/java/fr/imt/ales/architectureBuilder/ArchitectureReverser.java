package fr.imt.ales.architectureBuilder;

import fr.imt.ales.ArchitecturalGraph.ArchitecturalGraph;
import fr.imt.ales.ArchitecturalGraph.ArchitecturalVertex;
import fr.imt.ales.Detectors.AutowiredComponentDetector;
import fr.imt.ales.Detectors.BeanComponentDetector;
import org.jgrapht.graph.DefaultEdge;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ArchitectureReverser {
    private List<String> javaSourceFilesList;
    private List<String> javaComponentFilesList;
    private List<String> javaBeanFilesList;

    private AutowiredComponentDetector autowiredComponentDetector;
    private BeanComponentDetector beanComponentDetector;

    private ArchitecturalGraph architecturalGraph;

    public ArchitectureReverser(List<String> javaSourceFilesList, List<String> javaComponentFilesList, List<String> javaBeanFilesList) {
        this.javaSourceFilesList = javaSourceFilesList;
        this.javaComponentFilesList = javaComponentFilesList;
        this.javaBeanFilesList = javaBeanFilesList;
        architecturalGraph = new ArchitecturalGraph();

        autowiredComponentDetector = new AutowiredComponentDetector();
        beanComponentDetector = new BeanComponentDetector();
    }

    public void initSubGraphWithBean(String javaSourceFileBeanContainer) throws IOException, URISyntaxException {
        List<ArchitecturalVertex> beanVertexList = beanComponentDetector.detectBeans(javaBeanFilesList.get(0));
        System.out.println(beanVertexList);
        System.out.println("BEAN : " + beanVertexList.get(0));

        //get Autowired from component A
        //System.out.println(autowiredComponentVertexList.toString());
        //System.out.println(autowiredComponentVertexList.get(1).getComponentClassType());
        //autowiredComponentVertexList
        //architecturalGraph.

        for (ArchitecturalVertex architecturalBeanVertex : beanVertexList) {
            List<ArchitecturalVertex> autowiredComponentVertexList = autowiredComponentDetector.detectAutowiredComponents(
                    findJavaSourceByClassName(architecturalBeanVertex.getComponentClassType()));
            architecturalGraph.addArchitecturalVertex(architecturalBeanVertex);
            createComponentGraph(architecturalGraph,architecturalBeanVertex,autowiredComponentVertexList);
        }

        System.out.println(architecturalGraph.toString());
    }

    public void createComponentGraph(ArchitecturalGraph architecturalGraph,
                                     ArchitecturalVertex vertex,
                                     List<ArchitecturalVertex> componentAutowiredVertexList) throws IOException, URISyntaxException {
        architecturalGraph.addArchitecturalVerticesFromVertex(vertex, componentAutowiredVertexList);
        vertex.setDiscovered(true);
        for (DefaultEdge defaultEdge : architecturalGraph.getArchitecturalDirectedGraph().edgesOf(vertex)) {
            ArchitecturalVertex currentVertex = architecturalGraph.getArchitecturalDirectedGraph().getEdgeTarget(defaultEdge);
            if(!currentVertex.getDiscovered()){
                List<ArchitecturalVertex> lac = autowiredComponentDetector.detectAutowiredComponents(findJavaSourceByClassName(currentVertex.getComponentClassType()));
                createComponentGraph(architecturalGraph,currentVertex,lac);
            }
        }
    }

    //public void printGraph()

    public String findJavaSourceByClassName(String className){
        for (String javaFile : javaSourceFilesList) {
            if(javaFile.contains(className)){
                return javaFile;
            }
        }
        return null;
    }

    public List<String> getJavaSourceFilesList() {
        return javaSourceFilesList;
    }

    public void setJavaSourceFilesList(List<String> javaSourceFilesList) {
        this.javaSourceFilesList = javaSourceFilesList;
    }

    public List<String> getJavaComponentFilesList() {
        return javaComponentFilesList;
    }

    public void setJavaComponentFilesList(List<String> javaComponentFilesList) {
        this.javaComponentFilesList = javaComponentFilesList;
    }

    public List<String> getJavaBeanFilesList() {
        return javaBeanFilesList;
    }

    public void setJavaBeanFilesList(List<String> javaBeanFilesList) {
        this.javaBeanFilesList = javaBeanFilesList;
    }

    public AutowiredComponentDetector getAutowiredComponentDetector() {
        return autowiredComponentDetector;
    }

    public void setAutowiredComponentDetector(AutowiredComponentDetector autowiredComponentDetector) {
        this.autowiredComponentDetector = autowiredComponentDetector;
    }

    public BeanComponentDetector getBeanComponentDetector() {
        return beanComponentDetector;
    }

    public void setBeanComponentDetector(BeanComponentDetector beanComponentDetector) {
        this.beanComponentDetector = beanComponentDetector;
    }

    public ArchitecturalGraph getArchitecturalGraph() {
        return architecturalGraph;
    }

    public void setArchitecturalGraph(ArchitecturalGraph architecturalGraph) {
        this.architecturalGraph = this.architecturalGraph;
    }
}
