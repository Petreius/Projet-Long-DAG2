package solver;
import java.util.ArrayList;
import java.util.List;


public class ListEnergie extends IListEnergie {

	ArrayList<Double> list;
	ArrayList<Double> listTotale;
	int z;
	int echantillonage;
	int taille; // nombre d'energies calcul�es jusqu'a maintenant
	int fenetreK;
	
	
	public ListEnergie(int echantillonage, int fenetreK) {
		this.list = new ArrayList<Double>();
		this.listTotale = new ArrayList<Double>();
		this.z=1;
		this.echantillonage = echantillonage ;
		this.fenetreK = fenetreK;
	}	
	
	public void add(double energie) {
		if (this.z == this.echantillonage) {
			this.list.add(energie);
			this.z=1;
			
		}
		else {
			this.z++;
		}
	}
	
	public void addTotal(double energieCourante) {
		this.listTotale.add(energieCourante);
		if (this.listTotale.size() > (this.fenetreK + 1) ) this.listTotale.remove(0);
	}
	
	public List<Double> getlistEnergie() {
		return this.list;
	}
	
	public List<Double> getlistEnergieTotale() {
		return this.listTotale;
	}
	
	public void init() {
		this.z=1;
		this.list= new ArrayList<Double>();
		this.listTotale= new ArrayList<Double>();
	}
	
	public int getTaille() {
		return this.taille;
	}
	
	public void initTaille() {
		this.taille = 0;
	}
	
	public void augmenteTaille() {
		this.taille ++;
	}

	@Override
	public int getFenetreK() {
		return this.fenetreK;		
	}
	
	
}
