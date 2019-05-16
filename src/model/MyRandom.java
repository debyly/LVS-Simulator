package model;

import org.apache.poi.ss.format.SimpleFraction;

import java.util.Random;

class MyRandom {

    private static int sum(int[] ints){

        int res = 0;
        for (int i : ints) res += i;
        return res;
    }

    private static SimpleFraction shorted(SimpleFraction a){

        long denominator = a.getDenominator();
        long numerator = a.getNumerator();
        long gcd = gcd(denominator, numerator);

        return new SimpleFraction((int)(numerator/gcd),(int)(denominator/gcd));
    }

    private static SimpleFraction sum(SimpleFraction a, SimpleFraction b){

        return shorted(new SimpleFraction(
                a.getNumerator()*b.getDenominator() + b.getNumerator()*a.getDenominator(),
                a.getDenominator() * b.getDenominator()));
    }

    private static SimpleFraction sum (SimpleFraction[] fractions){

        SimpleFraction result = new SimpleFraction(0,1);

        for (SimpleFraction fraction : fractions)
            result = sum(result, fraction);

        return result;
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

    private static int[] getProportions(
            int[] fractions){

        long[] longs = new long[fractions.length];
        int[] out = new int[fractions.length];

        for (int i = 0; i < longs.length; i++)
            longs[i] = fractions[i];

        long gcd = gcd(longs);

        for (int i = 0; i < longs.length; i++)
            longs[i] = (int) (longs[i] / gcd);

        long lcm = lcm(longs);

        for (int i = 0; i < longs.length; i++)
            out[i] = (int)( lcm / longs[i]);

        return out;
    }

    private static int getBalancedRandom(int[] probs) {

        SimpleFraction[] fracts = new SimpleFraction[probs.length];

        for (int i = 0; i < probs.length; i++)
            fracts[i] = (new SimpleFraction(
                    1, probs[i]));

        SimpleFraction totalProb = sum(fracts);

        Random r = new Random();
        if (r.nextInt(totalProb.getDenominator())
                >= totalProb.getNumerator()) return 0;

        int[] denoms = new int[fracts.length];
        for (int i = 0; i < fracts.length; i++)
            denoms[i] = fracts[i].getDenominator();

        int[] proportions = getProportions(denoms);
        int sum = 0;
        int random = r.nextInt(sum(proportions));

        for (int i = 0; i < proportions.length; i++)
            if (random < (sum += proportions[i]))
                return i + 1;

        return proportions.length;
    }

    static TerminalDevice.DeviceState getRandomState(int genProb, int denProb, int failProb, int busyProb){

        int[] probs = new int[]{genProb,denProb,failProb,busyProb};

        int randomState = getBalancedRandom(probs);

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
