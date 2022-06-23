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
        this.strategy = randomNumInRange((float) 0.1, 1); // player with strategy 0.1 plays the earliest, 1 plays the latest
    }

    public float waitTimeToPlay(int otherPlayersNumCards) {
        // the main formula used to specify wait time based on strategy and current game state
        return mapRanges(this.getSmallestCard(), 1, 100, 1, 15) * this.strategy * otherPlayersNumCards / 4;
    }


    private static float mapRanges(float input, float input_start, float input_end, float output_start, float output_end) {
        return output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start);
    }

    private static float randomNumInRange(float start, float end) {
        Random rand = new Random();

        return (end - start) * rand.nextFloat() + start;

    }

}
