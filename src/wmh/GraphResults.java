package wmh;

import java.util.Vector;

//struktura z wynikami dla jednego grafu
public class GraphResults 
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
	public Vector<Integer> bestPossiblePath;
	
	public double pheromoneAttractiveness = 1.0;
	public double weightAttractiveness = 1.0;
	public double pheromoneFadingRate = 0.2;
	public double pheromoneQConstant = 10;
	public double maxInitialPheromone = 0.2;
	public int numberOfAnts = 30;
	
	public Vector<Integer> foundPath;
	public double foundCost;
	public long executionTimeInNs;
	public int numEpochs;
}
