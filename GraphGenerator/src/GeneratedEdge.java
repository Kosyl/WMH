public class GeneratedEdge {
	private double weight;
	
	public GeneratedEdge(double weight) {
		this.weight = weight;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
        this.weight = weight;
    }
	
	public String toString() {
		return String.format("%.2f", weight);
	}
}
