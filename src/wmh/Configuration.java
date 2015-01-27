package wmh;

import java.util.Vector;

//Struktura przechowuje zmienne globalne konfiguruj¹ce dzia³anie programu
public class Configuration 
{
	//sciezki: zrodlowa i docelowa
	public static String folderPath = "";
	public static String resultsPath = "";
	
	//lista plikow z grafami
	public static Vector<String> files = new Vector<String>();
	
	public static double pheromoneAttractiveness = 1;
	public static double weightAttractiveness =1;
	public static double pheromoneFadingRate = 0.5;
	public static double pheromoneQConstant = 1;
	public static double maxInitialPheromone = 0.02;
	
	public static int numberOfAnts = 300;
	public static long antTimeout = 500000000L;
	
	public static int maxEpochs = 2500;
	
	public static boolean debug = true;
	
	public static int repetitions = 100;
}
