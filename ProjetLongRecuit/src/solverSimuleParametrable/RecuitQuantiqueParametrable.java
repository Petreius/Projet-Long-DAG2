package solverSimuleParametrable;



import solverCommun.Etat;
import solverCommun.MutationElementaire;
import solverCommun.Probleme;


public class RecuitQuantiqueParametrable extends RecuitSimuleP { 				// pas touche � cette classe !!!
																		// creer vos propres Temperature, ConstanteK et trucs pour les graphes
	public Temperature G;
	public ConstanteK K;
	public double meilleureEnergie;									// en soit, nos energies pourraient etre des Int, mais bon 
	public double energiePrec;										// probleme nous renvoie des doubles
	public double temperature;
	
	public int nbMaxIteration; 							// nombre maximale d'iteration si la solution n'est pas trouvee, redondance avec t.nbIteration
	public int palier;
	// abstract void init(); 								// initialisation // mais de quoi ?

	public RecuitQuantiqueParametrable(Temperature G, ConstanteK K, int palier, double temperature) {
		this.G=G;												// contructeur : on lui donne la facon de calculer l'energie, K et tout le blabla
		this.K=K;												// en creant une classe dedie et reutilisable qui extends temperature
		this.nbMaxIteration=this.G.nbIteration;						// ainsi on combine le tout facilement
		this.palier = palier;										//en quantique, la temp�rature est constante et le Gamma est variable, d'o� le fait que Gamma soit une "temp�rature"
	}

	public Probleme lancer(Probleme probleme) {

		// TODO methode init()
		// init();
		
		/*toujours a implementer :
		 * Gamma variable et initialisation du gamma (peut �tre changer les classes Temperature � un nom plus neutre)
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
		this.energiePrec = probleme.calculerEnergie() ;
		this.meilleureEnergie = this.energiePrec ;
		double proba = 1;
		
		while(G.modifierT() && this.meilleureEnergie!=0){
			probleme.shuffleEtats();
			
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
					double deltaEp = probleme.calculerDeltaEp(etat, mutation);	// calculer deltaE si la mutation etait acceptee
					
					double deltaE = deltaEp;
					K.calculerK(deltaE);
					
					if( deltaE <= 0){
						probleme.modifElem(etat, mutation);				// faire la mutation
						this.energiePrec += deltaE;						// mettre a jour l'energie
						if( this.energiePrec < this.meilleureEnergie ){	// mettre a jour la meilleur energie
							this.meilleureEnergie = this.energiePrec;
						}
					} else {
						proba = Math.exp(-deltaE / (this.K.k * this.temperature));	// calcul de la proba
						if (proba >= probleme.gen.nextDouble()) {
							probleme.modifElem(etat, mutation);  		// accepter la mutation 
							this.energiePrec += deltaE;					// mettre a jour l'energie
						}
					}
				}
			}
		}
		
		return probleme;
	}
}
