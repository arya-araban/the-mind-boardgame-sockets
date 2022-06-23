package mindgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Bot extends Player {

    private float strategy;

    public float getStrategy() {
        return strategy;
    }

    public Bot(String playerName) {
        super(playerName);
        this.strategy = randomNumInRange((float) 0.1, 1); // player with start 0 plays the earliest, 1 plays the latest
    }

    public float waitTimeToPlay(int otherPlayerNumCards) {
        // the main formula used to specify wait time based on strategy and current game state
        return mapRanges(this.getSmallestCard(), 1, 100, 1, 10) * this.strategy * otherPlayerNumCards / 4;
    }


    private static float mapRanges(float input, float input_start, float input_end, float output_start, float output_end) {
        return output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start);
    }

    private static float randomNumInRange(float start, float end) {
        Random rand = new Random();

        return (end - start) * rand.nextFloat() + start;

    }

//    public static void main(String[] args) {
//        Bot bot = new Bot("arya");
//        Bot botr = new Bot("reza");
//        System.out.println(bot.strategy);
//        bot.setPlayerHand(Arrays.asList(29, 75));
//        System.out.println(bot.waitTimeToPlay(5));
//        System.out.println("-------------");
//        System.out.println(botr.strategy);
//        botr.setPlayerHand(Arrays.asList(29, 75));
//        System.out.println(botr.waitTimeToPlay(5));
//    }
}
