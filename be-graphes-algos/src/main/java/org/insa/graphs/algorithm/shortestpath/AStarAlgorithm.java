package org.insa.graphs.algorithm.shortestpath;

import java.util.Iterator;

import org.insa.graphs.algorithm.AbstractInputData;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }
    //final int N = this.data.getGraph().size();
    final double inf = Double.POSITIVE_INFINITY;
    
    int nbnoeudmark=0;
    int nbnoeudvisit=0;

    
    double time=0;
    
    @Override
    protected ShortestPathSolution doRun() {
    	
        final ShortestPathData data = getInputData();
        
          
        BinaryHeap<Label> Tas = new BinaryHeap<Label>();
        
        final int nbNodes = data.getGraph().size();
        
        
        //Tableau de labels pour tous les stocker
        LabelStar[] Tablab = new LabelStar[nbNodes];
        
        /**
         * 
         * Dans le cas du MODE_TIME calcul de la vitesse pour déterminer l'heuristique
         * 
         */
        
        double speed=1;
        
        if ((this.data.getMode()== AbstractInputData.Mode.TIME)) {
        	
        	//2 types de vitesses autorisées: celle pour le graphe et celle pour les données
        	double graph_speed= this.data.getGraph().getGraphInformation().getMaximumSpeed();
        	double data_speed= this.data.getMaximumSpeed();
        	
        	//si aucune des vitesses n'est valide on met la vitesse à 130/3,6 m/s
        	speed=(double)(130/3.6);
        	
        	if ((graph_speed==-1)&&(data_speed!=-1)) {
        		speed=(double)(data_speed/3.6);
        		
        	}else if ((graph_speed!=-1)&&(data_speed==-1)) {
        		speed=(double)(graph_speed/3.6);
        		
        	//si les deux vitesses sont valides, on prend la vitesse minimale
        	}else if ((graph_speed!=-1)&&(data_speed!=-1)) {
        		speed=(double)(Math.min(data_speed, graph_speed)/3.6);
        	}
        }       
        
        /**
         * 
         * --------------Initialisation de notre tas-------------------
         * Tous les coûts valent l'infini sauf celui de l'origine qui vaut 0 
         * 
         * */
        
        for (int i =0; i<nbNodes; i++) {
        	if (data.getGraph().get(i).equals(data.getOrigin())) {
        		
                // On prévient les observers qu'on a l'origine
                notifyOriginProcessed(data.getOrigin());
                
                //on initialise notre Label d'origine
        		LabelStar lab =new LabelStar(i, false, 0, null,(data.getDestination().getPoint().distanceTo(data.getGraph().get(i).getPoint())/speed));
        		lab.setSommetC(data.getGraph().get(i).getId());
        		Tablab[i]=lab;
        		Tas.insert(lab);
        		
        	}else {
        		
        		//on initialise le reste de nos Labels
        		LabelStar lab =new LabelStar(i, false, inf, null,(data.getDestination().getPoint().distanceTo(data.getGraph().get(i).getPoint())/speed));
        		lab.setSommetC(data.getGraph().get(i).getId());  
        		Tablab[i]=lab;
        		
        	}
        }
                
        
        /**
         * -----------------------Itérations-------------------------
         * boucle tant que le sommet de destination n'est pas marqué
         */
        
       boolean fin=false;
  
       while ((!(Tas.isEmpty())) && !fin) {   	   
 
   		//recherche élément min du tas
   		Label x = Tas.deleteMin();
   		
   		//on marque ce sommet
   		x.setMarque(true);
   		
   		nbnoeudmark+=1;
        System.out.println("");
        System.out.println("on marque le noeud: "+x.getsommetC());
        System.out.println("son cout est: "+x.getCostTot());
        System.out.println("");
        
   		//on prévient que le noeud a été marqué
   		notifyNodeMarked(data.getGraph().get(x.getsommetC()));
   		System.out.println("on regarde les successeurs du noeud " + x.getsommetC());
   		
   		if (x==Tablab[data.getDestination().getId()]) {
   			fin=true;
   			System.out.println("on est a la fin");
   		}
   		
   		List<Arc> successeurs = data.getGraph().get(x.getsommetC()).getSuccessors();
   		
   		//tests des coûts de tous les arcs successeurs
		
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

			//on cherche le sommet d'arc de length min parmi les sommets non marqués
			if (!(Tablab[arc.getDestination().getId()].getMarque())){
				
				if(coutAct>(x.getCost()+data.getCost(arc))){
					
					coutAct=(x.getCost()+data.getCost(arc));
					Tablab[arc.getDestination().getId()].setCost(coutAct);
					
					//on met à jour l'heuristique
					Tablab[arc.getDestination().getId()].setCostTot(Tablab[arc.getDestination().getId()].getCost()+(data.getDestination().getPoint().distanceTo(data.getGraph().get(arc.getDestination().getId()).getPoint())/speed));
					
					//Placer y dans le tas (enlever ancien label s'il y était déjà
					if (Tablab[arc.getDestination().getId()].getPere()!=null) {
						Tas.remove(Tablab[arc.getDestination().getId()]);
						
					}else {
						notifyNodeReached(arc.getDestination());
					}
					
		   			System.out.println("on donne un pere au noeud "+arc.getDestination().getId());
					Tablab[arc.getDestination().getId()].setPere(arc);
					Tas.insert(Tablab[arc.getDestination().getId()]);
					nbnoeudvisit++;		
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
           
           // pour affichage de notre liste d'Arcs
           System.out.println("");
           System.out.println("Liste des Arcs du path:");


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
               System.out.println("");
               System.out.println("Le path est valide!");
           }
           
           //Vérification longueur du chemin=PCC Dijkstra  
           if (path.getLength()==(float)Tablab[data.getDestination().getId()].getCostTot()) {
               System.out.println("Le chemin est bien le plus court!");
           }
           
           //nombre de noeuds marqués
  			System.out.println("Noeuds marqués: " + nbnoeudmark);
  			
            //nombre de noeuds visités
   			System.out.println("Noeuds visités: " + nbnoeudvisit);


  			//Durée du PCC 
  			time=path.getMinimumTravelTime();
  			System.out.println("Durée du trajet: " + time);

           
           //création de la solution
           solution = new ShortestPathSolution(data, Status.OPTIMAL, path);
        	   
        } 

       return solution;
              
}
}
    
