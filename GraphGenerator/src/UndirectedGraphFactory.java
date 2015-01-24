import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class UndirectedGraphFactory<V, E> implements Factory<UndirectedGraph<V, E>> {
	
	public UndirectedGraph<V, E> create() {
		return UndirectedSparseGraph.<V, E>getFactory().create();
	}
}
