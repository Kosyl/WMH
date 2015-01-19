package wmh;

public class Edge 
{
	public int idx;
	public Vertex begin;
	public Vertex end;
	public double weight;
	public double totalPheromone;
	public double effectiveAttractiveness = 0.0;
	
	@Override
	public String toString()
	{
		String res = begin.idx + " - " + end.idx + ", weight: " + weight + ", feromon: " + totalPheromone + ", attr: " + effectiveAttractiveness;
		return res;
	}
	
	public Edge(int idx, Vertex begin, Vertex end, double weight)
	{
		this.idx = idx;
		this.begin = begin;
		this.end = end;
		this.weight = weight;
	}
	
	public void reset()
	{
		totalPheromone = 0.0;
	}
	
	public void updateAttractiveness()
	{
		effectiveAttractiveness = Math.pow( totalPheromone, Configuration.pheromoneAttractiveness) * Math.pow(getAttractiveness(),Configuration.weightAttractiveness);
	}
	
	private double getAttractiveness()
	{
		return 1.0/weight;
	}
}
