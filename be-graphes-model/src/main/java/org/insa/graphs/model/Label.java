package org.insa.graphs.model;

public class Label implements Comparable<Label>{
	
	//sommet courant : sommet ou num de sommet associé au label
	private int sommetCourant;
	
	//booléen vrai quand le coût min du sommet est connu par l'algorithme
	private boolean marque;
	
	//valeur courante du plus court chemin de l'origine vers le sommet
	private double cout;
	
	//arc entre sommet précédent sur le chemin correspondant au plus court chemin et noeud actuel
	private Arc pere;
	
	

	public Label(int sommetC, boolean mark, double cost, Arc padre) {
		this.sommetCourant=sommetC;
		this.marque=mark;
		this.cout=cost;
		this.pere=padre;		
	}
	
	@Override
    public int compareTo(Label other) {
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
	
    public boolean getMarque() {
    	return this.marque;
    }
    
    public void setMarque(boolean mark) {
    	this.marque=mark;
    }
    
    public Arc getPere() {
    	return this.pere;
    }
    
    public void setPere(Arc padre) {
    	this.pere=padre;
    }
}
