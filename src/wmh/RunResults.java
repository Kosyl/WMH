package wmh;

import java.util.Vector;

//struktura z wynikami dla jednego grafu
public class RunResults 
{
	//nazwa pliku wejsciowego
	public String filename;
	
	//liczba wierzcholkow grafu
	public int n;
	
	//krawedzie grafu
	public int q;

	public int foodIdx;
	public int nestIdx;
	public double bestPossibleCost;
	public Vector<Integer> bestPossiblePath= new Vector<Integer>();
	
	public double pheromoneAttractiveness = 1.0;
	public double weightAttractiveness = 1.0;
	public double pheromoneFadingRate = 0.2;
	public double pheromoneQConstant = 10;
	public double maxInitialPheromone = 0.2;
	public int numberOfAnts = 30;
	
	public String bestFoundPath;
	public double bestFoundCost;
	public double meanFoundCost;
	public long meanExecutionTimeInNs;
	public int meanNumEpochs;

	public Vector<Integer> meanPathsInEpochs = new Vector<Integer>();
}
