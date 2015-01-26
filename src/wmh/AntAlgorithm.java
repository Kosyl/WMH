package wmh;

import java.util.Iterator;
import java.util.Vector;

public class AntAlgorithm 
{
	private Graph g;
	Ant[] ants;
	private Vertex voidVertex;
	int currentEpoch;
	double meanEpoch;
	long duration;
	Vector<Path> foundPaths;
	Path bestPath;
	Path bestPathInRun;
	double meanCost = 0.0;
	long meanDuration = 0;
	Vector<Double> pathsInEpoch;
	boolean error = false;
	
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
		pathsInEpoch = new Vector<Double>();
	}
	
	public RunResults calcBestPath()
	{
		this.bestPath = new Path();
		bestPath.cost = Double.MAX_VALUE;
		
		long start, stop;
		
		for(int i = 0; i < Configuration.repetitions; ++i)
		{
			do
			{
				g.reset();
				currentEpoch = 0;
				error = false;
				
				start = System.nanoTime();
				launchLoop();
				stop = System.nanoTime();
				
				duration = stop-start;
				if(currentEpoch > Configuration.maxEpochs)
					currentEpoch = Configuration.maxEpochs;
				
			}
			while(error);

			meanDuration += duration;
			meanEpoch += currentEpoch;
			processResults();
			
			System.out.format("%d ",i);
		}
		System.out.println();
		
		meanDuration/=Configuration.repetitions;
		meanCost/=Configuration.repetitions;
		meanEpoch /= Configuration.repetitions;
		for(int i = 0; i < pathsInEpoch.size(); ++i)
		{
			pathsInEpoch.set(i, pathsInEpoch.elementAt(i)/Configuration.repetitions);
		}
		if(Configuration.debug)
			printSummary();
		
		RunResults results = new RunResults();
		
		results.bestPossibleCost = g.bestPathCost;
		results.bestPossiblePath =(Vector) g.bestPath.clone();
		results.meanExecutionTimeInNs = meanDuration;
		results.foodIdx = g.foodIdx;
		results.nestIdx = g.nestIdx;
		results.n = g.n;
		results.q = g.q;
		results.numberOfAnts = Configuration.numberOfAnts;
		results.maxInitialPheromone = Configuration.maxInitialPheromone;
		results.bestFoundCost = bestPath.cost;
		results.meanFoundCost = meanCost;
		results.meanNumEpochs = meanEpoch;
		results.bestFoundPath = bestPath.toString();
		results.meanPathsInEpochs = (Vector) this.pathsInEpoch.clone();
		results.pheromoneAttractiveness = Configuration.pheromoneAttractiveness;
		results.pheromoneFadingRate = Configuration.pheromoneFadingRate;
		results.pheromoneQConstant = Configuration.pheromoneQConstant;
		results.weightAttractiveness = Configuration.weightAttractiveness;
		
		
		return results;
	}

	private boolean launchLoop()
	{
		boolean end = false;
		error = false;
		while(!end)
		{
			resetAnts();
			findPaths();
			if(error)
				break;
			evaporatePheromone();
			leavePheromone();
			finalizeEpoch();
			aggregatePaths();
			end = checkEnd();
		}
		return error;
	}
	
	private void processResults()
	{
		this.bestPathInRun = new Path();
		bestPathInRun.cost = Double.MAX_VALUE;
		
		for(Path p:foundPaths)
		{
			if(p.cost < bestPathInRun.cost)
				bestPathInRun = p;
		}
		
		if(bestPath.cost > bestPathInRun.cost)
		{
			bestPath = bestPathInRun.clone();
		}
		
		meanCost += bestPathInRun.cost;
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
		Iterator<Double> i = pathsInEpoch.iterator();
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
		if(pathsInEpoch.size() < currentEpoch)
		pathsInEpoch.add((double)foundPaths.size());
		else
		{
			pathsInEpoch.set(currentEpoch-1, pathsInEpoch.elementAt(currentEpoch-1)+(double)foundPaths.size());
		}
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
				//if(Configuration.debug)
					System.out.format("X%dX ",ant.idx);
				error = true;
				break;
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
