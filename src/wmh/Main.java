package wmh;

import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

//klasa glowna
public class Main
{
	// wyniki algorytmow
	Results results = new Results();

	// konstruktor parsuje plik konfiguracyjny
	Main(String plik)
	{
		try
		{
			readConfig(plik);
		} catch (Exception e)
		{
			System.out.println("Wyjatek: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void start()
	{
		int counter = 0;
		for (String filename : Configuration.files)
		{
			System.out.println(counter);
			++counter;
			Graph g = readGraph(filename);
			if (g != null)
			{
				//policzyc
			}
		}

		results.writeResults();
	}

	public static void sampleCheck()
	{
		Graph g = getSampleGraph();
		if (g != null)
		{
			GraphResults gRes = new GraphResults();
			gRes.bestPossibleCost = g.bestPathCost;
			gRes.filename = "sampleGraph.txt";
			gRes.n = g.n;
			gRes.q = g.q;

			new AntAlgorithm(g).calcBestPath();
		}
	}

	public void readConfig(String confPath)
	{
		try
		{
			FileReader fi = new FileReader(confPath);
			BufferedReader config = new BufferedReader(fi);
			String s = config.readLine();
			do
			{
				String data = s.split("=")[0];
				switch (data)
				{
				case "inputFolder":
					Configuration.folderPath = s.replaceFirst(data + "=", "");
					break;
				case "outputFile":
					Configuration.resultsPath = s.replaceFirst(data + "=", "");
					break;
				default:
					break;
				}
				s = config.readLine();
			} 
			while (s != null && !s.isEmpty());
			config.close();
			fi.close();
		} 
		catch (IOException e)
		{
			System.out.println("File input stream failure: " + e.getMessage());
		}

		// zapisujemy na liscie w Configuration wszystkie pliki z folderu z
		// grafami
		File folder = new File(Configuration.folderPath);
		File[] files = folder.listFiles();
		if (files != null)
		{
			for (final File fileEntry : files)
			{
				String path = fileEntry.getAbsolutePath();
				Configuration.files.add(path);
			}
		}
	}

	// wczytanie grafu z podanego pliku - DO POPRAWY
	public Graph readGraph(String confPath)
	{
		try
		{
			FileReader fi = new FileReader(confPath);
			BufferedReader config = new BufferedReader(fi);

			int q, n, vBegin, vEnd;
			double weight;
			String s = config.readLine();

			n = Integer.parseInt(s.split(" ")[3]);
			q = Integer.parseInt(s.split(" ")[4]);

			Graph g = new Graph(n);

			s = config.readLine();
			g.bestPathCost = Integer.parseInt(s.split(" ")[1]);

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
		} catch (IOException e)
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
		String config;
		config = "D:\\txt\\mvcconfig.txt";
		// config = arg[0];
		//Main program = new Main(config);
		//program.start();
		Main.sampleCheck();
	}
}