package fr.imt.ales.ArchitecturalGraph;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedPseudograph;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArchitecturalGraph {
    private Graph<ArchitecturalVertex, DefaultEdge> architecturalDirectedGraph;
    private GraphExporter<ArchitecturalVertex, DefaultEdge> graphExporter;
    private ComponentNameProvider<ArchitecturalVertex> vertexLabelProvider;
    private ComponentNameProvider<ArchitecturalVertex> vertexIdProvider;
    private Writer writer;

    public ArchitecturalGraph(){
        architecturalDirectedGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
    }

    public void addArchitecturalVertex(ArchitecturalVertex architecturalVertex){
        architecturalDirectedGraph.addVertex(architecturalVertex);

        vertexIdProvider = ArchitecturalVertex::getComponentClassType;
        vertexLabelProvider = ArchitecturalVertex::getComponentName;
        Map<String, String> map =
                new LinkedHashMap<String, String>();
        map.put("shape","parallelogram");
        graphExporter = new DOTExporter<>(vertexIdProvider, vertexLabelProvider, null);
        writer = new StringWriter();
    }

    public void addArchitecturalVertex(AnnotationsEnum annotationType, AutowiringTypesEnum autowiringType, String componentClassType, String componentName){
        ArchitecturalVertex architecturalVertex = new ArchitecturalVertex(annotationType, autowiringType, componentClassType, componentName);
        architecturalDirectedGraph.addVertex(architecturalVertex);
    }

    public void addArchitecturalVerticesFromVertex(ArchitecturalVertex vertex, List<ArchitecturalVertex> vertexList){
        for (ArchitecturalVertex architecturalVertex : vertexList) {
            architecturalDirectedGraph.addVertex(architecturalVertex);
            addEdge(vertex,architecturalVertex);
        }
    }

    public void addArchitecturalVertices(List<ArchitecturalVertex> vertexList){
        for (ArchitecturalVertex architecturalVertex : vertexList) {
            architecturalDirectedGraph.addVertex(architecturalVertex);
        }
    }

    public void exportGraph() throws ExportException, IOException {
        graphExporter.exportGraph(architecturalDirectedGraph, writer);
        System.out.println(writer.toString());
        MutableGraph g = Parser.read(writer.toString());
        Graphviz.fromGraph(g).width(700).render(Format.PNG).toFile(new File("example/ex4-1.png"));

    }

    public void addEdge(ArchitecturalVertex av, ArchitecturalVertex av1){
        architecturalDirectedGraph.addEdge(av,av1);
    }

    public Graph<ArchitecturalVertex, DefaultEdge> getArchitecturalDirectedGraph() {
        return architecturalDirectedGraph;
    }
}
