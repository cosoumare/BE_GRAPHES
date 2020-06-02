package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insa.graphs.algorithm.AbstractMettingPointAlgorithm;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.LabelMeet;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class MeetingPointAlgorithm extends AbstractMettingPointAlgorithm<ShortestPathObserver> {

	protected MeetingPointAlgorithm(ShortestPathData data) {
        super(data);
    }

    //final int N = this.data.getGraph().size();
    final double inf = Double.POSITIVE_INFINITY;
    
    @Override
    public ShortestPathSolution[] run() {
        return (ShortestPathSolution[]) super.run();
    }

    @Override
    public ShortestPathData getInputData() {
        return (ShortestPathData) super.getInputData();
    }

    /**
     * Notify all observers that the origin has been processed.
     * 
     * @param node Origin.
     */
    public void notifyOriginProcessed(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyOriginProcessed(node);
        }
    }

    /**
     * Notify all observers that a node has been reached for the first time.
     * 
     * @param node Node that has been reached.
     */
    public void notifyNodeReached(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyNodeReached(node);
        }
    }

    /**
     * Notify all observers that a node has been marked, i.e. its final value has
     * been set.
     * 
     * @param node Node that has been marked.
     */
    public void notifyNodeMarked(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyNodeMarked(node);
        }
    }

    /**
     * Notify all observers that the destination has been reached.
     * 
     * @param node Destination.
     */
    public void notifyDestinationReached(Node node) {
        for (ShortestPathObserver obs: getObservers()) {
            obs.notifyDestinationReached(node);
        }
    }

    
    @Override
    protected ShortestPathSolution[] doRun() {
    	
        final ShortestPathData data = getInputData();
        
        BinaryHeap<LabelMeet> Tas1 = new BinaryHeap<LabelMeet>();
        BinaryHeap<LabelMeet> Tas2 = new BinaryHeap<LabelMeet>();
        
        List<Node> Liste_Milieux= new ArrayList<Node>();
        
        final int nbNodes = data.getGraph().size();
        
        //pour retenir le tas que l'on vient de visiter
        int TasVisite=0;

        //Tableaux de labels pour tous les stocker
        LabelMeet[] Tablab1 = new LabelMeet[nbNodes];
        LabelMeet[] Tablab2 = new LabelMeet[nbNodes];

        
        /**
         * 
         * --------------Initialisation de notre tas-------------------
         * Tous les coûts valent l'infini sauf celui de l'origine qui vaut 0 
         * 
         * */
 
        for (int i =0; i<nbNodes; i++) {
        	if (data.getGraph().get(i).equals(data.getOrigin())) {
        		
                // On prévient les observers qu'on a l'origine 1
                notifyOriginProcessed(data.getOrigin());
                
        		LabelMeet lab =new LabelMeet(i, false, false, 0, null);
        		lab.setSommetC(data.getGraph().get(i).getId());
        		Tablab1[i]=lab;
        		Tablab2[i]=lab;
        		Tas1.insert(lab);
        		
        	}else if (data.getGraph().get(i).equals(data.getDestination())) {
                // On prévient les observers qu'on a l'origine 2
                notifyOriginProcessed(data.getDestination());
                
        		LabelMeet lab =new LabelMeet(i, false, false, 0, null);
        		lab.setSommetC(data.getGraph().get(i).getId());
        		Tablab1[i]=lab;
        		Tablab2[i]=lab;
        		Tas2.insert(lab);
        		      	
        	}else{
        		LabelMeet lab =new LabelMeet(i, false, false, inf, null);
        		lab.setSommetC(data.getGraph().get(i).getId());  
        		Tablab1[i]=lab;
        		Tablab2[i]=lab;
        	}
        }
          
        
        /**
         * -----------------------Itérations-------------------------
         * boucle tant que le sommet de destination n'est pas marqué
         */
        
       boolean fin=false;
        
       while ((!(Tas1.isEmpty())) && (!(Tas2.isEmpty())) && !fin) {   
    	   
    	//affiher si le tas est valide
    	if ((Tas1.isValid()) && (Tas2.isValid())){
    		System.out.println("Les tas sont valides!");
    	}else {
    	   	System.out.println("Oula un tas n'est pas valide!");
    	}
    	
   		//recherche élément min du tas et on marque ce sommet
   		LabelMeet x1 = Tas1.findMin();
   		LabelMeet x2 = Tas2.findMin();
   		LabelMeet x= new LabelMeet(0, false, false, inf, null);
   		
   		if (x1.compareTo(x2)<=0) {
   			x =Tas1.deleteMin();
   	   		x.setMarque1(true);
   	   		TasVisite=1;
   		}else {
   			x =Tas2.deleteMin();
   	   		x.setMarque2(true);
   	   		TasVisite=2;
   		}

   		if (TasVisite==1) {
   		//on prévient que le noeud a été marqué
   	   		notifyNodeMarked(data.getGraph().get(x.getsommetC()));
   	   		
   	   		if (x==Tablab1[data.getDestination().getId()]) {
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
   				
   				double coutAct=Tablab1[arc.getDestination().getId()].getCost();
   	   			//System.out.println("cout du noeud "+arc.getDestination().getId()+ " = "+coutAct);

   				//on vérifie les coûts des arcs pour les sommets non marqués
   				if (!(Tablab1[arc.getDestination().getId()].getMarque1())){
   					
   					if(coutAct>(x.getCost()+data.getCost(arc))){
   						
   						coutAct=(x.getCost()+data.getCost(arc));
   						Tablab1[arc.getDestination().getId()].setCost(coutAct);
   						
   						//Placer y dans le tas (enlever ancien label s'il y était déjà
   						if (Tablab1[arc.getDestination().getId()].getPere()!=null) {
   							Tas1.remove(Tablab1[arc.getDestination().getId()]);	
   							
   						}else {
   							notifyNodeReached(arc.getDestination());
   						}
   						
   			   			//System.out.println("on donne un pere au noeud "+arc.getDestination().getId());
   						Tablab1[arc.getDestination().getId()].setPere(arc);
   						Tas1.insert(Tablab1[arc.getDestination().getId()]);					
   					}
   				}
   			}
   			
   			if (Tablab2[x.getsommetC()].getMarque2()) {
   				Liste_Milieux.add(data.getGraph().get(x.getsommetC()));
   			}
   	      } else if (TasVisite==2) {
   	    	  
   	   		//on prévient que le noeud a été marqué
     	   		notifyNodeMarked(data.getGraph().get(x.getsommetC()));
     	   		
     	   		if (x==Tablab2[data.getOrigin().getId()]) {
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
     				
     				double coutAct=Tablab2[arc.getDestination().getId()].getCost();
     	   			//System.out.println("cout du noeud "+arc.getDestination().getId()+ " = "+coutAct);

     				//on vérifie les coûts des arcs pour les sommets non marqués
     				if (!(Tablab2[arc.getDestination().getId()].getMarque2())){
     					
     					if(coutAct>(x.getCost()+data.getCost(arc))){
     						
     						coutAct=(x.getCost()+data.getCost(arc));
     						Tablab2[arc.getDestination().getId()].setCost(coutAct);
     						
     						//Placer y dans le tas (enlever ancien label s'il y était déjà
     						if (Tablab2[arc.getDestination().getId()].getPere()!=null) {
     							Tas2.remove(Tablab1[arc.getDestination().getId()]);	
     							
     						}else {
     							notifyNodeReached(arc.getDestination());
     						}
     						
     			   			//System.out.println("on donne un pere au noeud "+arc.getDestination().getId());
     						Tablab2[arc.getDestination().getId()].setPere(arc);
     						Tas2.insert(Tablab1[arc.getDestination().getId()]);					
     					}
     				}
     			}
     			
     			if (Tablab1[x.getsommetC()].getMarque1()) {
     				Liste_Milieux.add(data.getGraph().get(x.getsommetC()));
     			}
     	      }
   	       
   		}
   		
       ShortestPathSolution[] solution = null;

       //si aucun milieu pour destination 
       
       if (Liste_Milieux.isEmpty()) {
    	   ShortestPathSolution Nosolution=new ShortestPathSolution(data, Status.INFEASIBLE);
    	   solution[0]=Nosolution; 
  			System.out.println("pas de solution");

       }else {
           //destinations atteintes
    	   notifyDestinationReached(data.getOrigin());
           notifyDestinationReached(data.getDestination());
           System.out.println("on a trouvé tous les milieux");
           
           for (int i=0; i<Liste_Milieux.size();i++) {
        	   //création du path solution
        	   //Depuis O1
               ArrayList<Arc> arcsO1 = new ArrayList<>();
               Arc arcAct1=Tablab1[data.getDestination().getId()].getPere();
               arcsO1.add(arcAct1);

               while(Tablab1[arcAct1.getOrigin().getId()].getPere()!=null) {
                   //System.out.println("arc entre "+arcAct1.getOrigin().getId() + " et "+arcAct.getDestination().getId());
                   arcAct1=Tablab1[arcAct1.getOrigin().getId()].getPere();
                   arcsO1.add(arcAct1);
               }
               
               //on remet le chemin dans l'ordre pour coller avec le chemin depuis l'origine O2
               Collections.reverse(arcsO1);
              
               //Depuis O2
               ArrayList<Arc> arcsO2 = new ArrayList<>();
               Arc arcAct2=Tablab1[data.getDestination().getId()].getPere();
               arcsO1.add(arcAct2);

               while(Tablab1[arcAct2.getOrigin().getId()].getPere()!=null) {
                   //System.out.println("arc entre "+arcAct1.getOrigin().getId() + " et "+arcAct.getDestination().getId());
                   arcAct2=Tablab1[arcAct2.getOrigin().getId()].getPere();
                   arcsO2.add(arcAct2);
               }
               
               ArrayList<Arc> arcs = new ArrayList<>();
               arcs.addAll(arcsO1);
               arcs.addAll(arcsO2);
               //Concatenation du path final pour ce milieu
               Path path =new Path(data.getGraph(), arcs);
               
               //Vérification de la validité du path
               if (path.isValid()) {
                   System.out.println("Le path est valide!");
               }
               
               ShortestPathSolution chemin = new ShortestPathSolution(data, Status.OPTIMAL, path);
               
               solution[i]=chemin;
        	   
           }

       }       

       return solution;
    }
}
    

    //System.out.println("nombre d'arcs: "+ nbarcs);


    
    /*Vérification longueur du chemin=PCC Dijkstra  
    if (path.getLength()==(float)Tablab[data.getDestination().getId()].getCost()) {
        System.out.println("Le cout du path et le même que le cout du label de destination!");
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
 	 */  

