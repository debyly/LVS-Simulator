package model;

import org.apache.poi.ss.format.SimpleFraction;

import java.util.Random;

public class MyRandom {

    private static int eventsAmount = 0;

    private static int steps = 0;
    private static int[] events = new int[5];
    private static int[] mEvents = new int[5];


    private static SimpleFraction shorted(SimpleFraction a){

        long denominator = a.getDenominator();
        long numerator = a.getNumerator();

        long gcd = gcd(denominator, numerator);

        while (gcd > 1){

            denominator /= gcd;
            numerator /= gcd;
            gcd = gcd(denominator, numerator);
        }

        return new SimpleFraction((int)numerator,(int)denominator);
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

    private static SimpleFraction mul (SimpleFraction a, int mul){

        return shorted(new SimpleFraction(mul*a.getNumerator(),
                mul*a.getDenominator()));
    }

    private static SimpleFraction mul (SimpleFraction a, SimpleFraction b){

        return shorted(new SimpleFraction(a.getNumerator()*b.getNumerator(),
                a.getDenominator() * b.getDenominator()));
    }
    private static SimpleFraction div (SimpleFraction a, int div){

        return shorted(new SimpleFraction(a.getNumerator(),
                a.getDenominator() * div));
    }

    private static SimpleFraction div (SimpleFraction a, SimpleFraction b){

        return shorted(new SimpleFraction(a.getNumerator()* b.getDenominator(),
                a.getDenominator() * b.getNumerator()));
    }

    private static long gcd(long a, long b){
        return b == 0 ? a : gcd(b,a % b);
    }

    private static long gcd(long[] ints){

        if (ints.length == 0) return 1;
        long result = ints[0];
        for (long i : ints)
            result = gcd(result, i);
        return result;
    }

    public static long lcm(long a, long b){
        return a*b / gcd(a,b);
    }

    public static long lcm(long[] ints){

        if (ints.length == 0) return 1;
        long result = ints[0];
        for (long i : ints)
            result = lcm(result, i);
        return result;
    }

    public static int[] getRoulette(
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

    private static int sum(int[] ints){

        int res = 0;
        for (int i : ints) res += i;
        return res;
    }

    public static int getBalancedRandom(int[] probs) {

        SimpleFraction[] fractions = new SimpleFraction[probs.length];

        eventsAmount++;

        for (int i = 0; i < probs.length; i++)
            fractions[i] = (new SimpleFraction(1, probs[i]));

        SimpleFraction totalProb = sum(fractions);

        Random r = new Random();

        if (r.nextInt(totalProb.getDenominator()) >= totalProb.getNumerator()){
            events[0] ++;
            return 0;
        }

        int[] denoms = new int[fractions.length];
        for (int i = 0; i < fractions.length; i++)
            denoms[i] = fractions[i].getDenominator();

        int[] roulette = getRoulette(denoms);

        int sum = 0;
        int random = r.nextInt(sum(roulette));

        for (int i = 0; i < roulette.length; i++){

            sum += roulette[i];
            if (random <= sum) {
                events[i+1] ++;
                return (i + 1);
            }
        }

        return roulette.length;
    }

    public static TerminalDevice.DeviceState getRandomState(int genProb, int denProb, int failProb, int busyProb){

        int[] probs = new int[]{genProb,denProb,failProb,busyProb};

        if (eventsAmount > 0 && eventsAmount % 20000 == 0) {
            steps ++;

            for (int i = 0; i < events.length; i++)
                mEvents[i] += events[i];

            System.out.println("Значения: Норма = "
                    + events[0]
                    + ", ген = "
                    + events[1]
                    + ", отк = "
                    + events[2]
                    + ", сбой = "
                    + events[3]
                    + ", занят = "
                    + events[4]);

            System.out.println("Средние значения: Норма = "
                    + mEvents[0]/steps
                    + ", ген = "
                    + mEvents[1]/steps
                    + ", отк = "
                    + mEvents[2]/steps
                    + ", сбой = "
                    + mEvents[3]/steps
                    + ", занят = "
                    + mEvents[4]/steps);



            for (int i = 0; i < events.length; i++)
                events[i] = 0;
        }

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
