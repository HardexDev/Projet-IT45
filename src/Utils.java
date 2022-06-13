import entities.Competence;
import entities.Intervenant;
import entities.Mission;
import entities.Specialite;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static double[][] constructionDistance(String nomFichier, int nombreMissions) {
        double[][] res = new double[nombreMissions+1][nombreMissions+1];

        Path pathToFile = Paths.get(nomFichier);

        try (BufferedReader br = Files.newBufferedReader(pathToFile.toAbsolutePath(),
                StandardCharsets.US_ASCII)) {

            String line = br.readLine();

            int lineCount = 0;

            while (line != null) {
                String[] attributes = line.split(",");

                for (int i=0; i<attributes.length; i++) {
                    res[lineCount][i] = Double.parseDouble(attributes[i]);
                }

                line = br.readLine();
                lineCount++;
            }

        } catch (IOException ioe) {
            System.err.println("Impossible d'ouvrir le fichier distances");
        }


        return res;
    }

    public static List<Mission> constructionMissions(String nomFichier) {
        List<Mission> missions = new ArrayList<>();
        Path pathToFile = Paths.get(nomFichier);

        try (BufferedReader br = Files.newBufferedReader(pathToFile.toAbsolutePath(),
                StandardCharsets.US_ASCII)) {

            String line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(",");

                Mission mission = new Mission(Integer.parseInt(attributes[0]), Integer.parseInt(attributes[1]), Integer.parseInt(attributes[2]),
                        Integer.parseInt(attributes[3]), Competence.valueOf(attributes[4]), Specialite.valueOf(attributes[5]));

                missions.add(mission);

                line = br.readLine();
            }

        } catch (IOException ioe) {
            System.err.println("Impossible d'ouvrir le fichier missions");
        }

        return missions;
    }

    public static List<Intervenant> constructionIntervenants(String nomFichier) {
        List<Intervenant> intervenants = new ArrayList<>();
        Path pathToFile = Paths.get(nomFichier);

        try (BufferedReader br = Files.newBufferedReader(pathToFile.toAbsolutePath(),
                StandardCharsets.US_ASCII)) {

            String line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(",");

                Intervenant intervenant = new Intervenant(Integer.parseInt(attributes[0]), Competence.valueOf(attributes[1]), Specialite.valueOf(attributes[2]),
                        Integer.parseInt(attributes[3]));

                intervenants.add(intervenant);

                line = br.readLine();
            }

        } catch (IOException ioe) {
            System.err.println("Impossible d'ouvrir le fichier intervenants");
        }

        return intervenants;
    }

    public static double calculerEcartType(double[] values) {
        double moyenneHeuresSup = Arrays.stream(values).sum() / values.length;
        double ecartTypeHeuresSup = 0;

        for (int i=0; i<values.length; i++) {
            ecartTypeHeuresSup += Math.pow(values[i] - moyenneHeuresSup, 2);
        }

        return Math.sqrt(ecartTypeHeuresSup/values.length);
    }

    public static int rand_int(int borne) {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt(borne);
    }

    public static int rand_in_bounds(int a, int b) {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt(b-a) + a;
    }
}
