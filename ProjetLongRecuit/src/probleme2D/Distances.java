package probleme2D;

import solver.commun.EnergieCinetique;
import solver.commun.Etat;
import solver.commun.MutationElementaire;
import solver.commun.Probleme;
import vertexColoring.GrapheColorie;

public class Distances extends EnergieCinetique {



	@Override
	public double calculer(Probleme probleme) {
		
		double Ec=0;
		
		Position2DParticule position2DParticule = (Position2DParticule) probleme;
		
		for (int p = 0; p < position2DParticule.replique-1 ; p++){
			Position2D etat = (Position2D) probleme.etats[p];
			Position2D etatNext = (Position2D) probleme.etats[p+1];
			Ec+=Math.abs(etat.getX()-etatNext.getX())+Math.abs(etat.getY()-etatNext.getY());
		}
		return Ec;
	}

	@Override
	public double calculerDeltaE(Etat etat, Etat prev, Etat next,MutationElementaire mutation) {
		Position2D position2D=(Position2D) etat;
		Position2D position2Dnext=(Position2D) next;
		Position2D position2Dprev=(Position2D) prev;
		
		MutationElementairePixel mutation2D = (MutationElementairePixel) mutation;
		
		double EcPrecedentX = Math.abs(position2Dnext.getX()- position2D.getX())+Math.abs(position2Dprev.getX()- position2D.getX());
		double EcPrecedentY = Math.abs(position2Dnext.getY()- position2D.getY())+Math.abs(position2Dprev.getY()- position2D.getY());
		
		double EcsuivantX = Math.abs(position2Dnext.getX()- position2D.getX()-mutation2D.deltaX)+Math.abs(position2Dprev.getX()- position2D.getX()-mutation2D.deltaX);
		double EcsuivantY = Math.abs(position2Dnext.getY()- position2D.getY()-mutation2D.deltaY)+Math.abs(position2Dprev.getY()- position2D.getY()-mutation2D.deltaY);
		
		double DeltaEc=EcsuivantX+EcsuivantY-EcPrecedentX-EcPrecedentY;
		return DeltaEc;
	}

	@Override
	public double calculerDeltaEUB(Etat etat, Etat prev, Etat next,
			MutationElementaire mutation) {
		// TODO Auto-generated method stub
		return 0;
	}

}
