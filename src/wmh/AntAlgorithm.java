package wmh;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class AntAlgorithm 
{
	private Graph g;
	Ant[] ants;
	private Vertex voidVertex;
	int currentEpoch;
	long duration;
	Vector<Path> foundPaths;
	Vector<Integer> pathsInEpoch;
	
	public AntAlgorithm(Graph graph)
	{
		g = graph;
		ants = new Ant[Configuration.numberOfAnts];
		for(int i = 0; i < Configuration.numberOfAnts; ++i)
		{
			ants[i] = new Ant(i,g.foodIdx);
		}
		voidVertex = new Vertex(-1);
		currentEpoch = 0;
		
		foundPaths = new Vector<Path>();
		pathsInEpoch = new Vector<Integer>();
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
			aggregatePaths();
			end = checkEnd();
		}
		
		long stop = System.nanoTime();
		
		duration = stop-start;
		if(currentEpoch > Configuration.maxEpochs)
			currentEpoch = Configuration.maxEpochs;
		
		printSummary();
		
		return null;
	}
	
	private void printSummary()
	{
		System.out.println("####################################################");
		System.out.println("koniec");
		System.out.format("czas: %d\n",duration);
		System.out.format("liczba iteracji: %d\\%d\n",currentEpoch,Configuration.maxEpochs);
		System.out.println("znalezione sciezki:");
		for(Path path: foundPaths)
		{
			System.out.println(path.toString());
		}
		System.out.println("iloœæ œcie¿ek:");
		Iterator<Integer> i = pathsInEpoch.iterator();
		do
		{
			System.out.print(i.next());
			if(!i.hasNext())
				break;

			System.out.print(" -> ");
		}
		while(true);
	}

	private void aggregatePaths()
	{
		this.foundPaths.clear();
		for(Ant a: ants)
		{
			if(a.lost)
				continue;
			
			boolean isNewPath = true;
			for(Path path: foundPaths)
			{
				isNewPath = Path.isDifferent(path,a.path);
				if(!isNewPath)
				{
					path.numAnts++;
					break;
				}
			}
			if(isNewPath)
			{
				a.path.numAnts++;
				foundPaths.add(a.path);
			}
		}
		
		if(Configuration.debug)
		{
			for(Path path: foundPaths)
			{
				System.out.println(path.toString());
			}
		}
		
		pathsInEpoch.add(foundPaths.size());
	}


	private boolean checkEnd()
	{
		if(currentEpoch > Configuration.maxEpochs)
			return true;
		if(foundPaths.size() == 1)
			return true;
		return false;
	}

	private void finalizeEpoch()
	{
		g.finalizeEpoch();
		++currentEpoch;
		if(Configuration.debug)
			System.out.format("koniec epoki %d\n",currentEpoch);
	}

	private void leavePheromone()
	{
		for(Ant a: ants)
		{
			if(a.lost)
				continue;
			
			double pathCost = a.path.cost;
			for(Edge e: a.path.edges)
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
			ant.lost = false;
		}
	}
	
	private void findPaths()
	{
		long start;
		
		for(Ant ant:ants)
		{
			start = System.nanoTime();
			do
			{
				ant.takeStep();
			}
			while(ant.currentVertex.idx != g.nestIdx && (System.nanoTime()-start < Configuration.antTimeout));
			
			if(ant.currentVertex.idx != g.nestIdx)
			{
				ant.lost = true;
				if(Configuration.debug)
					System.out.format("X%dX ",ant.idx);
				continue;
			}
			ant.removeLoops();
			ant.path.refreshCost();
			if(Configuration.debug)
				System.out.format("%d ",ant.idx);
		}
		if(Configuration.debug)
			System.out.println();
	}
}
