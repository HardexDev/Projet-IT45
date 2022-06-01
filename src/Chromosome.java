import java.util.Random;

public class Chromosome {
    private int[] genes;
    private float[] genesWithKeys;
    private int size;

    public Chromosome(int[] gene, int size) {
        this.genes = genes;
        this.size = size;
    }

    public Chromosome(Chromosome c) {
        genes = c.genes;
        genesWithKeys = c.genesWithKeys;
        size = c.size;
    }

    public Chromosome(int tailleChromosome, int nbIntervenants) {
        size = tailleChromosome;
        genes = new int[tailleChromosome];
        genesWithKeys = new float[tailleChromosome];
        Random rand = new Random();

        for (int i=0; i<genes.length; i++) {
            int randInt=0, randIntervenant = 0;
            float randKey = 0f;

            boolean recommence = true;
            while (recommence) {
                recommence = false;
                randInt = rand.nextInt(tailleChromosome);
                randKey = rand.nextFloat();
                randIntervenant = rand.nextInt(nbIntervenants) + 1;

                for (int j=0; j<i; j++) {
                    if (genes[j] == randInt) {
                        recommence = true;
                        break;
                    }
                }
            }
            genes[i] = randInt;
            genesWithKeys[i] = randKey + randIntervenant;
        }
    }

    public float evaluateFitnessEmployee() {
        return 0f;
    }

    public void afficher() {
        for (int i=0; i<genesWithKeys.length; i++) {
            System.out.print(genes[i] + " ");
            // System.out.printf("%.2f ", genesWithKeys[i]);
        }
        System.out.println();
    }

    public void echange2genes(int indexA, int indexB) {
        int temp = genes[indexA];
        float temp2 = genesWithKeys[indexA];

        genes[indexA] = genes[indexB];
        genesWithKeys[indexA] = genesWithKeys[indexB];

        genes[indexB] = temp;
        genesWithKeys[indexB] = temp2;

    }

    public int getSize() {
        return size;
    }

    public float[] getGenesWithKeys() {
        return genesWithKeys;
    }

    public int[] getGenes() {
        return genes;
    }

    public Chromosome copier() {
        return new Chromosome(this);
    }
}
