public class Population {
    private Chromosome[] individus;
    private int size;

    public Population(Chromosome[] individus, int size) {
        this.individus = individus;
        this.size = size;
    }

    public Population(int taillePop, int tailleChromosome, int nbIntervenants) {
        individus = new Chromosome[taillePop];
        for (int i=0; i<individus.length; i++) {
            individus[i] = new Chromosome(tailleChromosome, nbIntervenants);
        }
    }

    public void afficher() {
        for (int i=0; i<individus.length; i++) {
            individus[i].afficher();
        }
    }

    public Chromosome[] getIndividus() {
        return individus;
    }

    public int getSize() {
        return size;
    }
}
