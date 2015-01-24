import java.awt.Dimension;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.generators.random.BarabasiAlbertGenerator;
import edu.uci.ics.jung.algorithms.generators.random.EppsteinPowerLawGenerator;
import edu.uci.ics.jung.algorithms.generators.random.ErdosRenyiGenerator;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.shortestpath.BFSDistanceLabeler;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public class GraphGenerator<V, E> {
	private Factory<Graph<V, E>> graphFactory;
	private Factory<UndirectedGraph<V, E>> undirectedGraphFactory;
	private Factory<V> vertexFactory;
	private Factory<E> edgeFactory;
	
	//Generators acronyms
	public enum Generator {
        BA, EPL, ER; //KSW;
    }
	
	//Random graph generators
	private BarabasiAlbertGenerator<V, E> baGenerator;
	private EppsteinPowerLawGenerator<V, E> eplGenerator;
	private ErdosRenyiGenerator<V, E> erGenerator;
	
	//Constructor
	public GraphGenerator(Factory<V> vertexFactory, Factory<E> edgeFactory) {
		this.graphFactory = new GraphFactory<V, E>();
		this.undirectedGraphFactory = new UndirectedGraphFactory<V, E>();
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
	}
	
	//Graph generation (vertices with degree zero are removed)
	public Graph<V, E> generateGraph(Generator generator, 
			int numSeedVertices, Set<V> seedVertices, int numSteps, int numEdgesPerStep, 	//BarabasiAlbertGenerator
			int numVertices, int numEdges, int iterations,									//EppsteinPowerLawGenerator
			double probability) {															//ErdosRenyiGenerator
		
		Graph<V, E> testGraph;
		
		if(generator == Generator.BA) {
			this.baGenerator = new BarabasiAlbertGenerator<V, E>
			(graphFactory, vertexFactory, edgeFactory, numSeedVertices, numEdgesPerStep, seedVertices);
			this.baGenerator.evolveGraph(numSteps);
			testGraph = this.baGenerator.create();
		}
		
		else if(generator == Generator.EPL) {
			this.eplGenerator = new EppsteinPowerLawGenerator<V, E>
			(graphFactory, vertexFactory, edgeFactory, numVertices, numEdges, iterations);
			testGraph = this.eplGenerator.create();
		}
		
		else if(generator == Generator.ER) {
			this.erGenerator = new ErdosRenyiGenerator<V, E>
			(undirectedGraphFactory, vertexFactory, edgeFactory, numVertices, probability);
			testGraph = this.erGenerator.create();
		}
		
		else {
			return null;
		}
		
		Set<V> removeMe = new HashSet<V>();
		for(V v : testGraph.getVertices()) {
            if(testGraph.degree(v) == 0) {
                removeMe.add(v);
            }
        }
		for(V v : removeMe) {
			testGraph.removeVertex(v);
		}

		return testGraph;
	}
	
	public static void main(String[] args) {
		
		//Choose generator 
		Generator gen = Generator.ER;
		
		//BarabasiAlbertGenerator options
		int numSeedVertices = 10;
		Set<GeneratedVertex> seedVertices = new HashSet<GeneratedVertex>((int)(numSeedVertices / 0.75) + 1);
		int numEdgesPerStep = 4;
		int numSteps = 5;
		
		//EppsteinPowerLawGenerator options
		int numVertices = 20;	//ErdosRenyiGenerator option
		int numEdges = 20;
		int iterations = 10;
		
		//ErdosRenyiGenerator options
		double probability = 0.2;
		
		//Dijkstra results
		DistanceResults<GeneratedVertex, GeneratedEdge> dr = new DistanceResults<GeneratedVertex, GeneratedEdge>();
		
		//Create graph (vertices with degree zero are removed)
		VertexFactory vertexFactory = new VertexFactory();
		EdgeFactory edgeFactory = new EdgeFactory(0.0, 1.0);
		GraphGenerator<GeneratedVertex, GeneratedEdge> generator = 
				new GraphGenerator<GeneratedVertex, GeneratedEdge>(vertexFactory, edgeFactory);
		
		Graph<GeneratedVertex, GeneratedEdge> graph = generator.generateGraph
				(gen, numSeedVertices, seedVertices, numSteps, numEdgesPerStep, 
						numVertices, numEdges, iterations, probability);
		
		//Calculate distance from random vertex to the farthest vertex (hops distance)
		//Remove vertices unconnected to beginning vertex
		calculateDistance(graph, dr);
		
		//Write to a file
		writeGraph("test.txt", graph, dr);
		
		//Print results
		System.out.println(dr.beginVertex);
		System.out.println(dr.endVertex);
		System.out.println(dr.distance);
		System.out.println(dr.path);
		
		//Show graph
		Layout<GeneratedVertex, GeneratedEdge> layout = new CircleLayout<GeneratedVertex, GeneratedEdge>(graph);
		layout.setSize(new Dimension(600, 600));
		BasicVisualizationServer<GeneratedVertex, GeneratedEdge> vv = 
				new BasicVisualizationServer<GeneratedVertex, GeneratedEdge>(layout);
		vv.setPreferredSize(new Dimension(650, 650));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<GeneratedVertex>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<GeneratedEdge>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
        JFrame frame = new JFrame("Test Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
	
	private static Random random = new Random();
	
	public static class DistanceResults<V, E> {
		V beginVertex;
		V endVertex;
		double distance;
		List<E> path;

		public DistanceResults() {
			beginVertex = null;
			endVertex = null;
			distance = -1.0;
			path = null;
		}
	}
	
	//Calculates distance from random vertex to the farthest vertex (hops distance)
	//Removes vertices unconnected to beginning vertex
	public static void calculateDistance(Graph<GeneratedVertex, GeneratedEdge> graph, 
			DistanceResults<GeneratedVertex, GeneratedEdge> dr) {
		
		int beginId = random.nextInt(graph.getVertexCount());
		for(GeneratedVertex v : graph.getVertices()) {
			if(v.getId() == beginId) {
				dr.beginVertex = v;
			}
		}
		
		BFSDistanceLabeler<GeneratedVertex, GeneratedEdge> bdl = new BFSDistanceLabeler<GeneratedVertex, GeneratedEdge>();
		bdl.labelDistances(graph, dr.beginVertex);
		
		List<GeneratedVertex> vertices = bdl.getVerticesInOrderVisited();
		dr.endVertex = vertices.get(vertices.size() - 1);
		
		Set<GeneratedVertex> removeMe = bdl.getUnvisitedVertices();
		for(GeneratedVertex v : removeMe) {
			graph.removeVertex(v);
		}
		int vertexId = 0;
		for(GeneratedVertex v : graph.getVertices()) {
           v.setId(vertexId++);
        }
		
		final Transformer<GeneratedEdge, Double> wtTransformer = new Transformer<GeneratedEdge, Double>() {
        	public Double transform(GeneratedEdge link) {
        		return link.getWeight();
        	}
        };
        
        DijkstraShortestPath<GeneratedVertex, GeneratedEdge> alg = new DijkstraShortestPath<GeneratedVertex, GeneratedEdge>(graph, wtTransformer);
		dr.distance = (double) alg.getDistance(dr.beginVertex, dr.endVertex);
		dr.path = alg.getPath(dr.beginVertex, dr.endVertex);
	}
	
	//Writes graph to a file
	public static boolean writeGraph(String filename, Graph<GeneratedVertex, GeneratedEdge> graph, DistanceResults<GeneratedVertex, GeneratedEdge> dr) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");
		} catch (Exception e) {
			return false;
		}
		
		writer.println("n " + graph.getVertexCount() + " e " + graph.getEdgeCount());
		writer.println("d " + dr.beginVertex + " " + dr.endVertex + " " + dr.distance);
		
		writer.print("r ");
		for (GeneratedEdge e : dr.path) {
			writer.print("[" + graph.getEndpoints(e).getFirst() + " " + graph.getEndpoints(e).getSecond() + "] ");
		}
		writer.println("");
		
		for (GeneratedEdge e : graph.getEdges()) {
			writer.println("p " + graph.getEndpoints(e).getFirst() + " " + graph.getEndpoints(e).getSecond() + " " + e.getWeight());
		}
		
		writer.close();
		
		return true;
	}
	
}
