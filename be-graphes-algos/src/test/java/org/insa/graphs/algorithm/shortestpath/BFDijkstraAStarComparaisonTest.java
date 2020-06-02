

package org.insa.graphs.algorithm.shortestpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.GraphReader;
import org.junit.Test;


/**
 * jUnits Tests permettant de comparer les 3 algorithmes de recherche de chemin
 * 
 * TYPE 1 de test: Avec oracle, on s'appuie sur l'agorithme de BellmaFord déjà implémenté
 * 1) Comparaison en distance
 * 2) Comparaison en temps
 * 3) Comparaison en fonction des modes de déplacement
 * 
 */

/**
 * Set of parameters.
 * 1)Choix carte, donc graphe donné à lire (graphreader)
 * 2)1 origine, 1destination 
 * 3)Type comparaison = Temps ou distance
 * 4)on donne à chaque algorithme un data composé du graph, du noeud d'origine, du noeud de destination, et de l'arcInspector
 */

public class BFDijkstraAStarComparaisonTest {

	public ShortestPathData premierTest(String chemin_map, int origine, int destination, int type) {
	    Graph graph;
	    
		try {
			//ouverture du graphe correspondant à la carte spécifiée	
            DataInputStream stream= new DataInputStream(new BufferedInputStream(new FileInputStream(chemin_map)));   
            GraphReader reader=new BinaryGraphReader(stream);
	        graph = reader.read();
	        reader.close();
	    }
	    catch (Exception exception) {
	        exception.printStackTrace(System.out);
	        graph=null;
	    }
	
		//si on veut une comparaison avec le MODE_LENGHT type==1, et avec le MODE_TIME, type==3
		
		ArcInspector arcInspector=ArcInspectorFactory.getAllFilters().get(type);
		
		Node org=graph.get(origine);
		Node destin=graph.get(destination);
		
		org.insa.graphs.algorithm.shortestpath.ShortestPathData data=new ShortestPathData(graph, org, destin, arcInspector);
		
		return data;

	}
	

	public Path initPath(String chemin_map, int origine, int destination, int type, String Algo) {
		Path path=null;
		ShortestPathData donnee1=premierTest(chemin_map, origine, destination, type);
		if (Algo=="AlgoB") {
			BellmanFordAlgorithm AlgoB= new BellmanFordAlgorithm(donnee1);
			ShortestPathSolution SolutionB = AlgoB.doRun();
			path=SolutionB.getPath();

		}else if (Algo=="AlgoD") {
			DijkstraAlgorithm AlgoD= new DijkstraAlgorithm(donnee1);
			ShortestPathSolution SolutionD = AlgoD.doRun();
			path=SolutionD.getPath();
			
		}else if (Algo=="AlgoA") {
			AStarAlgorithm AlgoA=new AStarAlgorithm(donnee1);
			ShortestPathSolution SolutionA = AlgoA.doRun();
			path=SolutionA.getPath();
		}
			return path;
	}
	
	

	
	@Test
	public void testOracle() {
		
		//cas de path vide testé dans le cas sans oracle, ici surtout vérification de l'optimalité de la solution
		//PREMIERS TESTS AVEC ORACLE (BellmanFord)
		//Test1: carte Haute-Garonne, comparaison en taille
	
		Path pathB=initPath("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, 1, "AlgoB");
		Path pathD=initPath("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, 1, "AlgoD");
		Path pathA=initPath("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, 1, "AlgoA");
		
		//comparaison BellmanFord-Dijkstra
		assertEquals(pathB.getLength(), pathD.getLength(), 1e-6);
		//comparaison BellmanFord-AStar
		assertEquals(pathB.getLength(), pathA.getLength(),1e-6);
		//comparaison Dijkstra-AStar 
		assertEquals(pathD.getLength(), pathA.getLength(), 1e-6);

		
		//Test2: carte Haute-Garonne, comparaison en temps
		Path pathB2=initPath("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, 2, "AlgoB");
		Path pathD2=initPath("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, 2, "AlgoD");
		Path pathA2=initPath("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, 2, "AlgoA");
		
		//comparaison BellmanFord-Dijkstra
		assertEquals(pathB2.getMinimumTravelTime(), pathD2.getMinimumTravelTime(), 1e-6);	
		//comparaison BellmanFord-AStar
		assertEquals(pathB2.getMinimumTravelTime(), pathA2.getMinimumTravelTime(),1e-6);
		//comparaison Dijkstra-AStar 
		assertEquals(pathD2.getMinimumTravelTime(), pathA2.getMinimumTravelTime(), 1e-6);
	
	}
	
	public void testSansOracleInit(String chemin_map, int origine, int destination, String Algo) {
		
		//cas de path vide
		Path emptyPath=initPath(chemin_map, origine, destination, 1, Algo);
		if (emptyPath==null) {
	        assertEquals(emptyPath, null);
		}else {		
		//On initialise un path d'un mode et on vérifie que le résultat de ce mode est cohérent 
		Path path0=initPath(chemin_map, origine, destination, 0, Algo);		
		Path path1=initPath(chemin_map, origine, destination, 1, Algo);
		Path path2=initPath(chemin_map, origine, destination, 2, Algo);
		Path path3=initPath(chemin_map, origine, destination, 3, Algo);

		assertTrue("ShortestPath optimal", path0.getLength()<=path2.getLength()); //ou égal (cas de certains path d'un noeud)
		assertTrue("ShortestPath optimal", path1.getLength()<=path3.getLength());
		assertTrue("FastestPath optimal", path0.getMinimumTravelTime()>=path2.getMinimumTravelTime());
		assertTrue("FastestPath optimal", path1.getMinimumTravelTime()>=path3.getMinimumTravelTime());
		}
		
	}
	
	@Test
	public void testSansOracle() {
		
		//path impossible, path vide carte philippines
		testSansOracleInit("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\philippines.mapgr",731863,706554, "AlgoD");
		testSansOracleInit("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\philippines.mapgr",731863,706554, "AlgoA");
		
		//path d'un noeud carte carré
		testSansOracleInit("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\carre.mapgr",13,13, "AlgoD");
		testSansOracleInit("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\carre.mapgr",13,13, "AlgoA");		
		
		
		//path insa-bikini carte haute garonne
		testSansOracleInit("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, "AlgoD");
		testSansOracleInit("C:/Users\\coumba\\Documents\\insa\\3A\\Cours\\Graphes\\BE_GRAPHES\\CARTES\\haute-garonne.mapgr",10991,63104, "AlgoA");
		
		
	}

}
