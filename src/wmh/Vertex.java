package wmh;

import java.util.HashMap;
import java.util.Vector;

public class Vertex
{
	//numer porzadkowy
	public int idx;
	
	public Vector<Edge> edges;
	
	public Vertex(int idx)
	{
		this.edges = new Vector<Edge>();
		this.idx = idx;
	}
	
	@Override
	public String toString()
	{
		String res = "idx: " + idx;
		return res;
	}
}
