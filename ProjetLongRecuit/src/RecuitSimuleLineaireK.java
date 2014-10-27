// Sous-classe de RecuitSimuleLineaire implementant un k variable


public class RecuitSimuleLineaireK extends RecuitSimuleLineaire {

	public RecuitSimuleLineaireK(double k, double Tdeb, double Tfin, double pas, int N, IListEnergie listEnergie) {
		super(k, Tdeb, Tfin, pas, N, listEnergie);
	}
	
	public void calculerK(){
		int taille = this.listEnergie.getTaille();
		if (taille <= 10){
			this.k = (this.k*(taille-1)+this.energiePrec)/taille;  // moyenne des energies
		}
		else{
			this.k = (this.k*(10) - this.listEnergie.getlistEnergieTotale().get(taille - 11)
						+ this.energiePrec) / 10;
		}
	}
	
}
