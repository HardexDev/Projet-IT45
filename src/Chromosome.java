import entities.Intervenant;
import entities.Mission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Chromosome implements Cloneable {
    private int[] genes;
    private int size;
    private int nbIntervenants;

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
            System.out.print(genes[i] + " ");
            // System.out.printf("%.2f ", genesWithKeys[i]);
        }
        System.out.println();
    }

    public void echange2genes(int indexA, int indexB) {
        int temp = genes[indexA];
        genes[indexA] = genes[indexB];
        genes[indexB] = temp;

    }

    public int evaluerPremierCritere(int nombreMissions) {
        double[][] distances = Utils.constructionDistance("src/instances/Distances.csv", nombreMissions);
        List<Mission> missions = Utils.constructionMissions("src/instances/Missions.csv");
        List<Intervenant> intervenants = Utils.constructionIntervenants("src/instances/Intervenants.csv");

        // Calcul des constantes
        double total = 0;
        for (int i=0; i<intervenants.size(); i++) {
            total += intervenants.get(i).getQuota();
        }

        double moyenneQuota = total/intervenants.size();

        double constanceQuota = 100 / moyenneQuota;

        System.out.println(constanceQuota);

        double constanteHeuresSupTolerees = 100.0 / 10;

        System.out.println(constanteHeuresSupTolerees);

        double totalDistances = 0;
        for (int i=0; i < distances.length-1; i++) {
            totalDistances += distances[0][i+1] + distances[i+1][0];
        }

        double moyenne = (totalDistances / (double) intervenants.size());

        System.out.println(moyenne);

        double constanteMoyenneDistances = 100.0 / (totalDistances / (double) intervenants.size());

        System.out.println(constanteMoyenneDistances);

        double[] heuresTravaillees = new double[intervenants.size()];

        // Calculer l'écart-type des heures supplémentaires
        double[] heuresSup = new double[intervenants.size()];

        for (int i=0; i<genes.length; i++) {
            Mission mission = missions.get(i);
            heuresTravaillees[genes[i] - 1] += (mission.getHeure_fin() - mission.getHeure_debut()) / 60.0;
        }

        for (int i=0; i<heuresTravaillees.length; i++) {
            heuresSup[i] = (heuresTravaillees[i] - intervenants.get(i).getQuota()) > 0 ? (heuresTravaillees[i] - intervenants.get(i).getQuota()) : 0;
        }

        System.out.println("heures Travaillées : " + Arrays.toString(heuresTravaillees));
        System.out.println("heures sup : " + Arrays.toString(heuresSup));

        double ecartTypeHeuresSup = Utils.calculerEcartType(heuresSup);
        System.out.println("Ecart-type heures sup : " + ecartTypeHeuresSup);

        // Calculer l'écart-type des heures non-travaillées
        double[] heuresNonTravaillees = new double[intervenants.size()];

        for (int i=0; i<heuresTravaillees.length; i++) {
            heuresNonTravaillees[i] = (intervenants.get(i).getQuota() - heuresTravaillees[i]) > 0 ? (intervenants.get(i).getQuota() - heuresTravaillees[i]) : 0;
        }

        double ecartTypeHeureNonTravaillees = Utils.calculerEcartType(heuresNonTravaillees);

        System.out.println("Heures Non Travaillées : " + Arrays.toString(heuresNonTravaillees));
        System.out.println("Ecart type heures non travaillées : " + ecartTypeHeureNonTravaillees);

        distancesParIntervenant(distances, missions);


        return 0;
    }

    private double[] distancesParIntervenant(double[][] distances, List<Mission> missions) {
        double[] res = new double[nbIntervenants];
        ArrayList<ArrayList<ArrayList<Integer>>> ordreMissions = new ArrayList<>();

        for (int i=0; i<nbIntervenants; i++) {
            ordreMissions.add(new ArrayList<>());
        }

        for (int i=0; i<nbIntervenants; i++) {
            ordreMissions.get(i).add(new ArrayList<>());
            int currentDay = 1;

            for (int j=0; j<genes.length; j++) {

                if (genes[j] == i+1) {
                    if (currentDay == missions.get(j).getJour()) {
                        ordreMissions.get(i).get(currentDay-1).add(j);
                    } else {
                        currentDay = missions.get(j).getJour();
                        ordreMissions.get(i).add(new ArrayList<>());
                    }
                }
            }
        }

        System.out.println(ordreMissions.size());

        return res;

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

    public int[] copyGenes() {
        return Arrays.copyOf(genes, size);
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

    public int solutionValide(){
        int missionIndex, intervenantIndex;
        List<Mission> missions = Utils.constructionMissions("src/instances/Missions.csv");
        List<Intervenant> intervenants = Utils.constructionIntervenants("src/instances/Intervenants.csv");

        //**********************contrainte 1****************************
        //verifier si une case vide ?
        //**********************contrainte 2****************************
        for (int i=0;i<size;i++)
        {
            missionIndex=i;
            for (int j=0; i<intervenants.size();i++)
            if (intervenants.get(i).getId()==genes[i] )
            {
                intervenantIndex=j;
                if (intervenants.get(intervenantIndex).getCompetence() != missions.get(missionIndex).getCompetence())
                    return -1;
            }
        }

        //**********************contrainte 3****************************
        //**********************contrainte 4****************************
        // evident
        //**********************contrainte 5****************************

        //**********************contrainte 6****************************

        //**********************contrainte 7****************************
        //**********************contrainte 8****************************
        //**********************contrainte 9****************************
    return 0;
    }
}
