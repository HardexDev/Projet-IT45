import entities.Intervenant;
import entities.Mission;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Population {
    private Chromosome[] individus;
    private int[] ordre;
    private int size;

    private List<Mission> missions;
    private List<Intervenant> intervenants;
    private double[][] distances;

    /**
     *
     * @param individus
     * @param size
     */
    public Population(Chromosome[] individus, int size) {
        this.individus = individus;
        this.size = size;
        ordre = new int[individus.length];
    }

    /**
     *
     * @param taillePop
     * @param tailleChromosome
     * @param nbIntervenants
     * @param missions
     * @param intervenants
     * @param distances
     */
    public Population(int taillePop, int tailleChromosome, int nbIntervenants, List<Mission> missions, List<Intervenant> intervenants, double[][] distances) {
        individus = new Chromosome[taillePop];
        size = taillePop;
        this.missions = missions;
        this.intervenants = intervenants;
        this.distances = distances;

        for (int i=0; i<individus.length; i++) {
            individus[i] = new Chromosome(tailleChromosome, nbIntervenants, missions, intervenants, distances);
        }
        ordre = new int[individus.length];
    }

    /**
     *
     */
    public void ordonner() {
        int inter;
        for(int i=0; i<size; i++)
            ordre[i]=i;

        for(int i=0; i<size-1; i++)
            for(int j=i+1; j<size; j++)
                if(individus[ordre[i]].getFitness() > individus[ordre[j]].getFitness()) {
                    inter= ordre[i];
                    ordre[i] = ordre[j];
                    ordre[j] = inter;
                }
    }

    /**
     *
     */
    public void reordonner() {
        int inter;
        for(int i=0; i<size-1; i++)
            for(int j=i+1; j<size; j++)
                if(individus[ordre[i]].getFitness() > individus[ordre[j]].getFitness())
        {
            inter = ordre[i];
            ordre[i] = ordre[j];
            ordre[j] = inter;
        }
    }

    /**
     *
     * @return
     */
    public Chromosome selectionRoulette() {
        double somme_fitness = individus[0].getFitness();
        double fitness_max   = individus[0].getFitness();
        double somme_portion;

        for(int i=1; i<size; i++)
        {
            somme_fitness += individus[i].getFitness();
            if (fitness_max < individus[i].getFitness())
            fitness_max = individus[i].getFitness();
        }
        somme_portion = fitness_max*size - somme_fitness;

        Random rand = new Random();

        double variable_alea = rand.nextInt(1000)/1000.0;

        int ind = 0;
        double portion = (fitness_max - individus[0].getFitness()) * (1./somme_portion);
        while ((ind<size-1) && (variable_alea>=portion))
        {
            ind++;
            portion += (fitness_max - individus[ind].getFitness())*(1./somme_portion);
        }

        return individus[ind];
    }

    /**
     *
     * @param individu
     */
    public void remplacementRoulette(Chromosome individu) {
        double somme_fitness = individus[0].getFitness();
        for(int i=1; i<size; i++)
            somme_fitness += individus[i].getFitness();

        double variable_alea;
        int ind = ordre[0];
        double portion;

        Random rand = new Random();
        while (ordre[0]==ind)
        {
            variable_alea = rand.nextInt(1000)/1000.0;
            ind = 0;
            portion = individus[0].getFitness()*(1./somme_fitness);
            while ((ind<size-1) && (variable_alea>portion))
            {
                ind++;
                portion += individus[ind].getFitness()*(1./somme_fitness);
            }
        }
        individus[ind].copier(individu);
        individus[ind].setFitness(individu.getFitness());
    }

    public void afficher() {
        for (int i=0; i<individus.length; i++) {
            individus[i].afficher();
        }
    }

    /**
     *
     * @return
     */
    public int nombreIndividusEgaux() {
        int res = 0;
        for (Chromosome c : individus) {
            for (Chromosome c2 : individus) {
                if (c2 != c) {
                    if (Arrays.equals(c.getGenes(), c2.getGenes())) {
                        res++;
                    }
                }

            }
        }

        return res;
    }

    public Chromosome[] getIndividus() {
        return individus;
    }

    public int[] getOrdre() {
        return ordre;
    }

    public int getSize() {
        return size;
    }

    public void setIndividus(Chromosome[] individus) {
        this.individus = Arrays.copyOf(individus, size);
    }
}
