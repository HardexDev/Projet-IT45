public class Population {
    private Solution[] individus;
    private int size;

    public Population(Solution[] individus, int size) {
        this.individus = individus;
        this.size = size;
    }

    public Population(int taillePop, int tailleChromosome, int nbIntervenants) {
        individus = new Solution[taillePop];
        for (int i=0; i<individus.length; i++) {
            individus[i] = new Solution(tailleChromosome, nbIntervenants);
        }
    }

    public void afficher() {
        for (int i=0; i<individus.length; i++) {
            individus[i].afficher();
        }
    }

    public Solution[] getIndividus() {
        return individus;
    }

    public int getSize() {
        return size;
    }
}
