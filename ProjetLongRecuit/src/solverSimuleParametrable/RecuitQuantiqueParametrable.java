package solverSimuleParametrable;



import solverCommun.Etat;
import solverCommun.MutationElementaire;
import solverCommun.Probleme;


public class RecuitQuantiqueParametrable extends RecuitSimuleP { 				// pas touche � cette classe !!!
																		// creer vos propres Temperature, ConstanteK et trucs pour les graphes
	public Temperature Gamma;
	public ConstanteK K;
	public double meilleureEnergie = Double.MAX_VALUE;									// en soit, nos energies pourraient etre des Int, mais bon 
	public double temperature;
	
	public int nbMaxIteration; 							// nombre maximale d'iteration si la solution n'est pas trouvee, redondance avec t.nbIteration
	public int palier;
	// abstract void init(); 								// initialisation // mais de quoi ?

	public RecuitQuantiqueParametrable(Temperature Gamma, ConstanteK K, int palier, double temperature) {
		this.Gamma=Gamma;												// contructeur : on lui donne la facon de calculer l'energie, K et tout le blabla
		this.K=K;												// en creant une classe dedie et reutilisable qui extends temperature
		this.nbMaxIteration=this.Gamma.nbIteration;						// ainsi on combine le tout facilement
		this.palier = palier;		
		this.temperature = temperature;						//en quantique, la temp�rature est constante et le Gamma est variable, d'o� le fait que Gamma soit une "temp�rature"
	}

	public Probleme lancer(Probleme probleme) {

		// TODO methode init()
		// init();
		
		/*toujours a implementer :
		 * Gamma variable et initialisation du gamma (peut-�tre changer les classes Temperature � un nom plus neutre) //fait
		 * Implementation d'une liste circulaire d'�tats // ou plutot d'un traitement circulaire au niveau de Ec (s�lection de next et previous faite ---)
		 * Implementation d'un shuffle des etats //fait
		 * methodes de calcul de Ec
		 * Implementation des classes couleurs
		 * Enlever les variables de spin???		
		*/
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
		
		while(Gamma.modifierT() && this.meilleureEnergie!=0){
			
			probleme.shuffleEtats();
			double Jr = -this.temperature/2*Math.log(Math.tanh(this.Gamma.t/nombreRepliques/this.temperature));
			
			for (int i = 0; i < nombreRepliques; i++){
				etat = probleme.etats[i];
				
				if(i == 0){
					previous = probleme.etats[nombreRepliques-1];
				}
				else{
					previous = probleme.etats[i-1];
				}
				
				if (i == nombreRepliques - 1){
					next = probleme.etats[0];
				}
				else{
					next = probleme.etats[i+1];
				}
				
				for (int j = 0; j < palier; j++){
					MutationElementaire mutation = probleme.getMutationElementaire(etat);	// trouver une mutation possible
					double deltaEp = probleme.calculerDeltaEp(etat, mutation);	// calculer deltaEp si la mutation etait acceptee
					double deltaEc = probleme.calculerDeltaEc(etat, previous, next, mutation);  // calculer deltaIEc si la mutation etait acceptee
					//puis multiplier deltaIEc par JGamme
					deltaEc *= Jr;
					
					//diff�rences du hamiltonien total
					double deltaE = deltaEp/nombreRepliques - deltaEc;
					K.calculerK(deltaE);
					
					// System.out.println(deltaEp +" " + deltaEc +" " +deltaE); //TEST
					
					if( deltaE <= 0 || deltaEp <= 0){
						System.out.println("acceptee"); //TEST
						probleme.modifElem(etat, mutation);				// faire la mutation
						double EpActuelle = etat.Ep.calculer(etat);		// energie potentielle temporelle
						if( EpActuelle < this.meilleureEnergie ){		// mettre a jour la meilleur energie
							this.meilleureEnergie = EpActuelle;
							System.out.println("etat "+etat.toString()+" , ME = "+this.meilleureEnergie+" , G = "+this.Gamma.t); //TEST
						}
					} else {
						proba = Math.exp(-deltaE / (this.K.k * this.temperature));	// calcul de la proba
						if (proba >= probleme.gen.nextDouble()) {
							probleme.modifElem(etat, mutation);  		// accepter la mutation 
						}
					}
				}
			}
		}
		
		return probleme;
	}
}
