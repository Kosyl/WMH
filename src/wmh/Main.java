package wmh;

import java.awt.Dimension;
import java.io.*;
import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

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

	private void initTables()
	{
		double[] ww = {0.01,0.05,0.1,0.2,0.4,0.5,0.7,0.9,1.0,1.5,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0};
		//for(double w = 0.01; w < 10; w += 0.15)
		for(double w:ww)
		{
			checkedWeightAttr.add(w);
			checkedPheromoneAttr.add(w);
		}
		double[] rr = {0.999,0.99,0.95,0.9,0.8,0.6,0.5,0.4,0.3,0.2,0.1,0.05,0.01,0.001};
		//for(double r = 0.01; r < 1; r += 0.02)
		for(double r:rr)
		{
			checkedFadingRate.add(r);
		}
		int[] ii = {3,4,5,7,10,15,20,25,30,40,50,70,100};
		//for(int i = 1; i < 500; i += 5)
		for(int i:ii)
		{
			checkedNumAnts.add(i);
		}
	}

	public void startForConfigFile(String plik)
	{
		try
		{
			if(plik.isEmpty())
				return;
			readConfig(plik);
		} 
		catch (Exception e)
		{
			System.out.println("Wyjatek: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
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
										checkedNumAnts.indexOf(i),
										checkedNumAnts.size(),
										checkedWeightAttr.indexOf(w1),
										checkedWeightAttr.size(),
										checkedPheromoneAttr.indexOf(w2),
										checkedPheromoneAttr.size(),
										checkedFadingRate.indexOf(r),
										checkedFadingRate.size());
								checkGraph(g,graphPath);
							}
						}
					}
				}
				
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

	public void showGraph(Graph g) 
	{
		UndirectedSparseGraph<Integer, String> graph = new UndirectedSparseGraph<Integer, String>();
		for(Vertex v : g.vertices)
			graph.addVertex(v.idx);
		
		Integer id = 0;
		for(Edge e : g.edges) {
			graph.addEdge("["+id.toString() +"] "+ String.format("%.2f", e.weight), e.begin.idx, e.end.idx);
			id++;
		}
		
		Layout<Integer, String> layout = new CircleLayout<Integer, String>(graph);
		layout.setSize(new Dimension(600, 600));
		BasicVisualizationServer<Integer, String> vv = 
				new BasicVisualizationServer<Integer, String>(layout);
		vv.setPreferredSize(new Dimension(650, 650));
		
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Integer>());
		vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
		
        JFrame frame = new JFrame("Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	static public void main(String[] arg)
	{
		String config;
		config = "D:\\test.txt";
		// config = arg[0];
		
		Main program = new Main();
		program.singleGraph(config);
	}
}