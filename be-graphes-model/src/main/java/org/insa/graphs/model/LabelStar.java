package org.insa.graphs.model;


public class LabelStar extends Label {

	private double coutDest;
	
	public LabelStar(int sommetC, boolean mark, double cost, Arc padre, double costDestin) {
		super(sommetC, mark, cost, padre);
		this.coutDest=costDestin;
	}
	
	
	@Override
	public double getCostTot() {
		return (this.getCost()+this.coutDest);		
	}
		
}
