package com.flow.shop.pszt;

import com.google.common.collect.ObjectArrays;

import java.util.*;

/**
 * Created by krris on 02.11.14.
 * Copyright (c) 2014 krris. All rights reserved.
 */
public class Population {

    private double mutationRate;          // Mutation rate
    private double alpha;

    private DNA[] population;             // Array to hold the current population
    private DNA[] tempPopulationT;
    private DNA[] tempPopulationR;

    private int generations;              // Number of generations

    private double bestFitnessEver;
	private DNA bestMemberEver;
    private int bestGenerationNo;
    
    private Random random = new Random();

    public Population( double mutationRate, double alpha, int populationMax, ArrayList<Task> tasks) {
        this.mutationRate = mutationRate;
        this.alpha = alpha;

        this.population = new DNA[populationMax];
        for (int i = 0; i < population.length; i++) {
            population[i] = new DNA(tasks);
        }

        this.tempPopulationT = new DNA[(int)(populationMax * this.alpha)];
        this.tempPopulationR = new DNA[(int)(populationMax * this.alpha)];

        this.generations = 0;

        bestFitnessEver = Double.MAX_VALUE;
        bestMemberEver = null;
    }

    public void createTemporaryPopulation() {
        for (int i = 0; i < tempPopulationT.length; i++) {
            int randomDna = random.nextInt(population.length);
            tempPopulationT[i] = population[randomDna];
        }
    }

    public void crossoverAndMutation() {
        assert (tempPopulationR.length == tempPopulationT.length);

        tempPopulationR = Arrays.copyOf(tempPopulationT, tempPopulationR.length);

        for (int i = 0; i < tempPopulationR.length; i++) {
            // crossover
            int a = random.nextInt(tempPopulationR.length);
            int b = random.nextInt(tempPopulationR.length);
            DNA partnerA = tempPopulationR[a];
            DNA partnerB = tempPopulationR[b];
            DNA child = partnerA.crossover(partnerB);
            tempPopulationR[i] = child;

            // mutation
            tempPopulationR[i].mutate(mutationRate);
        }
    }

    // Create a new generation
    public void oneGeneration() {
        this.createTemporaryPopulation();
        this.crossoverAndMutation();
        this.naturalSelection();
        generations++;
    }


    public void naturalSelection() {
        DNA[] both = ObjectArrays.concat(population, tempPopulationR, DNA.class);
        Arrays.sort(both, Comparator.comparing(DNA::getFitness));
        population = Arrays.copyOf(both, population.length);
    }


    // Compute the current "most fit" member of the population
    public DNA getBestMemberDNA() {
        double worldRecord = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < population.length; i++) {
            if (population[i].getFitness() < worldRecord) {
                index = i;
                worldRecord = population[i].getFitness();
            }
        }

        return population[index];
    }
    
    /**
     * evaluate the current Population and check if there is a member better
     * than the one which was best until now
     * @return
     */
    public void evaluate(int generationNo) {
    	for (int i = 0; i < population.length; i++) {
            if (population[i].getFitness() < bestFitnessEver) {
                bestMemberEver = population[i];
                bestFitnessEver = population[i].getFitness();
                bestGenerationNo = generationNo;
            }
        }
    }

    public int getGenerations() {
        return generations;
    }

    // Compute average fitness for the population
    public float getAverageFitness() {
        float total = 0;
        for (int i = 0; i < population.length; i++) {
            total += population[i].getFitness();
        }
        return total / (population.length);
    }
    
    public double getBestFitnessEver() {
		return bestFitnessEver;
	}

	public String getBestMemberEver() {
		return bestMemberEver.getOrder();
	}

    public int getBestGenerationNo() {
        return bestGenerationNo;
    }
}
