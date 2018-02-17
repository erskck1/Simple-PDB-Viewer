package visualization.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.function.BiFunction;

public class ProgressBarService extends Service<double[][]> {

    private int numberOfNodes;
    private int[][] edges;
    private double[][] initialCoordinates;
    private int iterations;

    public static double[][] computeSpringEmbedding(int iterations, int numberOfNodes, int[][] edges, double[][] initialCoordinates, BiFunction<Integer, Integer, Boolean> updateFunction) {
        if (numberOfNodes < 2) {
            return new double[][]{{0.0, 0.0}};
        }

        final int width = 100; // approximate window width
        final int height = 100; // approximate window height

        // Initial positions are on a circle:
        final double[][] coordinates = new double[numberOfNodes][2];
        if (initialCoordinates != null) {
            for (int i = 0; i < numberOfNodes; ++i) {
                coordinates[i] = new double[2];
                coordinates[i][0] = initialCoordinates[i][0];
                coordinates[i][1] = initialCoordinates[i][1];
            }
        } else {
            int count = 0;
            for (int v = 0; v < numberOfNodes; v++) {
                coordinates[v][0] = (float) (width * Math.cos(2.0 * Math.PI * (double) count / (double) numberOfNodes));
                coordinates[v][1] = (float) (height * Math.sin(2.0 * Math.PI * (double) count / (double) numberOfNodes));
                count++;
            }
        }

        // compute node degrees
        final int[] deg = new int[numberOfNodes];
        for (int[] e : edges) {
            deg[e[0]]++;
            deg[e[1]]++;
        }

        // run iterations of spring embedding:
        final double log2 = Math.log(2);

        for (int count = 1; count < iterations; count++) {
            final double k = Math.sqrt(width * (double) height / numberOfNodes) / 2;

            final double l2 = 25 * log2 * Math.log(1 + count);

            final double tx = width / l2;
            final double ty = height / l2;

            final double[][] array = new double[numberOfNodes][2];

            // repulsions

            for (int v = 0; v < numberOfNodes; v++) {
                double xv = coordinates[v][0];
                double yv = coordinates[v][1];

                for (int u = 0; u < numberOfNodes; u++) {
                    if (u == v) {
                        continue;
                    }
                    double xDistance = xv - coordinates[u][0];
                    double yDistance = yv - coordinates[u][1];
                    double dist = xDistance * xDistance + yDistance * yDistance;
                    dist = Math.max(dist, 1e-3);
                    double repulse = k * k / dist;
                    array[v][0] += repulse * xDistance;
                    array[v][1] += repulse * yDistance;
                }

                for (int[] edge : edges) {
                    int a = edge[0];
                    int b = edge[1];
                    if (a == v || b == v) {
                        continue;
                    }
                    double xDistance = xv - (coordinates[a][0] + coordinates[b][0]) / 2;
                    double yDistance = yv - (coordinates[a][1] + coordinates[b][1]) / 2;
                    double dist = xDistance * xDistance + yDistance * yDistance;
                    dist = Math.max(dist, 1e-3);
                    double repulse = k * k / dist;
                    array[v][0] += repulse * xDistance;
                    array[v][1] += repulse * yDistance;
                }
            }

            // attractions

            for (int[] edge : edges) {
                final int u = edge[0];
                final int v = edge[1];

                double xdist = coordinates[v][0] - coordinates[u][0];
                double ydist = coordinates[v][1] - coordinates[u][1];

                double dist = Math.sqrt(xdist * xdist + ydist * ydist);

                dist /= ((deg[u] + deg[v]) / 16.0);

                array[v][0] -= xdist * dist / k;
                array[v][1] -= ydist * dist / k;

                array[u][0] += xdist * dist / k;
                array[u][1] += ydist * dist / k;
            }

            // exclusions

            for (int v = 0; v < numberOfNodes; v++) {
                double xd = array[v][0];
                double yd = array[v][1];

                final double dist = Math.sqrt(xd * xd + yd * yd);

                xd = tx * xd / dist;
                yd = ty * yd / dist;

                coordinates[v][0] += xd;
                coordinates[v][1] += yd;
            }
            if (!updateFunction.apply(count, iterations)) {
                return null;
            }
        }
        return coordinates;
    }

    public void setEdges(int[][] edges) {
        this.edges = edges;
    }

    public void setNumberOfNodes(int numberOfNodes) {
        this.numberOfNodes = numberOfNodes;
    }

    public void setInitialCoordinates(double[][] initialCoordinates) {
        this.initialCoordinates = initialCoordinates;
    }

    public void setIterations(@SuppressWarnings("SameParameterValue") int iterations) {
        this.iterations = iterations;
    }

    @Override
    protected Task<double[][]> createTask() {
        //noinspection ReturnOfInnerClass
        return new Task<double[][]>() {
            @Override
            protected double[][] call() throws Exception {
                return computeSpringEmbedding(iterations, numberOfNodes, edges, initialCoordinates, (count, it) -> {
                    if (isCancelled()) {
                        return false;
                    } else {
                        updateMessage("Iteration " + count);
                        updateProgress(count + 1, iterations);
                        try {
                            Thread.sleep(4);
                        } catch (Exception e) {

                        }
                        return true;
                    }
                });
            }
        };
    }
}
