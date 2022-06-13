import java.util.Arrays;
import java.util.Scanner;

public class Project {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Nombre de missions : ");
        int nbMissions = sc.nextInt();
        System.out.print("Nombre d'intervenants : ");
        int nbIntervenants = sc.nextInt();
        System.out.print("Temps d'éxécution (en secondes) : ");
        int tempsExecution = sc.nextInt();



        GeneticAlgorithm ga = new GeneticAlgorithm(nbMissions, nbIntervenants, tempsExecution);

        Chromosome best = ga.optimiser();
        System.out.println();
        best.afficherDetails();
        System.out.println();
        System.out.println("Fitness 1 : " + best.evaluerPremierCritere());
        System.out.println("Fitness 2 : " + best.evaluerDeuxiemeCritere());
        System.out.println("Fitness 3 : " + best.evaluerTroisiemeCritere());

    }
}
