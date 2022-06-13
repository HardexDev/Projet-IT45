import java.util.Arrays;
import java.util.Scanner;

public class Project {
    public static void main(String[] args) {
        int nbMissions,nbIntervenants;

        Scanner sc = new Scanner(System.in);
        do {
            System.out.print("Nombre de missions : ");
            nbMissions = sc.nextInt();
            System.out.print("Nombre d'intervenants : ");
            nbIntervenants = sc.nextInt();
        } while (!(((nbIntervenants == 4) && (nbMissions ==45)) || ((nbIntervenants==6 ) && (nbMissions ==96)) ||( (nbIntervenants==10)  && (nbMissions ==100))));

        int tempsExecution;
        do {
            System.out.print("Temps d'éxécution (en milisecondes) : ");
            tempsExecution = (int)sc.nextInt();
        }while (!(tempsExecution>2 && tempsExecution < 1000000000));




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
