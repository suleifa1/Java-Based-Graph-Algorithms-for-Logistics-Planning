import java.util.*;


/**
 * Represents a weighted graph with functionality to add edges, compute shortest paths,
 * and retrieve distances between vertices. The graph is represented internally as an
 * adjacency list and a separate map for weighted edges. It includes methods for edge
 * manipulation, pathfinding via Dijkstra's algorithm, and memory management for large
 * graphs by removing half the entries from inner maps when necessary.
 *
 * The Edge inner class is used to represent connections between vertices along with their
 * respective weights and provides a natural ordering of edges based on these weights.
 *
 * The Graph class also supports precomputed distances between vertices for efficient
 * distance retrieval. It offers methods to add and retrieve individual edge weights, as
 * well as to compute shortest paths and overall distances between given vertices.
 *
 * Usage of this class involves creating a Graph instance, adding edges with associated
 * weights, and then querying for shortest paths or distances as needed. The class provides
 * a toString method for easy visualization of the graph's structure in terms of vertex
 * connections and edge counts.
 */
public class Graph {


    /**
     * Edge class to hold the vertex identifier and the weight of the edge.
     * It implements Comparable to allow sorting based on the edge's weight.
     */
    static class Edge implements Comparable<Edge> {
        int vertex;
        double weight;

        /**
         * Constructs an Edge with a target vertex and the weight of the edge.
         *
         * @param v The target vertex this edge leads to.
         * @param w The weight or cost of traversing this edge.
         */
        Edge(int v, double w) {
            vertex = v;
            weight = w;
        }

        /**
         * Compares this edge with another edge based on weight.
         *
         * @param other The edge to compare with this one.
         * @return A negative integer, zero, or a positive integer as this edge
         *         is less than, equal to, or greater than the specified edge.
         */
        public int compareTo(Edge other) {
            return Double.compare(weight, other.weight);
        }
    }
    HashMap<Integer, ArrayList<Edge>> adjacencyList = new HashMap<>();
    public static int size;
    public static boolean flagBigGraph;
    public HashMap<Integer, HashMap<Integer, Double>> weightedMap = new HashMap<>();


    /**
     * Removes half of the entries from each inner map in the weightedMap.
     * It's used to manage memory consumption for large graphs.
     */
    public void removeHalfFromEachInnerMap() {
        for (Map.Entry<Integer, HashMap<Integer, Double>> entry : weightedMap.entrySet()) {
            HashMap<Integer, Double> innerMap = entry.getValue();
            List<Integer> keysToRemove = new ArrayList<>();

            int count = 0;
            int halfSize = innerMap.size() / 2;
            for (Integer key : innerMap.keySet()) {
                if (count < halfSize) {
                    keysToRemove.add(key);
                } else {
                    break; // Достаточно собрать половину ключей
                }
                count++;
            }

            for (Integer key : keysToRemove) {
                innerMap.remove(key);
            }
        }
    }

    /**
     * Adds or updates the weight for an edge between two vertices.
     *
     * @param from   The starting vertex identifier.
     * @param to     The ending vertex identifier.
     * @param weight The weight of the edge.
     */
    public void addWeight(int from, int to, double weight) {
        weightedMap.computeIfAbsent(from, k -> new HashMap<>()).put(to, weight);
    }

    /**
     * Retrieves a precomputed weight value between two vertices.
     *
     * @param src  The source vertex.
     * @param dest The destination vertex.
     * @return The weight of the edge if it exists, otherwise -1.0.
     */
    public double getPrecomputedValue(int src, int dest){
        src--;
        dest--;
        HashMap<Integer, Double> innerMap = weightedMap.get(src);
        if (innerMap != null) {
            return innerMap.getOrDefault(dest, -1.0);
        }
        return -1;
    }

    /**
     * Adds an edge to the graph between two vertices.
     *
     * @param i      The starting vertex identifier.
     * @param j      The ending vertex identifier.
     * @param weight The weight of the edge.
     */
    void addEdge(int i, int j, double weight) {
        i--;
        j--;

        adjacencyList.computeIfAbsent(i, k -> new ArrayList<>()).add(new Edge(j, weight));
        adjacencyList.computeIfAbsent(j, k -> new ArrayList<>()).add(new Edge(i, weight));

        addWeight(i, j, weight);
    }

    /**
     * Computes the shortest path between two vertices using Dijkstra's algorithm.
     *
     * @param src  The source vertex identifier.
     * @param dest The destination vertex identifier.
     * @return An ArrayList of integers representing the path from source to destination.
     */
    public ArrayList<Integer> shortestPath(int src, int dest) {
        src--;
        dest--;

        double[] dist = new double[size];
        int[] prev = new int[size];
        boolean[] visited = new boolean[size];
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        Arrays.fill(dist, Double.MAX_VALUE);
        dist[src] = 0;
        pq.add(new Edge(src, 0));
        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int u = current.vertex;
            if (visited[u]) continue;
            visited[u] = true;
            for (Edge edge : adjacencyList.get(u)) {
                int v = edge.vertex;
                if (!visited[v] && dist[u] + edge.weight < dist[v]) {
                    dist[v] = dist[u] + edge.weight;
                    prev[v] = u;
                    pq.add(new Edge(v, dist[v]));
                    addWeight(src,v,dist[v]);
                }
            }

        }
        ArrayList<Integer> path = new ArrayList<>();
        for (int at = dest; at != src; at = prev[at]) {
            path.add(at + 1);
        }
        path.add(src + 1);
        Collections.reverse(path);

        return path;
    }

    /**
     * Calculates the distance between two vertices in the graph.
     * It may use precomputed values or compute the path if necessary.
     *
     * @param src  The source vertex.
     * @param dest The destination vertex.
     * @return The distance between the source and destination vertices.
     */
    public double getDistance(int src, int dest) {
        double range = getPrecomputedValue(src, dest);
        range = range == -1 ? getPrecomputedValue(dest, src) : range;
        if (range == -1) {
            ArrayList<Integer> path = shortestPath(src, dest);
            if(flagBigGraph){
                removeHalfFromEachInnerMap();
            }
            double value = 0;
            for (int i = 0; i < path.size() - 1; i++) {
                int u = path.get(i) - 1 ;
                int v = path.get(i + 1) - 1 ;
                for (Edge edge : adjacencyList.get(u)) {
                    if (edge.vertex == v) {
                        value += edge.weight;
                        break;
                    }
                }
            }
            --src;
            --dest;
            addWeight(src,dest,value);
            return value;
        }
        else {
            return range;
        }
    }

    /**
     * Provides a string representation of the graph.
     *
     * @return A string that lists all vertices and their edge counts.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(i + 1).append(" ").append(adjacencyList.get(i).size()).append("\n");
        }
        return sb.toString();
    }
}
