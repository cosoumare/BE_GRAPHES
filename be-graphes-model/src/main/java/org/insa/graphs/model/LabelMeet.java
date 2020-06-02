package org.insa.graphs.model;

public class LabelMeet implements Comparable<LabelMeet>{
	
	//sommet courant : sommet ou num de sommet associé au label
	private int sommetCourant;
	
	//booléen vrai quand le coût min du sommet est connu par l'algorithme dans le tableau 1
	private boolean marque1;
	//booléen vrai quand le coût min du sommet est connu par l'algorithme dans le tableau 2
	private boolean marque2;

	
	//valeur courante du plus court chemin de l'origine vers le sommet
	private double cout;
	
	//arc entre sommet précédent sur le chemin correspondant au plus court chemin et noeud actuel
	private Arc pere;
	

	public LabelMeet(int sommetC, boolean mark1, boolean mark2, double cost, Arc padre) {
		this.sommetCourant=sommetC;
		this.marque1=mark1;
		this.marque2=mark2;
		this.cout=cost;
		this.pere=padre;		
	}
	
	@Override
    public int compareTo(LabelMeet other) {
        return Double.compare(getCostTot(), other.getCostTot());
    }
	
    public int getsommetC() {
    	return this.sommetCourant;
    }
	
    public void setSommetC(int sommet) {
    	this.sommetCourant=sommet;
    }
    
	public double getCost() {
		return this.cout;		
	}
	
	public double getCostTot() {
		return this.cout;		
	}
		
	public void setCost(double cost) {
		this.cout=cost;		
	}
	
    public boolean getMarque1() {
    	return this.marque1;
    }
    
    public boolean getMarque2() {
    	return this.marque2;
    }
    
    public void setMarque1(boolean mark1) {
    	this.marque1=mark1;
    }
    
    public void setMarque2(boolean mark2) {
    	this.marque2=mark2;
    }
    
    public Arc getPere() {
    	return this.pere;
    }
    
    public void setPere(Arc padre) {
    	this.pere=padre;
    }
}

