public class Solution {
    private Chromosome[] chromosomes;

    public Solution() {
        this.chromosomes = new Chromosome[5];
    }

    public Solution(int tailleChromosome, int nbIntervenants) {
        chromosomes = new Chromosome[5];
        for (int i=0; i<chromosomes.length; i++) {
            chromosomes[i] = new Chromosome(tailleChromosome, nbIntervenants);
        }
    }

    public void afficher() {
        System.out.println("--------- Solution ----------");
        System.out.println("LUNDI : ");
        chromosomes[0].afficher();
        System.out.println("MARDI : ");
        chromosomes[1].afficher();
        System.out.println("MERCREDI : ");
        chromosomes[2].afficher();
        System.out.println("JEUDI : ");
        chromosomes[3].afficher();
        System.out.println("VENDREDI : ");
        chromosomes[4].afficher();
    }
}
