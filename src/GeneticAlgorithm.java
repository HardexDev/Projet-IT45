import entities.Intervenant;
import entities.Mission;
import java.util.*;

/**
 * Classe principale qui implémente l'algorithme génétique
 */
public class GeneticAlgorithm {
    private int nbGenerations; // Nombre de génération
    private int taillePop; // Taille de la population
    private double tauxCroisement; //
    private double tauxMutation;
    private int tailleChromosome;
    private Population population;
    private int nombreMissions;
    private int nombreIntervenants;
    private long tempsExecution;

    private double[][] distances;
    private List<Mission> missions;
    private List<Intervenant> intervenants;

    /**
     *constructeur
     * @param nombreMissions
     * @param nombreIntervenants
     * @param tempsExecution
     */
    public GeneticAlgorithm(int nombreMissions, int nombreIntervenants, int tempsExecution) {
        nbGenerations = 150000;
        taillePop = 100;
        tauxCroisement = 0.8;
        tauxMutation = 0.3;
        tailleChromosome = nombreMissions;
        this.nombreIntervenants = nombreIntervenants;
        this.nombreMissions = nombreMissions;
        this.tempsExecution = tempsExecution;

        String basePath = System.getProperty("user.dir").contains("src") ? System.getProperty("user.dir") + "/instances/" + tailleChromosome + "-" + nombreIntervenants: System.getProperty("user.dir") + "/src/instances/" + tailleChromosome + "-" + nombreIntervenants;
        distances = Utils.constructionDistance( basePath + "/Distances.csv", tailleChromosome);
        missions = Utils.constructionMissions(basePath + "/Missions.csv");
        intervenants = Utils.constructionIntervenants(basePath + "/Intervenants.csv");

        population = new Population(taillePop, tailleChromosome, nombreIntervenants, missions, intervenants, distances);
    }

    /**
     * fonction qui execute l'algorithme genetique avec le premier critere puis filtre les solution avec le second puis le troisieme critere
     * @return solutionsTroisiemeCritere.get(0)
     */
    public Chromosome optimiser() {
        int amelioration = 0;
        int nbEnfantsNonValides = 0;
        int nbEnfantsValides = 0;

        List<Chromosome> solutionsPremierCritere = new ArrayList<>();
        List<Chromosome> solutionsDeuxiemeCritere = new ArrayList<>();
        List<Chromosome> solutionsTroisiemeCritere = new ArrayList<>();

        Chromosome p1, p2;

        long t = System.currentTimeMillis();
        long end = t + tempsExecution;

        Random rand = new Random();

        int g = 0;
        while (System.currentTimeMillis() < end) {
            for (Chromosome c : population.getIndividus()) {
                c.evaluerPremierCritere();
            }

            population.ordonner();

            double meilleurFitness = population.getIndividus()[0].getFitness();

            System.out.println("Lancement n°" + g);
            for (int i=0; i<nbGenerations; i++) {
                p1 = population.selectionRoulette();
                p2 = population.selectionRoulette();

                Chromosome[] fils = croisement1X(p1, p2);
                Chromosome c1 = fils[0].copier();
                c1.updateOrdreMissions();
                Chromosome c2 = fils[1].copier();
                c2.updateOrdreMissions();

                if (!c1.estValide()) {
                    c1 = new Chromosome(tailleChromosome, nombreIntervenants, missions, intervenants, distances);
                }

                if (!c2.estValide()) {
                    c2 = new Chromosome(tailleChromosome, nombreIntervenants, missions, intervenants, distances);
                }

                // Mutation enfant 1 si au dessus du taux de mutation
                if (rand.nextInt(1000)/1000.0 < tauxMutation) {
                    int geneA = rand.nextInt(nombreMissions-1);
                    int geneB = rand.nextInt(nombreMissions-1);
                    c1.echange2genes(geneA, geneB);

                    if (!c1.estValide()) {
                        c1 = new Chromosome(tailleChromosome, nombreIntervenants, missions, intervenants, distances);
                    }
                }

                // Mutation enfant 2 si au dessus du taux de mutation
                if (rand.nextInt(1000)/1000.0 < tauxMutation) {
                    int geneA = rand.nextInt(nombreMissions-1);
                    int geneB = rand.nextInt(nombreMissions-1);
                    c2.echange2genes(geneA, geneB);
                    if (!c2.estValide()) {
                        c2 = new Chromosome(tailleChromosome, nombreIntervenants, missions, intervenants, distances);
                    }
                }

                c1.evaluerPremierCritere();
                c2.evaluerPremierCritere();

                population.remplacementRoulette(c1);
                population.remplacementRoulette(c2);

                population.reordonner();

                if (population.getIndividus()[population.getOrdre()[0]].getFitness() < meilleurFitness) {
                    meilleurFitness = population.getIndividus()[population.getOrdre()[0]].getFitness();
                    amelioration = i;
                }
            }

            g++;

            solutionsPremierCritere.add(population.getIndividus()[population.getOrdre()[0]]);
            population = new Population(taillePop, tailleChromosome, nombreIntervenants, missions, intervenants, distances);

        }


        // Trier les solutions du critère 1 par fitness
        solutionsPremierCritere.sort((o1, o2) -> {
            if (o1.getFitness() < o2.getFitness()) {
                return -1;
            } else if (o1.getFitness() == o2.getFitness()) {
                return 0;
            }

            return 1;
        });

        // On récupère les 10% des meilleures solutions du premier critère
        List<Chromosome> meilleuresSolutionsPremierCritere = new ArrayList<>();
        double bound = solutionsPremierCritere.size() * 0.3;
        for (int i=0; i<bound; ++i) {
            meilleuresSolutionsPremierCritere.add(solutionsPremierCritere.get(i));
        }

        // On évalue les meilleures solution du critère 1 avec le critère 2
        for (Chromosome c : meilleuresSolutionsPremierCritere) {
            c.evaluerDeuxiemeCritere();
            solutionsDeuxiemeCritere.add(c);
        }

        // Trier les solutions du critère 2 par fitness
        solutionsDeuxiemeCritere.sort((o1, o2) -> {
                if (o1.getFitness() < o2.getFitness()) {
                    return -1;
                } else if (o1.getFitness() == o2.getFitness()) {
                    return 0;
                }

                return 1;
        });

        // On récupère les 10% des meilleures solutions du deuxième critère
        List<Chromosome> meilleuresSolutionsDeuxiemeCritere = new ArrayList<>();
        double bound2 = solutionsDeuxiemeCritere.size() * 0.5;
        for (int i=0; i<bound2; ++i) {
            meilleuresSolutionsDeuxiemeCritere.add(solutionsDeuxiemeCritere.get(i));
        }



        // On évalue les meilleures solutions du critère 2 avec le critère 3
        for (Chromosome c : meilleuresSolutionsDeuxiemeCritere) {
            c.evaluerTroisiemeCritere();
            solutionsTroisiemeCritere.add(c);
        }

        // Trier les solutions du critère 3 par fitness
        solutionsTroisiemeCritere.sort((o1, o2) -> {
            if (o1.getFitness() < o2.getFitness()) {
                return -1;
            } else if (o1.getFitness() == o2.getFitness()) {
                return 0;
            }

            return 1;
        });

        return solutionsTroisiemeCritere.get(0);
    }

    /**
     * fonction qui effectue un croisement en un point
     * @param p1
     * @param p2
     * @return res: un tableau des deux chromosomes fils
     */
    public static Chromosome[] croisement1X(Chromosome p1, Chromosome p2) {
        int nbGenes = p1.getSize();
        Chromosome c1 = p1.clone();
        Chromosome c2 = p2.clone();
        int[] c1Genes1 = c1.copyGenes();
        int[] c1Genes = c1.copyGenes();
        int[] c2Genes = c2.copyGenes();

        int point = Utils.rand_int(nbGenes);    //point du croisement choisi aleatoirement

        for (int i=point+1; i<nbGenes; i++) {
            c1Genes[i] = c2Genes[i];
        }

        for (int i=point+1; i<nbGenes; i++) {
            c2Genes[i] = c1Genes1[i];
        }

        c1.setGenes(c1Genes);
        c2.setGenes(c2Genes);

        Chromosome[] res = new Chromosome[2];
        res[0] = c1;
        res[1] = c2;

        return res;
    }

}
