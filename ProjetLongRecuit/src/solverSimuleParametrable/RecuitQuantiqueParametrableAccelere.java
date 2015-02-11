package solverSimuleParametrable;



import java.util.ArrayList;
import java.util.Collections;

import solver.GrapheColorieParticule;
import solverCommun.Etat;
import solverCommun.MutationElementaire;
import solverCommun.Probleme;


public class RecuitQuantiqueParametrableAccelere  { 				
																		// creer vos propres Temperature, ConstanteK et trucs pour les graphes
	public Temperature Gamma;
	public ConstanteK K;
	public double meilleureEnergie = Double.MAX_VALUE;									// en soit, nos energies pourraient etre des Int, mais bon 
	public double temperature;
	
	public int nbMaxIteration; 							// nombre maximale d'iteration si la solution n'est pas trouvee, redondance avec t.nbIteration
	public int palier;
	
	// reinitialise Gamma et K au debut de lancer
	private void init(){
		this.Gamma.init();
		this.K.init();
		meilleureEnergie = Double.MAX_VALUE;
	}

	public RecuitQuantiqueParametrableAccelere(Temperature Gamma, ConstanteK K, int palier, double temperature) {
		this.Gamma=Gamma;												// contructeur : on lui donne la facon de calculer l'energie, K et tout le blabla
		this.K=K;												// en creant une classe dedie et reutilisable qui extends temperature
		this.nbMaxIteration=this.Gamma.nbIteration;						// ainsi on combine le tout facilement
		this.palier = palier;		
		this.temperature = temperature;						//en quantique, la temp�rature est constante et le Gamma est variable, d'o� le fait que Gamma soit une "temp�rature"
	}

	public double lancer(Probleme probleme) {

		this.init();
		
		/*toujours a implementer :
		 * (peut-�tre changer les classes Temperature � un nom plus neutre)
		 * Enlever les variables de spin???		
		*/
		
		double mutationsTentees = 0;
		double mutationsAccepteesUB = 0;
		double mutationsAcceptees = 0;
		
		//TEST variables temporaires declarees qu'une fois ici:
		double Jr = 0;
		double deltaEp = 0;
		double deltaEcUB = 0;
		double deltaE = 0;
		double EpActuelle = 0;
		double deltaEc = 0;
		//TEST
		
		int nombreRepliques = probleme.etats.length;
		
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
			Jr = -this.temperature/2*Math.log(Math.tanh(this.Gamma.t/nombreRepliques/this.temperature));	// calcul de Jr pour ce palier

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
					mutationsTentees++; //permet d'avoir une r�f�rence ind�pendante pour les am�liorations de l'algorithme, mais aussi sur son temps
					
					deltaEp = probleme.calculerDeltaEp(etat, mutation);	// calculer deltaEp si la mutation etait acceptee
					deltaEcUB = probleme.calculerDeltaEcUB(etat, previous, next, mutation);  // calculer deltaIEc si la mutation etait acceptee
					//System.out.println("UB : " + deltaEcUB)	;
					//diff�rences du hamiltonien total
					//multiplier deltaIEc par JGamma
					deltaE = deltaEp/nombreRepliques - deltaEcUB*Jr;
					//K.calculerK(deltaE);

					if(deltaEp <= 0){
						//deltaE ici n'est pas le bon, il d�pend de EcUB
						mutationsAcceptees++;
						probleme.modifElem(etat, mutation);				// faire la mutation
						EpActuelle = etat.Ep.calculer(etat);		// energie potentielle temporelle
						
						if( EpActuelle < this.meilleureEnergie ){		// mettre a jour la meilleur energie
							this.meilleureEnergie = EpActuelle;
							//System.out.println("ME = "+this.meilleureEnergie);
							if (this.meilleureEnergie == 0){	// fin du programme
								//System.out.println("Mutations tent�es : " + mutationsTentees);
								//System.out.println("Mutations accept�es UB : " + mutationsAccepteesUB);
								//System.out.println("Mutations accept�es : " + mutationsAcceptees);
								return mutationsTentees ;
							}
						}
					}
					else {
						proba = exp1(-deltaE / (this.K.k * this.temperature));
						
						if (proba >= probleme.gen.nextDouble()) {	
							mutationsAccepteesUB++;

							deltaEc = probleme.calculerDeltaEc(etat, previous, next, mutation);
							deltaE = deltaEp/nombreRepliques - deltaEc*Jr;
							
							if( deltaE <= 0){
								
								mutationsAcceptees++;
								probleme.modifElem(etat, mutation);				// faire la mutation
								EpActuelle = etat.Ep.calculer(etat);		// energie potentielle temporelle
								
								if( EpActuelle < this.meilleureEnergie ){		// mettre a jour la meilleur energie
									this.meilleureEnergie = EpActuelle;
									// System.out.println("ME = "+this.meilleureEnergie); //TEST
									if (this.meilleureEnergie == 0){	// fin du programme
										//System.out.println("Mutations tent�es : " + mutationsTentees);
										//System.out.println("Mutations accept�es UB : " + mutationsAccepteesUB);
										//System.out.println("Mutations accept�es : " + mutationsAcceptees);
										return mutationsTentees ;
									}
								}
							}
							else{
								proba = exp1(-deltaE / (this.K.k * this.temperature));
							
								if (proba >= probleme.gen.nextDouble()) {
									mutationsAcceptees++;
									probleme.modifElem(etat, mutation);  		// accepter la mutation 
								}
							}

						}
					}
				}

			}
		}
		
		
		//System.out.println("Mutations tent�es : " + mutationsTentees);
		//System.out.println("Mutations accept�es UB : " + mutationsAccepteesUB);
		//System.out.println("Mutations accept�es : " + mutationsAcceptees);
		return mutationsTentees ;
	}
	
	double exp1(double x) {
		  x = 1.0 + x / 256.0;
		  x *= x; x *= x; x *= x; x *= x;
		  x *= x; x *= x; x *= x; x *= x;
		  return x;
	}
	
	public static double exp(double val) {
		final long tmp = (long) (1512775 * val + 1072632447);
		return Double.longBitsToDouble(tmp << 32);
	}
}
