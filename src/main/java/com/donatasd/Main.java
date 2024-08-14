package com.donatasd;

import com.donatasd.solutions.AdvancedSolution;
import com.donatasd.solutions.BasicSolution;
import com.donatasd.solutions.OptimalSolution;
import com.donatasd.solutions.Solution;
import com.donatasd.utils.ExecutionTimer;

public class Main {
    public static final Integer IMAGES_COUNT = 100;
    public static final Integer MEMORY_SIZE = 100;

    public static void main(String[] args) {
        Solution basicSolution = new BasicSolution(MEMORY_SIZE, IMAGES_COUNT);
        Long timeBasicSolution = ExecutionTimer.measure(basicSolution);
        Solution advancedSolution = new AdvancedSolution(MEMORY_SIZE, IMAGES_COUNT);
        Long timeAdvancedSolution = ExecutionTimer.measure(advancedSolution);
        Solution optimalSolution = new OptimalSolution(MEMORY_SIZE, IMAGES_COUNT);
        Long timeOptimalSolution = ExecutionTimer.measure(optimalSolution);
        System.out.println(STR."Basic solution time: \{timeBasicSolution}MS");
        System.out.println(STR."Advanced solution time: \{timeAdvancedSolution}MS");
        System.out.println(STR."Optimal solution time: \{timeOptimalSolution}MS");
    }
}