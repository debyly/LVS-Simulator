package model;
import java.util.Random;

class MyRandom {

    private static int sum(long[] ints){

        int res = 0;
        for (long i : ints) res += i;
        return res;
    }

    private static double sum(double[] doubles){

        double res = .0;
        for (double d : doubles) res += d;
        return res;
    }

    private static long gcd(long a, long b){
        return b == 0 ? a : gcd(b,a % b);
    }

    private static long lcm(long a, long b){
        return a*b / gcd(a,b);
    }

    private static long gcd(long[] ints){

        if (ints.length == 0) return 1;
        long result = ints[0];
        for (long i : ints) result = gcd(result, i);
        return result;
    }

    private static long lcm(long[] ints){

        if (ints.length == 0) return 1;
        long result = ints[0];
        for (long i : ints) result = lcm(result, i);
        return result;
    }

    private static long[] getProportions(
            long[] fractions){

        long[] longs = new long[fractions.length];
        long[] out = new long[fractions.length];

        System.arraycopy(fractions, 0, longs, 0, longs.length);

        long gcd = gcd(longs);

        for (int i = 0; i < longs.length; i++)
            longs[i] = (longs[i] / gcd);

        long lcm = lcm(longs);

        for (int i = 0; i < longs.length; i++)
            out[i] = (int)( lcm / longs[i]);

        return out;
    }

    private static int getNBRandom(double[] probs){

        long [] denoms = new long[probs.length];
        for (int i = 0; i < probs.length; i++) {
            denoms[i] = Math.round(1 / probs[i]);
        }

        Random r = new Random();

        if (r.nextDouble() < 1.0 - sum(probs))
            return 0;

        long[] proportions = getProportions(denoms);
        int sum = 0;
        int random = r.nextInt(sum(proportions));

        for (int i = 0; i < proportions.length; i++)
            if (random < (sum += proportions[i]))
                return i + 1;

        return proportions.length;
    }

    static TerminalDevice.DeviceState getRandomState(
            double genProb, double denProb, double failProb, double busyProb){

        int randomState = getNBRandom(new double[]{genProb,denProb,failProb,busyProb});

        switch (randomState){
            case 1:
                return TerminalDevice.DeviceState.GENERATOR;
            case 2:
                return TerminalDevice.DeviceState.DENIAL;
            case 3:
                return TerminalDevice.DeviceState.FAILURE;
            case 4:
                return TerminalDevice.DeviceState.BUSY;
        }

        return TerminalDevice.DeviceState.WORKING;

    }
}
