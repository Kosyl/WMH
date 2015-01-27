package wmh;

//Struktura przechowuje zmienne globalne konfiguruj¹ce dzia³anie programu
public class Configuration 
{
	public static String resultsPath = "";
	
	public static double pheromoneAttractiveness = 1.0;
	public static double weightAttractiveness =0.5;
	public static double pheromoneFadingRate = 0.5;
	public static double pheromoneQConstant = 1;
	public static double maxInitialPheromone = 0.02;
	
	public static int numberOfAnts = 30;
	public static long antTimeout = 500000000L;
	
	public static int maxEpochs = 2500;
	
	public static boolean debug = true;
	
	public static int repetitions = 50;
}
