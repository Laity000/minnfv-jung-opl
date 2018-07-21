package com.zj.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.zj.heuristic.Algorithm;
import com.zj.network.Demands;
import com.zj.network.Node;
import com.zj.network.Topology;
import com.zj.solution.Solution;

public class GAsolver {

	private Topology topo;

	private Demands dems;

	private int mutationFactor = 0;


	public GAsolver(Topology topo, Demands dems) {
		super();
		this.topo = topo;
		this.dems = dems;
	}


	public Solution run() {
		Population<MyVector> population = createInitialPopulation(2);

		Fitness<MyVector, Solution> fitness = new MyVectorFitness();

		GeneticAlgorithm<MyVector, Solution> ga = new GeneticAlgorithm<MyVector, Solution>(population, fitness);

		addListener(ga);

		ga.evolve(100);

		MyVector best = ga.getBest();
		Solution bestPMsolu = ga.fitness(best);
		return bestPMsolu;
	}

	/**
	 * 初始化基因染色体，
	 */
	private Population<MyVector> createInitialPopulation(int populationSize) {
		Population<MyVector> population = new Population<MyVector>();
		MyVector base = new MyVector();
		for (int i = 0; i < populationSize; i++) {
			// each member of initial population
			// is mutated clone of base chromosome
			MyVector chr = base.mutate();
			population.addChromosome(chr);
		}
		return population;
	}

	/**
	 * After each iteration Genetic algorithm notifies listener
	 */
	private void addListener(GeneticAlgorithm<MyVector, Solution> ga) {
		// just for pretty print
		System.out.println(String.format("%s\t%s\t%s", "iter", "fit", "chromosome"));

		// Lets add listener, which prints best chromosome after each iteration
		ga.addIterationListener(new IterartionListener<MyVector, Solution>() {

			private final double threshold = 1e-5;

			@Override
			public void update(GeneticAlgorithm<MyVector, Solution> ga) {

				MyVector best = ga.getBest();
				Solution bestSolu = ga.fitness(best);
				double bestFit = bestSolu.getFitValue();
				int iteration = ga.getIteration();

				// Listener prints best achieved solution
				//System.out.println(String.format("%s\t%s\t%s", iteration, bestFit, best));
				System.out.println(String.format("%s\t%s;", iteration+1, bestFit));

				// If fitness is satisfying - we can stop Genetic algorithm
				if (bestFit < this.threshold) {
					ga.terminate();
				}
			}
		});
	}

	/**
	 * 染色体，代表节点是否被禁用
	 */
	public class MyVector implements Chromosome<MyVector>, Cloneable {

		private final Random random = new Random();

		int numNodes = topo.nodesCount();
		//true代表节点被禁用
		private boolean[] vector = new boolean[numNodes];

		/**
		 * 变异
		 */
		@Override
		public MyVector mutate() {
			MyVector result = this.clone();

			/**
			 * n点变异
			 */
			//初始化
			ArrayList<Integer> list = new ArrayList<>(this.vector.length);
			for (int i = 0; i < this.vector.length; i++) {
				list.add(i);
			}

			for (int i = 0; i < mutationFactor; i++) {

				int index = list.remove(random.nextInt(this.vector.length-i));
				if (!result.vector[index]) {
					result.vector[index] = true;
				}else {
					result.vector[index] = false;
				}
			}

			return result;

		}

		/**
		 * 交叉进化
		 */
		@Override
		public List<MyVector> crossover(MyVector other) {
			MyVector thisClone = this.clone();
			MyVector otherClone = other.clone();

			//单点交叉

			int index = random.nextInt(this.vector.length - 1);
			for (int i = index; i < this.vector.length; i++) {
				boolean tmp = thisClone.vector[i];
				thisClone.vector[i] = otherClone.vector[i];
				otherClone.vector[i] = tmp;
			}

			return Arrays.asList(thisClone, otherClone);
		}


		@Override
		protected MyVector clone() {
			MyVector clone = new MyVector();
			System.arraycopy(this.vector, 0, clone.vector, 0, this.vector.length);
			return clone;
		}

		public boolean[] getVector() {
			return this.vector;
		}

		@Override
		public String toString() {
			return Arrays.toString(this.vector);
		}
	}

	/**
	 * Fitness function, which calculates difference between chromosomes vector
	 * and target vector
	 */
	public class MyVectorFitness implements Fitness<MyVector, Solution> {


		@Override
		public Solution calculate(MyVector chromosome) {
			boolean[] v = chromosome.getVector();
			for (int i = 0; i < v.length; i++) {
				Node node = topo.getNode(i);
				if (v[i]) {
					node.setBanFlag(true);
				}else {
					node.setBanFlag(false);
				}
			}
			Solution solu = Algorithm.installNFC(topo, dems);
			return solu;
		}
	}
}
