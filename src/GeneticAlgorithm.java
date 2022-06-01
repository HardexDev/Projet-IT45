public class GeneticAlgorithm {
    private int nbGenerations;
    private int taillePop;
    private double tauxCroisement;
    private double tauxMutation;
    private int tailleChromosome;
    private Population population;

    public GeneticAlgorithm() {
        nbGenerations = 100000;
        taillePop = 100;
        tauxCroisement = 0.8;
        tauxMutation = 0.5;
        tailleChromosome = 50;
        population = new Population(taillePop, tailleChromosome, 3);
    }

    public static Chromosome[] croisement1X(Chromosome p1, Chromosome p2) {
        int nbGenes = p1.getSize();

        Chromosome c1 = p1.copier();
        Chromosome c2 = p2.copier();

        float[] ordre_parent1 = new float[nbGenes];
        float[] ordre_parent2 = new float[nbGenes];

        for (int i=0; i<nbGenes; i++) {
            ordre_parent1[p1.getGenes()[i]] = i;
            ordre_parent2[p2.getGenes()[i]] = i;
        }

        int point = Random.rand_int(nbGenes);

        System.out.println("Point = " + point);

        for (int k=point+1; k<nbGenes; k++) {
            for (int l=k+1; l<nbGenes; l++) {
                if (ordre_parent2[c1.getGenes()[k]] > ordre_parent2[c1.getGenes()[l]]) {
                    System.out.println("Here");
                    c1.echange2genes(k, l);
                }
                if (ordre_parent1[c2.getGenes()[k]] > ordre_parent1[c2.getGenes()[l]]) {
                    System.out.println("Here");
                    c2.echange2genes(k, l);
                }
            }
        }

        Chromosome[] res = new Chromosome[2];
        res[0] = c1;
        res[1] = c2;

        return res;
    }

}
