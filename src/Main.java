import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;

/**
 * The Main class serves as the entry point for a transport simulation program.
 * It provides methods for handling file input, processing transport orders, and managing a fleet of vehicles.
 *
 * The program reads data from a specified file, including details of storages, buyers, orders, and available transports.
 * It simulates the delivery process by assigning transports to orders, considering factors like transport capacity,
 * distance to the delivery point, and order deadlines. The simulation outputs the progress and results of deliveries,
 * including the number of completed orders and the amount of coal delivered.
 *
 * The Main class uses a series of static methods to perform its operations, which include:
 * - Parsing command-line arguments to configure file paths.
 * - Cleaning input files from unnecessary characters.
 * - Finding the slowest transport, the closest storage to an order, and the most relevant transport for an order.
 * - Adding and managing active transports.
 * - Processing and fulfilling orders through a simulated environment.
 *
 * Command-line arguments allow the user to specify the input file and whether to use the current directory for file paths.
 */

public class Main {
    static ArrayList<Storage> storages = new ArrayList<>();
    static ArrayList<Buyer> buyers = new ArrayList<>();
    static ArrayList<Transport> transports = new ArrayList<>();
    static ArrayList<Transport> activeTransports = new ArrayList<>();
    static Graph graph = new Graph();
    static double currentTime = 0;
    static int deliveredCoal = 0;
    static int completedOrders = 0;
    static double timeForTheSlowestCar = 0;
    static PriorityQueue<Order> queue = new PriorityQueue<>(Comparator.comparing(Order::getTimeCreation));

    static ArrayList<Double> chances = new ArrayList<>();

    /**
     * The main entry point of the program.
     * <p>
     * Parses command-line arguments to configure the file reading process and starts the order processing simulation.
     * It supports a '-f' option to specify the input file and an optional '-cR' flag to use the current directory
     * for the file path. After setting up the environment, it reads and processes all orders from the file,
     * simulating the delivery process and outputting the results to the console.
     *
     * @param args Command-line arguments for configuring the simulation. Expected arguments include:
     *             - "-cR" to indicate that the current directory should be used for file paths.
     *             - "-f" followed by the filename to specify the input file for the simulation.
     */

    public static void main(String[] args) {

        String filename = null;
        boolean useCurrentDirectory = false;

        for (int i = 0; i < args.length; i++) {
            if ("-cR".equals(args[i])) {
                useCurrentDirectory = true;
            } else if ("-f".equals(args[i]) && i + 1 < args.length) {
                filename = args[i + 1];
                i++;
            }
        }

        if (filename == null) {
            System.err.println("Error: No file specified. Use -f option to specify the file.");
            return;
        }

        if (useCurrentDirectory) {
            filename = System.getProperty("user.dir") + File.separator + filename;
        }

        try {
            FileReader file = new FileReader(filename);
            readFile(file);

            System.out.println("Amount of orders to be delivered: " + queue.size());

            while (!queue.isEmpty()) {
                proceedAnOrder();
                currentTime += 0.1;
            }
            System.out.println("Amount of completed orders: " + completedOrders);
            System.out.println("Amount of delivered coal: " + deliveredCoal);

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found - " + filename);
        } catch (IOException e) {
            System.err.println("Error: An I/O error occurred - " + e.getMessage());
        }


    }

    /**
     * Fills the active transport list with a specified amount of transport objects.
     * Each transport will be tasked to deliver a calculated number of orders.
     *
     * @param amount The number of transport objects to add to the active list.
     */
    public static void fillTheActiveTransportList(int amount) {
        int howManyOrdersWillEachCarDeliver = queue.size() / amount;
        for (int i = 0; i < amount; i++) {

             PriorityQueue<Order> queueCopy = new PriorityQueue<>(queue);
            Order order = queueCopy.poll();
            for (int j = 1; j < howManyOrdersWillEachCarDeliver; j++) {
                queueCopy.poll();
            }
            Storage closestStorageToTheOrder = findClosestStorageToTheOrder(order);
            Transport transport = getRandomTransport();
            activeTransports.add(transport);
            transport.pos = storages.indexOf(closestStorageToTheOrder) + 1;
        }
    }
    /**
     * Finds the most relevant transport based on its proximity to a given storage and its availability within the specified time.
     *
     * @param storage The storage to find the closest transport to.
     * @param time    The simulation time by which the transport must be available.
     * @return The most relevant Transport object, or null if none are suitable.
     */
    public static Transport findTheMostRelevantCar(Storage storage, double time) {
        ArrayList<Transport> activeTransportsCopy = new ArrayList<>(activeTransports);
        int index = -1;
        double range = Double.MAX_VALUE;
        for (int i = 0; i < activeTransportsCopy.size(); i++) {
            if (graph.getDistance(i + 1, storages.indexOf(storage) + 1) < range) {
                Transport transport = activeTransportsCopy.get(i);
                if (transport.time > time) {
                    continue;
                } else {
                    index = i;
                    range = graph.getDistance(i + 1, storages.indexOf(storage) + 1);
                }
            }
        }
        if (index == -1) {
            return null;
        }
        return activeTransportsCopy.get(index);
    }
    /**
     * Processes an order from the queue. This method simulates the order fulfillment process,
     * assigning transports, loading coal, delivering orders, and managing transport time and coal capacity.
     */

    public static void proceedAnOrder() {
        double simulationTime = currentTime;
        assert queue.peek() != null;
        if (simulationTime >= queue.peek().timeCreation) {
            Transport transport;
            Order order = queue.peek();
            Buyer buyer = buyers.get(order.getIndexCustomer() - 1);
            Storage closestStorageToTheOrder = findClosestStorageToTheOrder(order);
            int posOfTheTransport;
            if (activeTransports.isEmpty()) {
                transport = getRandomTransport();
                activeTransports.add(transport);
                transport.pos = storages.indexOf(closestStorageToTheOrder) + 1;
                posOfTheTransport = storages.indexOf(closestStorageToTheOrder) + 1;
            } else {
                transport = findTheMostRelevantCar(closestStorageToTheOrder, simulationTime);
                if (transport == null) {
                    return;
                }
                posOfTheTransport = transport.pos;
                simulationTime = transport.time;
            }
            Storage storage = (Storage) findPlaceFromIndex(posOfTheTransport);
            System.out.println("Cas: " + (int) simulationTime + ", Pozadavek: " + (completedOrders + 1) + ", Zakaznik: " + order.getIndexCustomer() + ", Pocet pytlu: " + order.getCoal() + ", Deadline: " + (int) order.timeDeadline);

            int coal;
            if (storage.timeWhenWillBeFull >= simulationTime) {
                storage.currentCoal = storage.coal;
            }
            if (transport.isFull()) {
                coal = 0;
            } else {
                if (transport.coalToFull() < storage.currentCoal) {
                    coal = transport.coalToFull();
                    storage.currentCoal -= transport.coalToFull();
                    transport.currentCoal = transport.maxCapacity;

                } else {
                    coal = storage.currentCoal;
                    transport.currentCoal += storage.currentCoal;
                    storage.currentCoal = 0;
                }
                storage.timeWhenWillBeFull = simulationTime + storage.timeToGenerate;
            }
            System.out.println("Cas: " + (int) simulationTime + ", Kolecko: " + (activeTransports.indexOf(transport) + 1)
                    + ", Sklad: " + (posOfTheTransport) + ", Nalozeno pytlu: " + coal
                    + ", Odjezd v: " + (int) (simulationTime + storage.getTimeToLoad() * coal));


            ArrayList<Integer> path = graph.shortestPath(posOfTheTransport, order.getIndexCustomer() + 1);
            double transportTravelledDistance = 0;

            if (path.size() == 2) {
                double distance = Point2D.Double.distance(storage.getPoint().getX(), storage.getPoint().getY(),
                        buyer.getPoint().getX(), buyer.getPoint().getY());
                simulationTime += distance / transport.getSpeed();
                if ((order.getTimeDeadline() - simulationTime + order.getCoal() * storage.getTimeToLoad()) < 0) {
                    System.out.println("Cas: " +(int) simulationTime + ", Zakaznik " + order.getIndexCustomer() + " umrzl zimou, protoze jezdit s koleckem je hloupost, konec");
                    queue.clear();
                    return;
                }
                soutDeliveredOrder(simulationTime, transport, order, storage);
                transport.currentCoal -= order.getCoal();
                transportTravelledDistance += distance;
                System.out.println("Cas: " + (int) simulationTime + ", Kolecko: " + (activeTransports.indexOf(transport) + 1)
                        + ", Navrat do skladu: " + (posOfTheTransport));
                simulationTime += distance / transport.getSpeed();
            }
            else {
                for (int i = 1; i < path.size(); i++) {
                    double distance = Point2D.Double.distance(findPlaceFromIndex(path.get(i-1)).getPoint().getX(), findPlaceFromIndex(path.get(i-1)).getPoint().getY(),
                            findPlaceFromIndex(path.get(i)).getPoint().getX(), findPlaceFromIndex(path.get(i)).getPoint().getY());
                    transportTravelledDistance += distance;
                    simulationTime += distance / transport.getSpeed();
                    if (findPlaceFromIndex(path.get(i)).getClass().getSimpleName().equals("Storage")) {
                        if (transportTravelledDistance > transport.getDistance()) {
                            simulationTime += transport.getTimeToRepair();

                            System.out.println("Cas: " + (int) simulationTime + ", Kolecko: " + (activeTransports.indexOf(transport) + 1)
                                    + ", Zakaznik: " + order.getIndexCustomer() + ", " + transport.getName() + " kolecko vyzaduje udrzbu, \nPokracovani mozne v: " + (int) simulationTime);
                        }
                    } else {
                        if (findOrderByBuyer((Buyer) findPlaceFromIndex(path.get(i))) != null && findOrderByBuyer((Buyer) findPlaceFromIndex(path.get(i))).getTimeCreation() > simulationTime) {
                            if ((order.getTimeDeadline() - simulationTime + order.getCoal() * storage.getTimeToLoad()) < 0) {
                                System.out.println("Cas: " +(int) simulationTime + ", Zakaznik " + order.getIndexCustomer() + " umrzl zimou, protoze jezdit s koleckem je hloupost, konec");
                                queue.clear();
                                return;
                            }
                            soutDeliveredOrder(simulationTime, transport, findOrderByBuyer((Buyer) findPlaceFromIndex(path.get(i))), storage);
                            simulationTime += findOrderByBuyer((Buyer) findPlaceFromIndex(path.get(i))).getCoal() * storage.getTimeToLoad();
                            deliveredCoal += findOrderByBuyer((Buyer) findPlaceFromIndex(path.get(i))).getCoal();
                            transport.currentCoal -= findOrderByBuyer((Buyer) findPlaceFromIndex(path.get(i))).getCoal();
                            queue.remove(findOrderByBuyer((Buyer) findPlaceFromIndex(path.get(i))));


                        } else {
                            System.out.println("Cas: " + (int) simulationTime + ", Kolecko: " + (activeTransports.indexOf(transport) + 1)
                                            + ", Zakaznik: " + order.getIndexCustomer() + ", Kuuk na " + transport.getName() + " kolecko");
                        }
                    }
                }
                if ((order.getTimeDeadline() - simulationTime + order.getCoal() * storage.getTimeToLoad()) < 0) {
                    System.out.println("Cas: " +(int) simulationTime + ", Zakaznik " + order.getIndexCustomer() + " umrzl zimou, protoze jezdit s koleckem je hloupost, konec");
                    queue.clear();
                    return;
                }
                soutDeliveredOrder(simulationTime, transport, order, storage);

                System.out.println("Cas: " + (int) simulationTime + ", Kolecko: " + (activeTransports.indexOf(transport) + 1)
                        + ", Navrat do skladu: " + (posOfTheTransport));
                //дополнить возврат
            }

            completedOrders++;
            deliveredCoal += order.getCoal();
            simulationTime += order.getCoal() * storage.getTimeToLoad();
            if (timeForTheSlowestCar == 0 && Objects.equals(transport.getName(), findTheSlowestTransport().getName())) {
                timeForTheSlowestCar = simulationTime;
                double howManyDeliveriesCouldMake = (order.getTimeDeadline() / timeForTheSlowestCar);
                double howManyCarsAreNeeded = queue.size() / howManyDeliveriesCouldMake;
                fillTheActiveTransportList((int) howManyCarsAreNeeded + 1 );
            }

            transport.time = simulationTime;
            queue.poll();
        }
    }
    /**
     * Finds and returns the slowest transport in the list of available transports.
     *
     * @return The Transport object with the slowest speed. If no transports are found, the behavior is undefined.
     */
    public static Transport findTheSlowestTransport() {
        double pom = Double.MAX_VALUE;
        int j = -1;
        for (int i = 0; i < transports.size(); i++) {
            if (transports.get(i).getSpeed() < pom) {
                pom = transports.get(i).getSpeed();
                j = i;
            }
        }
        return transports.get(j);
    }
    /**
     * Finds the storage closest to the specified order based on distance.
     *
     * @param order The order to which the closest storage is to be found.
     * @return The closest Storage object to the specified order.
     */


    public static Storage findClosestStorageToTheOrder(Order order) {
        double range = Double.MAX_VALUE;
        int index = -1;
        for (int k = 0; k < storages.size(); k++) {
            if (graph.getDistance(k + 1, order.getIndexCustomer()) < range) {
                index = k;
                range = graph.getDistance(k + 1, order.getIndexCustomer());
            }
        }
        Storage storage = storages.get(index);
        return storage;
    }
    /**
     * Finds the order in the queue that corresponds to a specific buyer.
     *
     * @param buyer The buyer whose order needs to be found.
     * @return The Order object corresponding to the specified buyer, or null if the buyer has no orders.
     */

    public static Order findOrderByBuyer(Buyer buyer) {
        Order order = null;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.peek().getIndexCustomer() == buyers.indexOf(buyer)) {
                order = queue.peek();
                return order;
            }
        }
        return order;
    }
    /**
     * Determines the place (either a storage or a buyer) based on the given index.
     *
     * @param index The index that identifies the place. If the index is greater than the number of storages,
     *              it corresponds to a buyer; otherwise, it corresponds to a storage.
     * @return The APlace object identified by the index.
     */
    public static APlace findPlaceFromIndex(int index) {
        APlace sourcePlace;
        if (index > storages.size()) {
            sourcePlace = buyers.get(index - storages.size() - 1);
        } else {
            sourcePlace = storages.get(index - 1);
        }
        return sourcePlace;
    }
    /**
     * Selects and returns a random transport based on their representation probabilities.
     *
     * @return A new Transport object selected randomly based on the representation weight.
     * @throws RuntimeException If an error occurs during the selection process.
     */
    public static Transport getRandomTransport() {
        transports.forEach(e -> chances.add(e.getRepresentation()));
        Random r = new Random();
        double number = r.nextDouble();
        double cumulativeChance = 0.0;
        for (int i = 0; i < chances.size(); i++) {
            cumulativeChance += chances.get(i);
            if (number <= cumulativeChance) {
                Transport transport = transports.get(i);
                Transport newTransport = new Transport(transport.name, transport.speed, transport.speed, transport.distance, transport.distance, transport.timeToRepair, transport.maxCapacity, transport.representation);
                return newTransport;
            }
        }
        throw new RuntimeException("Unexpected error occurred");
    }
    /**
     * Processes the contents of a file, removing sequences between pairs of '❄' (snowflake) and '⛏' (pickaxe) symbols,
     * replacing them with a single space.
     *
     * @param file The FileReader object representing the file to be processed.
     * @return A String with the cleaned file content.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    public static String cleanFile(FileReader file) throws IOException {
        BufferedReader br = new BufferedReader(file);
        StringBuilder s = new StringBuilder();
        int countSnowflakes = 0;
        String line;
        while ((line = br.readLine()) != null) {
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '❄') {
                    countSnowflakes++;
                    while (countSnowflakes > 0) {
                        i++;
                        c = line.charAt(i);
                        if (c == '⛏') {
                            s.append(" ");
                            countSnowflakes--;
                        } else if (c == '❄') {
                            countSnowflakes++;
                        }
                        if (line.length() == i + 1 && countSnowflakes > 0) {
                            i = 0;
                            line = br.readLine();
                        }
                    }
                } else {
                    s.append(c);
                }
            }
            s.append('\n');
        }
        br.close();
        return s.toString();
    }
    /**
     * Reads and parses the content of a file to initialize storages, buyers, edges in a graph, transports, and orders.
     * It uses the cleanFile method to preprocess the file content before parsing.
     *
     * @param file The FileReader object representing the file to be read and parsed.
     * @throws IOException If an I/O error occurs while reading the file.
     */

    public static void readFile(FileReader file) throws IOException {
        // очистка файла от звездочек и кирок
        String input = cleanFile(file);
        Scanner sc = new Scanner(input);
        sc.useLocale(Locale.US);
        // склады
        int i = sc.nextInt();
        for (int j = 0; j < i; j++) {
            Storage storage = new Storage(sc.nextDouble(), sc.nextDouble(), sc.nextInt(),
                    sc.nextDouble(), sc.nextDouble());
            storages.add(storage);
        }
        // заказчики
        i = sc.nextInt();
        for (int j = 0; j < i; j++) {
            buyers.add(new Buyer(sc.nextDouble(), sc.nextDouble()));
        }

        // грани
        i = sc.nextInt();
        for (int j = 0; j < i; j++) {
            int x = sc.nextInt();
            int y = sc.nextInt();
            if (x == y) {
                continue;
            }
            APlace place1;
            APlace place2;
            if (x > storages.size()) {
                place1 = buyers.get(x - storages.size() - 1);
            } else {
                place1 = storages.get(x - 1);
            }

            if (y > storages.size()) {
                place2 = buyers.get(y - storages.size() - 1);
            } else {
                place2 = storages.get(y - 1);
            }
            double range = Point2D.Double.distance(place1.getPoint().getX(), place1.getPoint().getY(),
                    place2.getPoint().getX(), place2.getPoint().getY());
            graph.addEdge(x, y, range);
        }
        Graph.size = graph.adjacencyList.size();

        // тачки
        i = sc.nextInt();
        for (int j = 0; j < i; j++) {
            transports.add(new Transport(sc.next(), sc.nextDouble(), sc.nextDouble(),
                    sc.nextDouble(), sc.nextDouble(),
                    sc.nextDouble(), sc.nextInt(), sc.nextDouble()));
        }
        // заказы
        i = sc.nextInt();
        for (int j = 0; j < i; j++) {
            queue.add(new Order(sc.nextDouble(), sc.nextInt(),
                    sc.nextInt(),
                    sc.nextDouble()));
        }
        sc.close();
    }
    /**
     * Outputs the details of a delivered order to the console.
     * It prints the simulation time, transport identifier, customer index, amount of coal delivered,
     * and the time reserve based on the deadline after the coal is unloaded.
     *
     * @param simulationTime The current time in the simulation when the order is being processed.
     * @param transport      The Transport object that is delivering the order.
     * @param order          The Order object containing details about the coal and customer.
     * @param storage        The Storage object from where the coal is being delivered.
     */
    public static void soutDeliveredOrder(double simulationTime, Transport transport, Order order, Storage storage) {
        System.out.println("Cas: " + (int) simulationTime + ", Kolecko: " + (activeTransports.indexOf(transport) + 1)
                + ", Zakaznik: " + order.getIndexCustomer()
                + ", Vylozeno pytlu: " + order.getCoal()
                + ", Vylozeno v: " + (int) (simulationTime + order.getCoal() * storage.getTimeToLoad())
                + ", Casova reserva: " + (int) (order.getTimeDeadline() - simulationTime + order.getCoal() * storage.getTimeToLoad()));
    }

}