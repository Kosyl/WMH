package wmh;
import java.io.*;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//klasa glowna
public class Main
{
	// wyniki algorytmow
	ResultWriter results = new ResultWriter();
	LinkedList<RunResults> partialResults = new LinkedList<>();
	Vector<Integer> checkedNumAnts = new Vector<>();
	Vector<Double> checkedWeightAttr = new Vector<>();
	Vector<Double> checkedPheromoneAttr = new Vector<>();
	Vector<Double> checkedFadingRate = new Vector<>();

	// konstruktor parsuje plik konfiguracyjny
	Main()
	{
		initTables();
	}

	public boolean parseConfig(String confPath)
	{
		try
		{
			FileReader fi = new FileReader(confPath);
			BufferedReader config = new BufferedReader(fi);
			String s = config.readLine();
			do
			{
				String data = (s.split("=")[0]).trim();
				switch (data)
				{
				case "resultsPath":
					Configuration.resultsPath = (s.replaceFirst(data + "=", "")).trim();
					break;
				case "pheromoneAttractiveness":
					Configuration.pheromoneAttractiveness = Double.parseDouble((s.replaceFirst(data + "=", "")).trim());
					break;
				case "weightAttractiveness":
					Configuration.weightAttractiveness = Double.parseDouble((s.replaceFirst(data + "=", "")).trim());
					break;
				case "pheromoneFadingRate":
					Configuration.pheromoneFadingRate = Double.parseDouble((s.replaceFirst(data + "=", "")).trim());
					break;
				case "pheromoneQConstant":
					Configuration.pheromoneQConstant = Double.parseDouble((s.replaceFirst(data + "=", "")).trim());
					break;
				case "maxInitialPheromone":
					Configuration.maxInitialPheromone = Double.parseDouble((s.replaceFirst(data + "=", "")).trim());
					break;
				case "numberOfAnts":
					Configuration.numberOfAnts = Integer.parseInt((s.replaceFirst(data + "=", "")).trim());
					break;
				case "antTimeoutInNs":
					Configuration.antTimeout = Long.parseLong((s.replaceFirst(data + "=", "")).trim());
					break;
				case "maxCycles":
					Configuration.maxEpochs = Integer.parseInt((s.replaceFirst(data + "=", "")).trim());
					break;
				case "debug":
					Configuration.debug = Boolean.parseBoolean((s.replaceFirst(data + "=", "")).trim());
					break;
				case "repetitions":
					Configuration.repetitions = Integer.parseInt((s.replaceFirst(data + "=", "")).trim());
					break;
				default:
					break;
				}
				s = config.readLine();
			} 
			while (s != null);
			config.close();
			fi.close();
		} 
		catch (IOException e)
		{
			System.out.println("File input stream failure: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private void initTables()
	{
		double[] wwp = {0.5};//beta
		for(double w:wwp)
		{
			checkedWeightAttr.add(w);
		}
		double[] wwe =  {1};//alpha
		for(double w:wwe)
		{
			checkedPheromoneAttr.add(w);
		}
		double[] rr = {0.5};
		for(double r:rr)
		{
			checkedFadingRate.add(r);
		}
		int[] ii = {20};
		for(int i:ii)
		{
			checkedNumAnts.add(i);
		}
	}

	public void sampleCheck()
	{
		Graph g = getSampleGraph();
		if (g != null)
		{
			RunResults gRes = new RunResults();
			gRes.bestPossibleCost = g.bestPathCost;
			gRes.filename = "sampleGraph.txt";
			gRes.n = g.n;
			gRes.q = g.q;

			new AntAlgorithm(g).calcBestPath();
		}
	}
	
	public void singleGraph(String graphPath)
	{
		try
		{
			Graph g = readGraph(graphPath);
			Configuration.debug = false;
			
			//showGraph(g);
			if (g != null)
			{
				for(int i: checkedNumAnts)
				{
					Configuration.numberOfAnts = i;
					for(double w1: checkedWeightAttr)
					{
						Configuration.weightAttractiveness = w1;
						for(double w2: checkedPheromoneAttr)
						{
							Configuration.pheromoneAttractiveness = w2;
							for(double r: checkedFadingRate)
							{
								Configuration.pheromoneFadingRate = r;
								
								System.out.format("%d/%d %d/%d %d/%d %d/%d\n",
										checkedNumAnts.indexOf(i)+1,
										checkedNumAnts.size(),
										checkedWeightAttr.indexOf(w1)+1,
										checkedWeightAttr.size(),
										checkedPheromoneAttr.indexOf(w2)+1,
										checkedPheromoneAttr.size(),
										checkedFadingRate.indexOf(r)+1,
										checkedFadingRate.size());
								checkGraph(g,graphPath);
							}
						}
					}
				}

				ResultWriter writer = new ResultWriter();
				//writer.writeResults(partialResults);
				//writer.writePathsInEpochs(partialResults);
				writer.writeAlfaBeta2D(partialResults, checkedWeightAttr,checkedPheromoneAttr);
			}
		} 
		catch (Exception e)
		{
			System.out.println("Wyjatek: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void singleGraphFromConfig(String graphPath)
	{
		try
		{
			Graph g = readGraph(graphPath);
			
			//showGraph(g);
			if (g != null)
			{
				checkGraph(g,graphPath);
				
				ResultWriter writer = new ResultWriter();
				writer.writeResults(partialResults);
				//writer.writePathsInEpochs(partialResults);
				//writer.writeAlfaBeta2D(partialResults, checkedWeightAttr,checkedPheromoneAttr);
			}
		} 
		catch (Exception e)
		{
			System.out.println("Wyjatek: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void checkGraph(Graph g,String graphPath)
	{		
		RunResults gRes = new AntAlgorithm(g).calcBestPath();
		gRes.filename = graphPath;
		
		partialResults.add(gRes);
	}

	public void singleFolder(String confPath)
	{
		File folder = new File(confPath);
		File[] files = folder.listFiles();
		if (files != null)
		{
			for (final File fileEntry : files)
			{
				String path = fileEntry.getAbsolutePath();
				singleGraph(path);
			}

			ResultWriter writer = new ResultWriter();
			writer.writeResults(partialResults);
			writer.writePathsInEpochs(partialResults);
		}
	}

	// wczytanie grafu z podanego pliku - DO POPRAWY
	public Graph readGraph(String confPath)
	{
		try
		{
			FileReader fi = new FileReader(confPath);
			BufferedReader config = new BufferedReader(fi);

			int q, n, vBegin, vEnd, v1, v2, last;
			double weight;
			String s = config.readLine();

			n = Integer.parseInt(s.split(" ")[1]);
			q = Integer.parseInt(s.split(" ")[3]);

			Graph g = new Graph(n);

			s = config.readLine();
			g.bestPathCost = Double.parseDouble(s.split(" ")[3]);
			g.foodIdx = Integer.parseInt(s.split(" ")[1]);
			g.nestIdx = Integer.parseInt(s.split(" ")[2]);
			g.bestPath.add(g.foodIdx);

			s = config.readLine();
			
			Pattern pair = Pattern.compile("\\[[0-9]+ [0-9]+\\]");
			 Matcher m = pair.matcher(s);
			 while(m.find())
			 {
				 String edge = m.group();
				 edge = edge.substring(1, edge.length()-1);
				 v1 = Integer.parseInt(edge.split(" ")[0]);
				 v2 = Integer.parseInt(edge.split(" ")[1]);
				 last = g.bestPath.lastElement();
				 g.bestPath.add(v1 == last? v2 : v1);
			 }
			 
			for (int i = 0; i < q; ++i)
			{
				s = config.readLine();

				vBegin = Integer.parseInt(s.split(" ")[1]);
				vEnd = Integer.parseInt(s.split(" ")[2]);
				weight = Double.parseDouble(s.split(" ")[3]);
				g.addEdge(vBegin, vEnd, weight);
			}

			config.close();
			fi.close();

			return g;
		} 
		catch (IOException e)
		{
			System.out.println("File input stream failure: " + e.getMessage());
		}
		return null;
	}

	public static Graph getSampleGraph()
	{
		Graph g = new Graph(7);

		g.addEdge(0, 1, 0.3);
		g.addEdge(0, 2, 0.4);
		g.addEdge(1, 3, 0.3);
		g.addEdge(2, 3, 0.4);
		g.addEdge(1, 4, 0.2);
		g.addEdge(4, 5, 0.2);
		g.addEdge(5, 1, 0.2);

		g.nestIdx = 3;
		g.foodIdx = 0;

		g.bestPathCost = 6.0;

		return g;
	}
	
	static public void main(String[] arg)
	{
		if(arg.length != 2)
		{
			System.out.format("uzycie:\njava -jar nazwa_programu.jar sciezka_konfiguracji sciezka_grafu\n","");
			return;
		}
		Main program = new Main();
		
		String config = arg[0];
		
		if(!program.parseConfig(config))
		{
			System.out.format("Nieprawid³owy plik konfiguracji.\n");
			return;
		}
		
		program.singleGraphFromConfig(arg[1]);
		//program.singleFolder(config);
	}
}