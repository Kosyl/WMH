import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class GraphFactory<V, E> implements Factory<Graph<V, E>> {
	
	public Graph<V, E> create() {
		return UndirectedSparseGraph.<V, E>getFactory().create();
	}
}
