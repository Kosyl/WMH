package wmh;

public class AntAlgorithm 
{
	private Graph g;
	Ant[] ants;
	private Vertex voidVertex;
	int currentEpoch;
	
	public AntAlgorithm(Graph graph)
	{
		g = graph;
		ants = new Ant[Configuration.numberOfAnts];
		for(int i = 0; i < Configuration.numberOfAnts; ++i)
		{
			ants[i] = new Ant();
			ants[i].startIdx = g.foodIdx;
		}
		voidVertex = new Vertex(-1);
		currentEpoch = 0;
	}
	
	public GraphResults calcBestPath()
	{
		long start = System.nanoTime();
		
		g.reset();
		
		boolean end = false;
		
		while(!end)
		{
			resetAnts();
			findPaths();
			evaporatePheromone();
			leavePheromone();
			finalizeEpoch();
			end = checkEnd();
		}
		
		long stop = System.nanoTime();
		
		return null;
	}
	
	private boolean checkEnd()
	{
		if(currentEpoch > Configuration.maxEpochs)
			return true;
		
		return false;
	}

	private void finalizeEpoch()
	{
		g.finalizeEpoch();
		++currentEpoch;
	}

	private void leavePheromone()
	{
		for(Ant a: ants)
		{
			double pathCost = a.getPathCost();
			for(Edge e: a.path)
			{
				e.totalPheromone += Configuration.pheromoneQConstant / pathCost;
			}
		}
	}

	private void evaporatePheromone()
	{
		for(Edge e: g.edges)
		{
			e.totalPheromone = e.totalPheromone * (1-Configuration.pheromoneFadingRate);
		}
	}

	private void resetAnts()
	{
		for(Ant ant:ants)
		{
			ant.currentVertex = g.getFoodVertex();
			ant.path.clear();
			ant.previousVertex = voidVertex;
		}
	}
	
	private void findPaths()
	{
		for(Ant ant:ants)
		{
			do
			{
				ant.takeStep();
			}
			while(ant.currentVertex.idx != g.nestIdx);
			
			ant.removeLoops();
		}
	}
}
