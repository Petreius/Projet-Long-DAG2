package solver.commun;

/**
 * Interface repr�sentant le concept abstrait de mutation dans l'algorithme de recuit.
 */
public interface IMutation {

	/**
	 * Renvoie une MutationElementaire possible selon le probl�me et l'�tat envoy�s.
	 * @param probleme
	 * Le probl�me dont on cherche une mutation �lementaire possible.
	 * @param etat
	 * L'�tat dont on cherche une mutation �lementaire possible.
	 * @return Un objet MutationElementaire
	 */
	abstract public MutationElementaire getMutationElementaire(Probleme probleme, Etat etat);
	
	/**
	 * Le probl�me fait une mutation sur demande.
	 * @param probleme
	 * Le probl�me sur lequel on cherche � r�aliser la mutation.
	 * @param etat
	 * L'�tat sur lequel on cherche � r�aliser la mutation.
	 * @param mutation
	 * La mutation � r�aliser.
	 */
	public void faire(Probleme probleme, Etat etat, MutationElementaire mutation);
	
}
