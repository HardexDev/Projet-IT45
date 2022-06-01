public class Random {
    public static int rand_int(int borne) {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt(borne);
    }

    public static int rand_in_bounds(int a, int b) {
        java.util.Random rand = new java.util.Random();
        return rand.nextInt(b-a) + a;
    }
}
