import entities.Intervenant;
import entities.Mission;

import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class Chromosome implements Cloneable {
    private int[] genes;
    private int size;
    private int nbIntervenants;
    private double fitness = 0.0;
    private double fitness1 = 0.0;
    private double fitness2 = 0.0;
    private double fitness3 = 0.0;
    private double[][] distances;
    private List<Mission> missions;
    private List<Intervenant> intervenants;

    private ArrayList<ArrayList<ArrayList<Integer>>> ordreMissions;

    /**
     * constructeur
     * @param genes
     * @param size
     * @param nbIntervenants
     * @param missions
     * @param intervenants
     * @param distances
     */
    public Chromosome(int[] genes, int size, int nbIntervenants, List<Mission> missions, List<Intervenant> intervenants, double[][] distances) {
        this.genes = genes;
        this.size = size;
        this.nbIntervenants = nbIntervenants;

        ordreMissions = ordreMission(this.genes, missions);

        this.missions = missions;
        this.intervenants = intervenants;
        this.distances = distances;
    }

    /**
     *constructeur
     * @param tailleChromosome
     * @param nbIntervenants
     * @param missions
     * @param intervenants
     * @param distances
     */
    public Chromosome(int tailleChromosome, int nbIntervenants, List<Mission> missions, List<Intervenant> intervenants, double[][] distances) {
        size = tailleChromosome;
        genes = new int[tailleChromosome];
        this.nbIntervenants = nbIntervenants;

        this.missions = missions;
        this.intervenants = intervenants;
        this.distances = distances;

        genes = contruireSolutionValide();

        ordreMissions = ordreMission(this.genes, missions);
    }

    /**
     * fonction de mise a jour de l'odre des missions
     */
    public void updateOrdreMissions() {
        ordreMissions = ordreMission(this.genes, missions);
    }

    /**
     * fonction d'evaluation du fistness d'un employe
     * @return 0f
     */
    public float evaluateFitnessEmployee() {
        return 0f;
    }

    /**
     * fonction qui permet l'affichage du tableau des genes du chromosome
     */
    public void afficher() {
        for (int i=0; i<genes.length; i++) {
            System.out.print(genes[i] + " ");
        }
        System.out.println();
    }

    /**
     * fonction qui permet l'affichage final de la solution
     */
    public void afficherDetails() {
        ArrayList<ArrayList<ArrayList<Integer>>> missionsParJour = ordreMission(this.genes, missions);

        System.out.println("Affichage des tournées de la semaine en détail : ");
        for (int i=0; i<nbIntervenants; i++) {
            System.out.println("Intervenant " + (i+1) + " :");
            for (int j=0; j<5; j++) {
                System.out.print("Jour " + (j+1) + " : ");
                ArrayList<Integer> currentListeMissions = missionsParJour.get(i).get(j);
                for (int k=0; k<currentListeMissions.size(); k++) {
                    System.out.print(currentListeMissions.get(k)+1 + " ");
                }
                System.out.println();
            }
        }
    }

    /**
     * fonction qui permet d'echanger deux genes dans le tableau genes
     * @param indexA
     * @param indexB
     */
    public void echange2genes(int indexA, int indexB) {
        int temp = genes[indexA];
        genes[indexA] = genes[indexB];
        genes[indexB] = temp;

    }

    /**
     * fonction d'evaluation du premier critere
     * @return fitness: le fitness de la solution
     */
    public double evaluerPremierCritere() {

        // Calcul des constantes
        double total = 0;
        for (int i=0; i<intervenants.size(); i++) {
            total += intervenants.get(i).getQuota();
        }

        double moyenneQuota = total/intervenants.size();

        double constanteQuota = 100 / moyenneQuota;

        double constanteHeuresSupTolerees = 100.0 / 10;

        double totalDistances = 0;
        for (int i=0; i < distances.length-1; i++) {
            totalDistances += distances[0][i+1] + distances[i+1][0];
        }
        double moyenne = (totalDistances / (double) intervenants.size());

        double constanteMoyenneDistances = 100.0 / (totalDistances / (double) intervenants.size());

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

        double ecartTypeHeuresSup = Utils.calculerEcartType(heuresSup);

        // Calculer l'écart-type des heures non-travaillées
        double[] heuresNonTravaillees = new double[intervenants.size()];

        for (int i=0; i<heuresTravaillees.length; i++) {
            heuresNonTravaillees[i] = (intervenants.get(i).getQuota() - heuresTravaillees[i]) > 0 ? (intervenants.get(i).getQuota() - heuresTravaillees[i]) : 0;
        }

        double ecartTypeHeureNonTravaillees = Utils.calculerEcartType(heuresNonTravaillees);

        double ecartTypeDistances = Utils.calculerEcartType(distancesParIntervenant);

        // On met à jour le fitness de la solution et on ajoute les éventuelles pénalités des contraintes souples
        fitness = fitness1 = ((constanteQuota*ecartTypeHeureNonTravaillees + constanteHeuresSupTolerees*ecartTypeHeuresSup + constanteMoyenneDistances*ecartTypeDistances)/3.0) + contrainteSouple();

        return fitness;
    }

    /**
     * fonction d'evaluation du deuxieme critere
     * @return fitness: le fitness de la solution
     */
    public double evaluerDeuxiemeCritere() {
        double alpha=100.0/size;
        int compteur=0;
        for(int i=0; i<size;i++)
        {
            if (missions.get(i).getSpecialite()!=intervenants.get(genes[i] - 1).getSpecialite())
            {
                compteur++;
            }
        }
        fitness = fitness2 = alpha*compteur + contrainteSouple();

        return fitness;
    }

    /**
     * fonction d'evaluation du troisieme critere
     * @return fitness: le fitness de la solution
     */
    public double evaluerTroisiemeCritere() {
        int sumWOH=0;
        double Beta=100.0/45;
        double moyenneK, moyD,maxD=-1;
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
        for(int i=0;i<nbIntervenants;i++)
        {
            sumWOH+=(intervenants.get(i).getQuota()-heuresTravaillees[i])+ (heuresTravaillees[i]-intervenants.get(i).getQuota());
        }
        double totalDistances = 0;
        for (int i=0; i < distances.length-1; i++) {
            totalDistances += distances[0][i+1] + distances[i+1][0];
        }

        double SUM=0;
        for(int i=0;i<distancesParIntervenant.length;i++)
        {
            if(distancesParIntervenant[i]>maxD)
            {
                maxD=distancesParIntervenant[i];
            }
            SUM+=distancesParIntervenant[i];
        }

        moyD=SUM/distancesParIntervenant.length;

        moyenneK = 100.0 / (totalDistances / (double) intervenants.size());
        fitness = fitness3 = ((Beta*sumWOH+moyenneK*moyD+moyenneK*maxD)/3) + contrainteSouple();

        return fitness;
    }

    /**
     * fonction qui calcule la distance parcourue par chaque intervenant durant la semaine
     * @param distances
     * @param missions
     * @return res : tableau des distances
     */
    private double[] distancesParIntervenant(double[][] distances, List<Mission> missions) {
        double[] res = new double[nbIntervenants];

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

    /**
     * fonction qui construit le tableau composé de tableaux representatn chaque intervenant compose de tableau representant les jours de la semaine
     * @param arr
     * @param missions
     * @return ordreMission le tableau
     */
    private ArrayList<ArrayList<ArrayList<Integer>>> ordreMission(int[] arr, List<Mission> missions) {
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

            for (int j=0; j<arr.length; j++) {

                if (arr[j] == i+1) {
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

    /**
     * fonction permettant la construction des chromosomes de la premiere population
     * @return res : tableau qui represente un chromosome (equivalent a au tableau genes)
     */
    private int[] contruireSolutionValide() {
        int[] res = new int[size];

        Random rand = new Random();

        for (int i=0; i<res.length; i++) {
            int randIntervenant = rand.nextInt(nbIntervenants) + 1;
            boolean stop = false;
            while (!stop) {
                if (missions.get(i).getCompetence() != intervenants.get(randIntervenant - 1).getCompetence()) {
                    randIntervenant = rand.nextInt(nbIntervenants) + 1;
                } else {
                    stop = true;
                    res[i] = randIntervenant;
                }
            }

        }
        return res;
    }

    /**
     * fonction de verification de la validite d'un chromosome (une solution)
     * @return true ou false
     */
    public boolean estValide() {

        // Vérifier que les missions sont bien assignées à des intervenants ayant la même compétence
        for (int i=0; i<genes.length; i++) {
            if (missions.get(i).getCompetence() != intervenants.get(genes[i] - 1).getCompetence()) {
                return false;
            }
        }

        return true;
    }

    public int getSize() {
        return size;
    }

    public int[] getGenes() {
        return genes;
    }

    public Chromosome copier() {
        return new Chromosome(genes, size, nbIntervenants, missions, intervenants, distances);
    }

    public void copier(Chromosome chromosome) {
        this.genes = Arrays.copyOf(chromosome.getGenes(), size);
    }

    public int[] copyGenes() {
        return Arrays.copyOf(genes, size);
    }

    public double getFitness() {
        return fitness;
    }

    public double getFitness1() {
        return fitness1;
    }

    public double getFitness2() {
        return fitness2;
    }

    public double getFitness3() {
        return fitness3;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public void setGenes(int[] genes) {
        this.genes = Arrays.copyOf(genes, genes.length);
    }

    /**
     * fonction qui permet de copier un chromosome
     * @return clone: le clone
     */
    @Override
    public Chromosome clone() {
        try {
            Chromosome clone = (Chromosome) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            clone.genes = Arrays.copyOf(genes, size);
            clone.size = size;
            clone.missions = missions;
            clone.intervenants = intervenants;
            clone.distances = distances;

            clone.ordreMissions = this.ordreMissions;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    /**
     * fonction qui permet la verification des contraintes souples
     * @return penalite : la valeur des penalites du chromosome
     */
    public int contrainteSouple(){
        int penalite=0;
        int missionIndex, intervenantIndex;

        ArrayList<ArrayList<ArrayList<Integer>>> missionsParJour = ordreMission(this.genes, missions);

        for (int i=0; i<nbIntervenants; i++) {
            for (int j=0; j<5; j++) {
                ArrayList<Integer> currentListeMissions = missionsParJour.get(i).get(j);
                for (int k=0; k<currentListeMissions.size()-1; k++) {
                    if (missions.get(currentListeMissions.get(k)).getHeure_fin() >= missions.get(currentListeMissions.get(k+1)).getHeure_debut()) {
                        penalite += 10;
                    }
                }
            }
        }

        //**********************contrainte 5**************************** 1h pause midi
        for (int i=0; i<nbIntervenants; i++) {
            for (int j=0; j<5; j++) {
                boolean tempsSuffisantAMidi = false;
                ArrayList<Integer> currentListeMissions = missionsParJour.get(i).get(j);
                for (int k=0; k<currentListeMissions.size() - 1; k++) {
                    if (missions.get(currentListeMissions.get(k)).getHeure_debut() >= 720 && missions.get(currentListeMissions.get(k)).getHeure_debut() <= 840
                         || missions.get(currentListeMissions.get(k)).getHeure_fin() >= 720 && missions.get(currentListeMissions.get(k)).getHeure_fin() <= 840   ) {
                        if (missions.get(currentListeMissions.get(k)).getHeure_debut() - 720 >= 60) {
                            tempsSuffisantAMidi = true;
                            break;
                        }

                        if (missions.get(currentListeMissions.get(k)).getHeure_fin() - 720 >= 60) {
                            tempsSuffisantAMidi = true;
                            break;
                        }

                        if (missions.get(currentListeMissions.get(k+1)).getHeure_debut() <= 840) {
                            if (missions.get(currentListeMissions.get(k+1)).getHeure_debut() - missions.get(currentListeMissions.get(k)).getHeure_fin() >= 60) {
                                tempsSuffisantAMidi = true;
                                break;
                            }
                        } else {
                            if (840 - missions.get(currentListeMissions.get(k)).getHeure_fin() >= 60) {
                                tempsSuffisantAMidi = true;
                                break;
                            }
                        }
                    } else if (missions.get(currentListeMissions.get(k+1)).getHeure_debut() >= 720 && missions.get(currentListeMissions.get(k+1)).getHeure_debut() <= 840 ||
                            missions.get(currentListeMissions.get(k+1)).getHeure_fin() >= 720 && missions.get(currentListeMissions.get(k+1)).getHeure_fin() <= 840) {
                            if (missions.get(currentListeMissions.get(k+1)).getHeure_debut() - 720 >= 60) {
                                tempsSuffisantAMidi = true;
                                break;
                            }

                            if (missions.get(currentListeMissions.get(k+1)).getHeure_fin() - 720 >= 60) {
                                tempsSuffisantAMidi = true;
                                break;
                            }

                            if (840 - missions.get(currentListeMissions.get(k+1)).getHeure_fin() >= 60) {
                                tempsSuffisantAMidi = true;
                                break;
                            }
                    } else {
                        tempsSuffisantAMidi = true;
                        break;
                    }
                }

                if (!tempsSuffisantAMidi) {
                    penalite += 5;
                }

            }
        }

        //**********************contrainte 6**************************** 8 ou 6 heures max par jour

        for (int i=0; i<nbIntervenants; i++) {
            int heureTot;
            int heureMaxParJour = intervenants.get(i).getQuota() == 24 ? 360 : 480;
            for (int j=0; j<5; j++) {
                ArrayList<Integer> currentListeMissions = missionsParJour.get(i).get(j);
                heureTot=0;
                if (!currentListeMissions.isEmpty()) {
                    // Première mission au SESSAD
                    heureTot += distances[0][currentListeMissions.get(0) + 1] / (50*16.667);
                    // Temps de travail de chaque mission
                    for (int k=0; k<currentListeMissions.size(); k++) {
                        heureTot+=missions.get(currentListeMissions.get(k)).getHeure_fin()-missions.get(currentListeMissions.get(k)).getHeure_debut();
                    }
                    // Dernière mission au SESSAD (Fin de journée)
                    heureTot += distances[currentListeMissions.get(currentListeMissions.size() - 1) + 1][0] / (50*16.667);
                    if (heureTot>heureMaxParJour) {
                        // System.out.println("Contrainte 6");
                        penalite+=7;
                    }
                }

            }
        }
        //**********************contrainte 7**************************** 2h sup max/jour et 10h sup max/semaine

        // On vérifie si on dépasse pas 2h d'heure sup par jour
        for (int i=0; i<nbIntervenants; i++) {
            double heureTot;
            int heuresMaxParJour = intervenants.get(i).getQuota() == 24 ? 360 : 480;
            double heureTotSemaine = 0;
            for (int j=0; j<5; j++) {
                ArrayList<Integer> currentListeMissions = missionsParJour.get(i).get(j);
                heureTot=0;
                if (!currentListeMissions.isEmpty()) {
                    heureTot += distances[0][currentListeMissions.get(0) + 1] / (50*16.667); // Sessad à première mission
                    // Distance entre chaque mission
                    for (int k=0; k<currentListeMissions.size(); k++) {
                        heureTot+=missions.get(currentListeMissions.get(k)).getHeure_fin()-missions.get(currentListeMissions.get(k)).getHeure_debut();
                    }
                    // Dernière mission au SESSAD
                    heureTot += distances[currentListeMissions.get(currentListeMissions.size() - 1) + 1][0] / (50*16.667);

                    heureTotSemaine += heureTot;

                    // System.out.println("Heure tot pour intervenant " + i + " jour " + j + " = " + heureTot);

                    if (heureTot-heuresMaxParJour>120) {   //480 pour 8h par jour
                        // System.out.println("Contrainte 7");
                        penalite+=5;
                    }
                }

            }
            // System.out.println("Heure tot semaine pour intervenant " + i + " = " + heureTotSemaine);
            // Si les heures sup de toute la semaine dépassent 10h alors on pénalise
            if (heureTotSemaine-(intervenants.get(i).getQuota()*60) > 600) {
                // System.out.println("Contrainte 7");
                penalite+=5;
            }
        }

        //**********************contrainte 8**************************** amplitude (pas plus de 12h)*
        for (int i=0; i<nbIntervenants; i++) {
            for (int j = 0; j < 5; j++) {
                ArrayList<Integer> currentListeMissions = missionsParJour.get(i).get(j);
                if (currentListeMissions.size() >= 2) {
                    if(missions.get(currentListeMissions.get(currentListeMissions.size()-1)).getHeure_fin() - missions.get(currentListeMissions.get(0)).getHeure_debut()>720)
                    {
                        penalite+=5;
                    }
                }
            }
        }

        //********************** contrainte 9 **************************** temps pour se deplacer 50km/h


        for (int i=0; i<nbIntervenants; i++) {
            for (int j=0; j<5; j++) {
                ArrayList<Integer> currentListeMissions = missionsParJour.get(i).get(j);
                for (int k=0; k<currentListeMissions.size()-1; k++) {
                    if (missions.get(currentListeMissions.get(k+1)).getHeure_debut() - missions.get(currentListeMissions.get(k)).getHeure_fin() <= (distances[currentListeMissions.get(k) + 1][currentListeMissions.get(k+1) + 1] /(50*16.667))) {
                        penalite+=3;
                    }
                }
            }
        }

        return penalite;
    }

    @Override
    public String toString() {
        return Arrays.toString(genes);
    }
}
