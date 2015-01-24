package wmh;

import java.util.Iterator;
import java.util.LinkedList;

public class Path
{
	public LinkedList<Edge> edges = new LinkedList<Edge>();
	public double cost;
	public int numAnts;
	public int foodIdx;
	
	@Override
	public String toString()
	{
		int lastIdx = foodIdx;
		String res = "[" + foodIdx;
		for (Edge e : edges)
		{
			int newIdx = (e.begin.idx == lastIdx ? e.end.idx : e.begin.idx);
			res += " " + newIdx;
			lastIdx = newIdx;
		}
		res += "], koszt="+cost+", mrowki: "+numAnts;
		return res;
	}
	
	public void refreshCost()
	{
		double sum = 0.0;
		for (Edge e : edges)
		{
			sum += e.weight;
		}
		cost = sum;
	}

	public void clear()
	{
		edges.clear();
		cost = 0;
		numAnts = 0;
	}
	

	public static boolean isDifferent(Path path,Path path2)
	{
		if(path.edges.size() != path2.edges.size())
		return true;
		
		Iterator<Edge> i1 = path.edges.iterator(), i2 = path2.edges.iterator();
		while(i1.hasNext())
		{
			if(i1.next().idx != i2.next().idx)
				return true;
		}
		return false;
	}
}
