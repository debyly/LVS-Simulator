package model;

import java.util.ArrayList;

public class MyRandom {

    private static long gcd(long a, long b){
        return b == 0 ? a : gcd(b,a % b);
    }

    public static long lcm(long a, long b){
        return a*b / gcd(a,b);
    }

    public static ArrayList<Byte> getRoulette(
            int clients, int sessions, int multiplier, int gen, int den, int fail, int busy){

        ArrayList<Byte> output = new ArrayList<>();
        int z = clients * sessions * multiplier;

        return output;
    }



}
