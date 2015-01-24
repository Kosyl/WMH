import org.apache.commons.collections15.Factory;

public class VertexFactory implements Factory<GeneratedVertex> {
	private long vertexCount;
	
	public VertexFactory() {
		vertexCount = 0;
	}
	
	public GeneratedVertex create() {
		return new GeneratedVertex(vertexCount++);
	}
}
