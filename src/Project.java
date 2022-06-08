import entities.Intervenant;
import entities.Mission;

import java.util.Arrays;
import java.util.List;

public class Project {
    public static void main(String[] args) {
        /*Population population = new Population(5, 40, 4);
        population.afficher();*/

        GeneticAlgorithm ga = new GeneticAlgorithm(45, 3);

        Chromosome c = new Chromosome(45, 4);
        c.afficher();
        double fitness = c.evaluerPremierCritere(45);

        System.out.println("Fitness = " + fitness);

        /*List<Mission> missions = ga.constructionMissions("src/instances/Missions.csv");
        System.out.println(Arrays.toString(missions.toArray()));

        List<Intervenant> intervenants = ga.constructionIntervenants("src/instances/Intervenants.csv");
        System.out.println(Arrays.toString(intervenants.toArray()));*/


        /*Chromosome c1 = new Chromosome(10, 3);
        Chromosome c2 = new Chromosome(10, 3);

        c1.afficher();
        c2.afficher();

        System.out.println("Nous allons croiser ces deux gènes avec la méthode 1X");

        Chromosome[] fils = GeneticAlgorithm.croisement1X(c1, c2);
        System.out.print("Fils 1 : ");
        fils[0].afficher();

        System.out.print("Fils 2 : ");
        fils[1].afficher();*/

    }
}
