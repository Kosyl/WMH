import java.util.Random;

import org.apache.commons.collections15.Factory;

public class EdgeFactory implements Factory<GeneratedEdge> {
	private double lowWeightLim;
	private double uppWeightLim;
	private Random random = new Random();

	public EdgeFactory(double lowWeightLim, double uppWeightLim) {
		this.lowWeightLim = lowWeightLim;
		this.uppWeightLim = uppWeightLim;
	}
	
	public GeneratedEdge create() {
		double weight = (uppWeightLim - lowWeightLim) * random.nextDouble() + lowWeightLim;
		return new GeneratedEdge(weight);
	}
}
