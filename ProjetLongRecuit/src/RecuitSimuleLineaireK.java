// Sous-classe de RecuitSimuleLineaire implementant un k variable


public class RecuitSimuleLineaireK extends RecuitSimuleLineaire {

	public RecuitSimuleLineaireK(double k, double Tdeb, double Tfin, double pas, int N, IListEnergie listEnergie) {
		super(k, Tdeb, Tfin, pas, N, listEnergie);
	}
	
	public void calculerK(){
		double taille = (double) this.listEnergie.getTaille();
		this.k = (this.k*(taille-1)+this.energiePrec)/taille;  // moyenne des energies
	}
	
}