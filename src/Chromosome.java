import entities.Intervenant;
import entities.Mission;
import jdk.jshell.execution.Util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Chromosome implements Cloneable {
    private int[] genes;
    private int size;
    private int nbIntervenants;
    private double fitness = 0.0;

    public Chromosome(int[] genes, int size, int nbIntervenants) {
        this.genes = genes;
        this.size = size;
        this.nbIntervenants = nbIntervenants;
    }

    public Chromosome(Chromosome c) {
        genes = c.genes;
        size = c.size;
        nbIntervenants = c.nbIntervenants;
    }

    public Chromosome(int tailleChromosome, int nbIntervenants) {
        size = tailleChromosome;
        genes = new int[tailleChromosome];
        this.nbIntervenants = nbIntervenants;

        Random rand = new Random();

        for (int i=0; i<genes.length; i++) {
            int randIntervenant = rand.nextInt(nbIntervenants) + 1;

            genes[i] = randIntervenant;
        }
    }

    public float evaluateFitnessEmployee() {
        return 0f;
    }

    public void afficher() {
        for (int i=0; i<genes.length; i++) {
            System.out.print(i + " ");
        }

        System.out.println();

        for (int i=0; i<genes.length; i++) {
            System.out.print(genes[i] + " ");
        }


        System.out.println();
    }

    public void echange2genes(int indexA, int indexB) {
        int temp = genes[indexA];
        genes[indexA] = genes[indexB];
        genes[indexB] = temp;

    }

    public void evaluerPremierCritere(int nombreMissions) {
        double[][] distances = Utils.constructionDistance("src/instances/Distances.csv", nombreMissions);
        List<Mission> missions = Utils.constructionMissions("src/instances/Missions.csv");
        List<Intervenant> intervenants = Utils.constructionIntervenants("src/instances/Intervenants.csv");

        // Calcul des constantes
        double total = 0;
        for (int i=0; i<intervenants.size(); i++) {
            total += intervenants.get(i).getQuota();
        }

        double moyenneQuota = total/intervenants.size();

        double constanteQuota = 100 / moyenneQuota;

        // System.out.println(constanteQuota);

        double constanteHeuresSupTolerees = 100.0 / 10;

        // System.out.println(constanteHeuresSupTolerees);

        double totalDistances = 0;
        for (int i=0; i < distances.length-1; i++) {
            totalDistances += distances[0][i+1] + distances[i+1][0];
        }

        double moyenne = (totalDistances / (double) intervenants.size());

        // System.out.println(moyenne);

        double constanteMoyenneDistances = 100.0 / (totalDistances / (double) intervenants.size());

        // System.out.println(constanteMoyenneDistances);

        double[] heuresTravaillees = new double[intervenants.size()];

        // On ajoute les heures travaillées pendant les missions
        for (int i=0; i<genes.length; i++) {
            Mission mission = missions.get(i);
            heuresTravaillees[genes[i] - 1] += (mission.getHeure_fin() - mission.getHeure_debut()) / 60.0;
        }

        double[] distancesParIntervenant = distancesParIntervenant(distances, missions);

        // On ajoute le temps de trajet aux heures travaillées
        for (int i=0; i<distancesParIntervenant.length; i++) {
            heuresTravaillees[i] += (distancesParIntervenant[i]/1000) / 50;
        }

        // Calculer l'écart-type des heures supplémentaires
        double[] heuresSup = new double[intervenants.size()];


        for (int i=0; i<heuresTravaillees.length; i++) {
            heuresSup[i] = (heuresTravaillees[i] - intervenants.get(i).getQuota()) > 0 ? (heuresTravaillees[i] - intervenants.get(i).getQuota()) : 0;
        }

        // System.out.println("heures Travaillées : " + Arrays.toString(heuresTravaillees));
        // System.out.println("heures sup : " + Arrays.toString(heuresSup));

        double ecartTypeHeuresSup = Utils.calculerEcartType(heuresSup);
        // System.out.println("Ecart-type heures sup : " + ecartTypeHeuresSup);

        // Calculer l'écart-type des heures non-travaillées
        double[] heuresNonTravaillees = new double[intervenants.size()];

        for (int i=0; i<heuresTravaillees.length; i++) {
            heuresNonTravaillees[i] = (intervenants.get(i).getQuota() - heuresTravaillees[i]) > 0 ? (intervenants.get(i).getQuota() - heuresTravaillees[i]) : 0;
        }

        double ecartTypeHeureNonTravaillees = Utils.calculerEcartType(heuresNonTravaillees);

        // System.out.println("Heures Non Travaillées : " + Arrays.toString(heuresNonTravaillees));
        // System.out.println("Ecart type heures non travaillées : " + ecartTypeHeureNonTravaillees);

        double ecartTypeDistances = Utils.calculerEcartType(distancesParIntervenant);

        // System.out.println("Distances par intervenant : " + Arrays.toString(distancesParIntervenant));
        // System.out.println("Ecart-type distances : " + ecartTypeDistances);

        fitness = (constanteQuota*ecartTypeHeureNonTravaillees + constanteHeuresSupTolerees*ecartTypeHeuresSup + constanteMoyenneDistances*ecartTypeDistances)/3.0;
    }

    private double[] distancesParIntervenant(double[][] distances, List<Mission> missions) {
        double[] res = new double[nbIntervenants];

        ArrayList<ArrayList<ArrayList<Integer>>> ordreMissions = ordreMission(missions);

        for (int i=0; i<nbIntervenants; i++) {
            for (int j=0; j<5; j++) {
                if (ordreMissions.get(i).get(j).size() > 0) {
                    res[i] += distances[0][ordreMissions.get(i).get(j).get(0) + 1];
                    for (int k=0; k<ordreMissions.get(i).get(j).size()-1; k++) {
                        res[i] += distances[ordreMissions.get(i).get(j).get(k)][ordreMissions.get(i).get(j).get(k+1)];
                    }

                    res[i] += distances[ordreMissions.get(i).get(j).get(ordreMissions.get(i).get(j).size() - 1) + 1][0];
                }
            }
        }

        return res;
    }

    private ArrayList<ArrayList<ArrayList<Integer>>> ordreMission(List<Mission> missions) {
        ArrayList<ArrayList<ArrayList<Integer>>> ordreMissions = new ArrayList<>();

        // Initialisation de l'ArrayList
        for (int i=0; i<nbIntervenants; i++) {
            ArrayList<ArrayList<Integer>> listeIntervenants = new ArrayList<>();
            ordreMissions.add(listeIntervenants);
            for (int j=0; j<5; j++) {
                listeIntervenants.add(new ArrayList<>());
            }
        }

        // On insère les missions pour chaque intervenant
        for (int i=0; i<nbIntervenants; i++) {
            int currentDay = 1;

            for (int j=0; j<genes.length; j++) {

                if (genes[j] == i+1) {
                    if (currentDay != missions.get(j).getJour()) {
                        currentDay = missions.get(j).getJour();
                    }

                    ordreMissions.get(i).get(currentDay-1).add(j);
                }
            }
        }

        // On trie chaque mission pour chaque intervenant en fonction de l'heure de début de la mission (Bubble Sort)
        for (int i=0; i<nbIntervenants; i++) {
            for (int j=0; j<5; j++) {
                for (int k=0; k<ordreMissions.get(i).get(j).size() - 1; k++) {
                    for (int l=0; l<ordreMissions.get(i).get(j).size() - k - 1; l++) {
                        if (missions.get(l+1).getHeure_debut() < missions.get(l).getHeure_debut()) {
                            int temp = ordreMissions.get(i).get(j).get(l);
                            ordreMissions.get(i).get(j).set(l, ordreMissions.get(i).get(j).get(l+1));
                            ordreMissions.get(i).get(j).set(l+1, temp);
                        }
                    }
                }
            }
        }

        return ordreMissions;
    }

    public int getSize() {
        return size;
    }

    public int[] getGenes() {
        return genes;
    }

    public Chromosome copier() {
        return new Chromosome(genes, size, nbIntervenants);
    }

    public void copier(Chromosome chromosome) {
        for (int i=0; i<size; i++) {
            genes[i] = chromosome.getGenes()[i];
        }
    }

    public int[] copyGenes() {
        return Arrays.copyOf(genes, size);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public Chromosome clone() {
        try {
            Chromosome clone = (Chromosome) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            clone.genes = Arrays.copyOf(genes, size);
            clone.size = size;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
