package wmh;

import java.util.Random;
import java.util.Vector;

public class Graph 
{
	public Vertex[] vertices;
	public Vector<Edge> edges;
	
	//wierzcholki
	public int n;
	public int foodIdx;
	public int nestIdx;
	
	//krawedzie
	public int q;
	
	public double bestPathCost;
	public Vector<Integer> bestPath;
	
	public Graph(int numVertices)
	{
		bestPathCost = -1;
		bestPath = new Vector<Integer>();
		n=numVertices;
		q=0;
		
		vertices = new Vertex[numVertices];
		edges = new Vector<Edge>();
		
		if(numVertices>0)
		{
			for(int i = 0;i<numVertices;++i)
			{
				vertices[i]=new Vertex(i);
			}
		}
	}
	
	public void addEdge(int v1, int v2, double weight)
	{
		Vertex begin = this.vertices[v1], end = this.vertices[v2];
		Edge e = new Edge(edges.size(),begin,end,weight);
		
		begin.edges.add(e);
		end.edges.add(e);
		
		edges.add(e);
		++q;
	}
	
	public Vertex getFoodVertex()
	{
		return vertices[foodIdx];
	}
	
	public Vertex getNestVertex()
	{
		return vertices[nestIdx];
	}
	
	public void reset()
	{
		Random r = new Random();
		for(Edge e:edges)
		{
			e.reset();
			e.totalPheromone = r.nextDouble()*Configuration.maxInitialPheromone;
			e.updateAttractiveness();
		}
	}
	
	public void finalizeEpoch()
	{
		for(Edge e:edges)
		{
			e.updateAttractiveness();
		}
	}
}
