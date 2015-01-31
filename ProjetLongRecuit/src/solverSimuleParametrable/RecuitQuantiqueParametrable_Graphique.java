package solverSimuleParametrable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import solver.GrapheColorie;
import solver.ListEnergie;
import solverCommun.Etat;
import solverCommun.MutationElementaire;
import solverCommun.Probleme;

public class RecuitQuantiqueParametrable_Graphique extends RecuitSimuleP { 

	// creer vos propres Temperature, ConstanteK et trucs pour les graphes
	public Temperature Gamma;
	public ConstanteK K;
	public double meilleureEnergie = Double.MAX_VALUE;									// en soit, nos energies pourraient etre des Int, mais bon 
	public double temperature;
	public int nbMaxIteration; 							// nombre maximale d'iteration si la solution n'est pas trouvee, redondance avec t.nbIteration
	public int palier;
	// abstract void init(); 				        	// initialisation // mais de quoi ?

	public RecuitQuantiqueParametrable_Graphique(Temperature Gamma, ConstanteK K, int palier, double temperature) {
		this.Gamma=Gamma;												// contructeur : on lui donne la facon de calculer l'energie, K et tout le blabla
		this.K=K;												// en creant une classe dedie et reutilisable qui extends temperature
		this.nbMaxIteration=this.Gamma.nbIteration;						// ainsi on combine le tout facilement
		this.palier = palier;		
		this.temperature = temperature;						//en quantique, la temp�rature est constante et le Gamma est variable, d'o� le fait que Gamma soit une "temp�rature"
	}





	public Probleme lancer(Probleme probleme) { return probleme;};



	public Probleme lancer(Probleme probleme, ListEnergie[] listeEnergie, ListEnergie[] listeProba, ListEnergie listeMeilleureEnergie, ListEnergie listeValeursJr,ListEnergie listeRapport) {

		int nombreRepliques = probleme.etats.length;
		double[] vectProbaPas1= new double[nombreRepliques];
		
		for (int j=0;j<nombreRepliques;j++) {
			vectProbaPas1[j]=0;
		}

		Etat etat = probleme.etats[0];
		Etat previous = probleme.etats[nombreRepliques-1];
		Etat next = probleme.etats[1];
		for (int i = 0; i < nombreRepliques; i++){	// initialisation de meilleureEnergie
			double energie = probleme.etats[i].Ep.calculer(probleme.etats[i]) ;
			if (energie < this.meilleureEnergie){
				this.meilleureEnergie = energie ;
			}
		}

		double proba = 1;

		// tableau des indices des etats a parcourir dans un certain ordre
		ArrayList<Integer> indiceEtats = new ArrayList<Integer>(); 
		for( int i = 0; i < nombreRepliques ; i++){
			indiceEtats.add(i);
		}

		while(Gamma.modifierT() && this.meilleureEnergie!=0){

			Collections.shuffle(indiceEtats, probleme.gen);	// melanger l'ordre de parcours des indices
			double Jr = -this.temperature/2*Math.log(Math.tanh(this.Gamma.t/nombreRepliques/this.temperature));	// calcul de Jr pour ce palier
		
			for (Integer p : indiceEtats){

				etat = probleme.etats[p];
				if(p == 0){
					previous = probleme.etats[nombreRepliques-1];
				}
				else{
					previous = probleme.etats[p-1];
				}

				if (p == nombreRepliques - 1){
					next = probleme.etats[0];
				}
				else{
					next = probleme.etats[p+1];
				}

				for (int j = 0; j < this.palier; j++){


					MutationElementaire mutation = probleme.getMutationElementaire(etat);	// trouver une mutation possible
					double deltaEp = probleme.calculerDeltaEp(etat, mutation);	// calculer deltaEp si la mutation etait acceptee
					double deltaEc = probleme.calculerDeltaEc(etat, previous, next, mutation);  // calculer deltaIEc si la mutation etait acceptee


					//diff�rences du hamiltonien total
					//multiplier deltaIEc par JGamma
					double deltaE = deltaEp/nombreRepliques - deltaEc*Jr;


					// On descend en �nergie
					if( deltaE <= 0 || deltaEp <= 0){
						probleme.modifElem(etat, mutation);				// faire la mutation
						proba = 1;


						// On teste la proba
					} else {
						proba = Math.exp(-deltaE / (this.K.k * this.temperature));	// calcul de la proba
						if (proba >= probleme.gen.nextDouble()) {							
							probleme.modifElem(etat, mutation);  		// accepter la mutation 
						}
					}
					double EpActuelle = etat.Ep.calculer(etat);		// energie potentielle temporelle

					// On regarde les �nergies obtenues
					if( EpActuelle < this.meilleureEnergie ){		// mettre a jour la meilleur energie
						this.meilleureEnergie = EpActuelle;
						if (this.meilleureEnergie == 0){	// fin du programme
							listeMeilleureEnergie.getlistEnergie().add(0.);
							listeEnergie[p].getlistEnergie().add(0.);
							System.out.println("fin par ce coot� la");
							return probleme;
						}
					}


					// Ajout dans les listes 
					
					if (deltaEp!=0 && p==1) {
						listeRapport.add((deltaEc*(-Jr))/(deltaEp/nombreRepliques));
					}
					
					
					if (p==1) {
						listeValeursJr.add(Jr);
					}
					
					
					if (proba!=1) {
						listeProba[p].add(proba);
						vectProbaPas1[p]=proba;
					} else {
						listeProba[p].add(vectProbaPas1[p]);
					}
					
					
					listeEnergie[p].add(EpActuelle);

					if ( p==1 || EpActuelle==0.) {
						if( EpActuelle > this.meilleureEnergie ){
							listeMeilleureEnergie.add(this.meilleureEnergie);
						} else {
							listeMeilleureEnergie.add(EpActuelle);
						}
					}
				} // arriv�e au palier
			} // fin d'un tour sur les r�pliques
		} // fin du while du recuit
		return probleme;
	}
	
	
	
	
	public Probleme lancer(Probleme probleme,ListEnergie listeMeilleureEnergie) {

		int nombreRepliques = probleme.etats.length;
		double[] vectProbaPas1= new double[nombreRepliques];
		
		for (int j=0;j<nombreRepliques;j++) {
			vectProbaPas1[j]=0;
		}

		Etat etat = probleme.etats[0];
		Etat previous = probleme.etats[nombreRepliques-1];
		Etat next = probleme.etats[1];
		for (int i = 0; i < nombreRepliques; i++){	// initialisation de meilleureEnergie
			double energie = probleme.etats[i].Ep.calculer(probleme.etats[i]) ;
			if (energie < this.meilleureEnergie){
				this.meilleureEnergie = energie ;
			}
		}

		double proba = 1;

		// tableau des indices des etats a parcourir dans un certain ordre
		ArrayList<Integer> indiceEtats = new ArrayList<Integer>(); 
		for( int i = 0; i < nombreRepliques ; i++){
			indiceEtats.add(i);
		}

		while(Gamma.modifierT() && this.meilleureEnergie!=0){

			Collections.shuffle(indiceEtats, probleme.gen);	// melanger l'ordre de parcours des indices
			double Jr = -this.temperature/2*Math.log(Math.tanh(this.Gamma.t/nombreRepliques/this.temperature));	// calcul de Jr pour ce palier
		
			for (Integer p : indiceEtats){

				etat = probleme.etats[p];
				if(p == 0){
					previous = probleme.etats[nombreRepliques-1];
				}
				else{
					previous = probleme.etats[p-1];
				}

				if (p == nombreRepliques - 1){
					next = probleme.etats[0];
				}
				else{
					next = probleme.etats[p+1];
				}

				for (int j = 0; j < this.palier; j++){


					MutationElementaire mutation = probleme.getMutationElementaire(etat);	// trouver une mutation possible
					double deltaEp = probleme.calculerDeltaEp(etat, mutation);	// calculer deltaEp si la mutation etait acceptee
					double deltaEc = probleme.calculerDeltaEc(etat, previous, next, mutation);  // calculer deltaIEc si la mutation etait acceptee


					//diff�rences du hamiltonien total
					//multiplier deltaIEc par JGamma
					double deltaE = deltaEp/nombreRepliques - deltaEc*Jr;


					// On descend en �nergie
					if( deltaE <= 0 || deltaEp <= 0){
						probleme.modifElem(etat, mutation);				// faire la mutation
						proba = 1;


						// On teste la proba
					} else {
						proba = Math.exp(-deltaE / (this.K.k * this.temperature));	// calcul de la proba
						if (proba >= probleme.gen.nextDouble()) {							
							probleme.modifElem(etat, mutation);  		// accepter la mutation 
						}
					}
					double EpActuelle = etat.Ep.calculer(etat);		// energie potentielle temporelle

					// On regarde les �nergies obtenues
					if( EpActuelle < this.meilleureEnergie ){		// mettre a jour la meilleur energie
						this.meilleureEnergie = EpActuelle;
						if (this.meilleureEnergie == 0){	// fin du programme
							listeMeilleureEnergie.getlistEnergie().add(0.);
							System.out.println("fin par ce coot� la");
							return probleme;
						}
					}


					// Ajout dans les listes 

					
					if ( p==1 || EpActuelle==0.) {
						if( EpActuelle > this.meilleureEnergie ){
							listeMeilleureEnergie.add(this.meilleureEnergie);
						} else {
							listeMeilleureEnergie.add(EpActuelle);
						}
					}
				} // arriv�e au palier
			} // fin d'un tour sur les r�pliques
		} // fin du while du recuit
		return probleme;
	}
	
	
	
	
}




