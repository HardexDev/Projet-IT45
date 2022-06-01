import java.awt.*;

public class Project {
    public static void main(String[] args) {
        /*Population population = new Population(5, 80, 3);
        population.afficher();*/

        Chromosome c1 = new Chromosome(5, 2);
        Chromosome c2 = new Chromosome(5, 2);

        c1.afficher();
        c2.afficher();

        System.out.println("Nous allons croiser ces deux gènes avec la méthode 1X");

        Chromosome[] fils = GeneticAlgorithm.croisement1X(c1, c2);
        System.out.print("Fils 1 : ");
        fils[0].afficher();

        System.out.print("Fils 2 : ");
        fils[1].afficher();

    }
}
