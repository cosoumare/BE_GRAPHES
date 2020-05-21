package org.insa.graphs.algorithm.shortestpath;

import java.util.Iterator;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    //final int N = this.data.getGraph().size();
    final double inf = Double.POSITIVE_INFINITY;
    
    int nbnoeudmark=0;
    
    double time;
    
    @Override
    protected ShortestPathSolution doRun() {
    	
        final ShortestPathData data = getInputData();
        
          
        BinaryHeap<Label> Tas = new BinaryHeap<Label>();
        
        final int nbNodes = data.getGraph().size();


        //Tableau de labels pour tous les stocker?
        Label[] Tablab = new Label[nbNodes];
        
        //Initialisation de notre tas
        //Tous les coûts valent l'infini sauf celui de l'origine qui vaut 0 
 
        for (int i =0; i<nbNodes; i++) {
        	if (data.getGraph().get(i).equals(data.getOrigin())) {
                // On prévient les observers qu'on a l'origine
                notifyOriginProcessed(data.getOrigin());
        		Label lab =new Label(i, false, 0, null);
        		lab.setSommetC(data.getGraph().get(i).getId());
        		Tablab[i]=lab;
        		Tas.insert(lab);
        	}else {
        		Label lab =new Label(i, false, inf, null);
        		lab.setSommetC(data.getGraph().get(i).getId());  
        		Tablab[i]=lab;
        	}
        }
          
        boolean fin=false;
        
       //boucle tant que le sommet de destination n'est pas marqué
       while ((!(Tas.isEmpty())) && !fin) {   
    	   
    	//affiher si le tas est valide
    	if (Tas.isValid()) {
    		System.out.println("Le tas est valide!");
    	}else {
    	   	System.out.println("Oula le tas est pas valide!");
    	}
    	
   		//recherche élément min du tas et on marque ce sommet
   		Label x = Tas.deleteMin();
   		x.setMarque(true);
   		nbnoeudmark+=1;
   		
   		//on prévient que le noeud a été marqué
   		notifyNodeMarked(data.getGraph().get(x.getsommetC()));
   		
   		//on vérifie la liste des successeurs du sommet visité
   		System.out.println("on regarde les successeurs du noeud " + x.getsommetC());
   		
   		if (x==Tablab[data.getDestination().getId()]) {
   			fin=true;
   			System.out.println("on est a la fin");
   		}
   		
   		List<Arc> successeurs = data.getGraph().get(x.getsommetC()).getSuccessors();

   		//tests des coûts de tous les arcs successeurs
   		//Si pas de successeur
   		if (successeurs.isEmpty()) {
   			System.out.println("pas de successeurs");
   		}
   		   		
		for (Arc arc: successeurs) {
			
			if (!data.isAllowed(arc)) {
	   			System.out.println("trajet pas accessible voiture");
				continue;
			}
			
			double coutAct=Tablab[arc.getDestination().getId()].getCost();
   			//System.out.println("cout du noeud "+arc.getDestination().getId()+ " = "+coutAct);

			//on vérifie les coûts des arcs pour les sommets non marqués
			if (!(Tablab[arc.getDestination().getId()].getMarque())){
				
				if(coutAct>(x.getCost()+data.getCost(arc))){
					coutAct=(x.getCost()+data.getCost(arc));
					Tablab[arc.getDestination().getId()].setCost(coutAct);
					//Placer y dans le tas (enlever ancien label s'il y était déjà
					if (Tablab[arc.getDestination().getId()].getPere()!=null) {
						Tas.remove(Tablab[arc.getDestination().getId()]);
					}else {
						notifyNodeReached(arc.getDestination());
					}
		   			//System.out.println("on donne un pere au noeud "+arc.getDestination().getId());
					Tablab[arc.getDestination().getId()].setPere(arc);
					Tas.insert(Tablab[arc.getDestination().getId()]);					
				}
			}
		}
       }
       
       ShortestPathSolution solution = null;

       //si aucun prédécesseur pour destination 
       
       if (Tablab[data.getDestination().getId()].getPere()==null) {
    	   solution=new ShortestPathSolution(data, Status.INFEASIBLE);
  			System.out.println("pas de solution");

       }else {
    	   int nbarcs=0;
           //destination atteinte
           notifyDestinationReached(data.getDestination());
           System.out.println("on a atteint la destination");

           //création du path solution
           ArrayList<Arc> arcs = new ArrayList<>();
           Arc arcAct=Tablab[data.getDestination().getId()].getPere();
           arcs.add(arcAct);
           nbarcs+=1;
           
           while(Tablab[arcAct.getOrigin().getId()].getPere()!=null) {
               System.out.println("arc entre "+arcAct.getOrigin().getId() + " et "+arcAct.getDestination().getId());
               arcAct=Tablab[arcAct.getOrigin().getId()].getPere();
               arcs.add(arcAct);
               nbarcs+=1;
           }
           System.out.println("nombre d'arcs: "+ nbarcs);

           //on remet le chemin dans l'ordre
           Collections.reverse(arcs);
           
           //Vérification de la validité du path
           Path path =new Path(data.getGraph(), arcs);
           if (path.isValid()) {
               System.out.println("Le path est valide!");
           }
           
           //Vérification longueur du chemin=PCC Dijkstra  
           if (path.getLength()==(float)Tablab[data.getDestination().getId()].getCost()) {
               System.out.println("Le cout du path et le même que le cout du label de destination!");
           }
                      
           //nombre de noeuds marqués
  			System.out.println("Noeuds marqués: " + nbnoeudmark);
  			
  			//Durée du PCC 
  			time=path.getMinimumTravelTime();
  			System.out.println("Durée du trajet: " + time);
           
           //création de la solution
           solution = new ShortestPathSolution(data, Status.OPTIMAL, path);
        	   
        } 

       return solution;
              
}
}
    
/*Iterator<Arc> iterateur= successeurs.iterator();
Arc arc=iterateur.next();
while (iterateur.hasNext()) {
arc=iterateur.next();*/




