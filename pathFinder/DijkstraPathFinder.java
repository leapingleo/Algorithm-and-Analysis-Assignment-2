package pathFinder;
import map.Coordinate;
import map.PathMap;
import java.util.*;

public class DijkstraPathFinder implements PathFinder
{
    // TODO: You might need to implement some attributes
    private PathMap map;
    //private ArrayList<Node> nodes = new ArrayList<Node>();
    private Node[][] nodes;
    private Set<Node> unvisited = new HashSet<>();
    private Set<Node> evaluatedNodes = new LinkedHashSet<>();
    private List<Node> previousNodes = new ArrayList<>();
    private PriorityQueue<Node> pq = new PriorityQueue<>(nodeComparator);
    List<Coordinate> path1 = new ArrayList<Coordinate>();
    private int counter = 0;

    public DijkstraPathFinder(PathMap map) {
        this.map = map;
        nodes = new Node[map.sizeR][map.sizeC];

        //transfer each passable coordinate to a node, and add them to the priority queue
        for (int i = 0; i < map.sizeR; i++) {
            for (int j = 0; j < map.sizeC; j++) {
                if (!map.cells[i][j].getImpassable()) {
                    nodes[i][j] = new Node(map.cells[i][j], map.cells[i][j].getTerrainCost(), Integer.MAX_VALUE);
                    pq.add(nodes[i][j]);
                }
            }
        }

        //set up a node's neighbours on its left, right, up and down
        for (int i = 0; i < map.sizeR; i++) {
            for (int j = 0; j < map.sizeC; j++) {
                if (j + 1 < map.sizeC  && !map.cells[i][j + 1].getImpassable() && nodes[i][j] != null)
                    nodes[i][j].getEdgeList().add(new Edge(nodes[i][j], nodes[i][j + 1], nodes[i][j + 1].getCost()));
                if (j - 1 >= 0 && !map.cells[i][j - 1].getImpassable() && nodes[i][j] != null)
                    nodes[i][j].getEdgeList().add(new Edge(nodes[i][j], nodes[i][j - 1], nodes[i][j - 1].getCost()));
                if (i + 1 < map.sizeR  && !map.cells[i + 1][j].getImpassable() && nodes[i][j] != null)
                    nodes[i][j].getEdgeList().add(new Edge(nodes[i][j], nodes[i + 1][j], nodes[i + 1][j].getCost()));
                if (i - 1 >= 0 && !map.cells[i - 1][j].getImpassable() && nodes[i][j] != null)
                    nodes[i][j].getEdgeList().add(new Edge(nodes[i][j], nodes[i - 1][j], nodes[i - 1][j].getCost()));
            }
        }
        // TODO :Implement
    } // end of DijkstraPathFinder()

    //return a shortest path between two nodes
    public List<Coordinate> shortestPath(Node source, Node dest){
        //each path finding should reset the priority queue
        //and set nodes to not visited, and set dist to integer_max
        resetPQ();
        List<Coordinate> path = new ArrayList<Coordinate>();
        //set first node to zero and mark as visited
        source.setDistToZero();
        source.setVisited(true);
        pq.remove(source);
        //since priority queue does not re-order after a node's distance is modified,
        reorderPQ(source);


        //1. poll the node with minumun dist-from-source value
        //2. mark it as visited
        //3. remove it from priority queue
        //4. update its neighbours dist-from-source value if necessary
        while (pq.size() > 0){

            Node currentNode = pq.poll();
            currentNode.setVisited(true);
            pq.remove(currentNode);
            updateNeighbourDistance(currentNode);
        }

        Node current = findNodeByCoor(dest.getCoor());
        //System.out.println(count);
       // int n = 0;
        while (current != null){
            path.add(current.getCoor());
            current = current.getPreviousNode();
        }
        //System.out.println(n);
        Collections.reverse(path);
        return path;
    }

    @Override
    public List<Coordinate> findPath() {
        List<Coordinate> path = new ArrayList<Coordinate>();
        List<List<Coordinate>> paths = new ArrayList<List<Coordinate>>();
        //to store multiple paths for PART-C, and PART C + D
        Map<List<Coordinate>, Integer> pathsMap = new HashMap<List<Coordinate>, Integer>();

        //Part A and B
        if (map.waypointCells.size() == 0) {

            for (int i = 0; i < map.originCells.size(); i++) {
                for (int j = 0; j < map.destCells.size(); j++) {
                    Node source = findNodeByCoor(map.originCells.get(i));
                    Node dest = findNodeByCoor(map.destCells.get(j));
                    List<Coordinate> p = shortestPath(source, dest);
                    paths.add(p);
                    pathsMap.put(p, dest.getDistFromSource());
                }
            }
            //get the minimum distance from the source
            Integer min = Collections.min(pathsMap.values());
            //System.out.println("Min dist " + min);

            List<Coordinate> minPath = new ArrayList<Coordinate>();
            for (Map.Entry<List<Coordinate>, Integer> entry: pathsMap.entrySet()){
                if (entry.getValue() == min)
                    path = entry.getKey();
            }
        }
        //PART C, D, and (C && D)
        else {
            for (int i = 0; i < map.originCells.size(); i++) {
                for (int j = 0; j < map.destCells.size(); j++) {
                    int eachTotalDist = 0;
                    List<Coordinate> eachWholePath = new ArrayList<Coordinate>();
                    Node source = findNodeByCoor(map.originCells.get(i));
                    Node firstW = findNodeByCoor(map.waypointCells.get(0));
                    List<Coordinate> intervalP1 = shortestPath(source, firstW);
                    eachWholePath.addAll(intervalP1);
                    eachTotalDist += firstW.getDistFromSource();

                    //compute shortest dist from each set of way points
                    for (int n = 0; n < map.waypointCells.size() - 1; n++) {
                        Node w1 = findNodeByCoor(map.waypointCells.get(n));
                        Node w2 = findNodeByCoor(map.waypointCells.get(n + 1));
                        List<Coordinate> p = shortestPath(w1, w2);
                        p.remove(0);
                        eachWholePath.addAll(p);
                        eachTotalDist += w2.getDistFromSource();
                    }

                    Node lastW = findNodeByCoor(map.waypointCells.get(map.waypointCells.size() - 1));
                    Node dest = findNodeByCoor(map.destCells.get(j));
                    List<Coordinate> lastWtoDest = shortestPath(lastW, dest);
                    eachTotalDist += dest.getDistFromSource();
                    lastWtoDest.remove(0);
                    eachWholePath.addAll(lastWtoDest);
                    pathsMap.put(eachWholePath, eachTotalDist);
                }
            }
            Integer min = Collections.min(pathsMap.values());
            for (Map.Entry<List<Coordinate>, Integer> entry: pathsMap.entrySet()){
                System.out.println("shortest dist is " + entry.getValue());
                if (entry.getValue() == min)
                    path = entry.getKey();
            }
            // List<Coordinate> combinedPath = new ArrayList<Coordinate>();
            // Node source = findNodeByCoor(map.originCells.get(0));
            // Node firstW = findNodeByCoor(map.waypointCells.get(0));
            // //compute source to
            // List<Coordinate> p1 = shortestPath(source, firstW);
            // combinedPath.addAll(p1);
           // //paths.add(shortestPath(source, dest));

            // //compute shortest distances between each i-th and (i + 1)th waypoints
            // for (int i = 0; i < map.waypointCells.size() - 1; i++) {
                // Node w1 = findNodeByCoor(map.waypointCells.get(i));
                // Node w2 = findNodeByCoor(map.waypointCells.get(i + 1));

                // List<Coordinate> p = shortestPath(w1, w2);
                // p.remove(0);
                // combinedPath.addAll(p);
            // }

            // Node lastW = findNodeByCoor(map.waypointCells.get(map.waypointCells.size() - 1));
            // Node dest = findNodeByCoor(map.destCells.get(0));
            // List<Coordinate> lastWtoDest = shortestPath(lastW, dest);
            // lastWtoDest.remove(0);
            // combinedPath.addAll(lastWtoDest);

            // path = combinedPath;
        }
        return path;
    } // end of findPath()

    //PART - C
    public void resetPQ(){
        pq.clear();
        for (int i = 0; i < map.sizeR; i++) {
            for (int j = 0; j < map.sizeC; j++) {
                if (!map.cells[i][j].getImpassable()) {
                    //nodes[i][j] = new Node(map.cells[i][j], map.cells[i][j].getTerrainCost(), Integer.MAX_VALUE);
                    nodes[i][j].setDistToMAX();
                    nodes[i][j].setVisited(false);
                    nodes[i][j].setPreviousNode(null);
                    pq.add(nodes[i][j]);
                }
            }
        }
    }

    public void reorderPQ(Node node){
        pq.remove(node);
        pq.add(node);
    }

    public void updateNeighbourDistance(Node atNode){
        ArrayList<Node> n = atNode.getNeighbours();

        for (Node neighbourNode: n) {
            if (!neighbourNode.getIsVisited()){

               if (neighbourNode.getDistFromSource() + neighbourNode.getCost() <= neighbourNode.getDistFromSource()) {
                    neighbourNode.updateDistFromSource(atNode.getDistFromSource());
                    neighbourNode.setPreviousNode(atNode);
                    reorderPQ(neighbourNode);
                }
            }
        }
    }

    public Node findNodeByCoor(Coordinate coordinate){
        for (int i = 0; i < nodes.length; i++){
            for (int j = 0; j < nodes[i].length; j++) {
                if (nodes[i][j] != null && nodes[i][j].getCoor().getRow() == coordinate.getRow() &&
                        nodes[i][j].getCoor().getColumn() == coordinate.getColumn()) {
                    return nodes[i][j];
                }
            }
        }
        return null;
    }

    @Override
    public int coordinatesExplored() {
        // TODO: Implement (optional)

        // placeholder
        return counter;
    } // end of cellsExplored()

    public static Comparator<Node> nodeComparator = new Comparator<Node>(){
        @Override
        public int compare(Node n1, Node n2) {
            if (n1.getDistFromSource() < n2.getDistFromSource())
                return -1;
            else if (n1.getDistFromSource() > n2.getDistFromSource())
                return 1;
            return 0;
          //  return (int) (n2.getDistFromSource() - n1.getDistFromSource());
        }
    };


    class Edge {
        protected Node fromNode;
        protected Node toNode;
        protected int weight;

        public Edge(Node fromNode, Node toNode, int weight){
            this.fromNode = fromNode;
            this.toNode = toNode;
            this.weight = weight;
            
        }

        public Node getFromNode() {
            return fromNode;
        }

        public Node getToNode() {
            return toNode;
        }

        public int getWeight(){
            return weight;
        }
    }
    //implements Comparable<Node>

    class Node {
        protected int distFromSource;
        protected Coordinate coordinate;
        protected boolean isVisited = false;
        protected ArrayList<Edge> edges = new ArrayList<Edge>();
        protected int cost;
        protected Node previousNode;
        protected ArrayList<Node> neighbours = new ArrayList<Node>();

        public Node(Coordinate coordinate, int cost, int distFromSource) {
            this.coordinate = coordinate;
            this.cost = cost;
            this.distFromSource = distFromSource;
            previousNode = null;
        }

        public void setPreviousNode(Node node){
            previousNode = node;
        }

        public Node getPreviousNode(){
            return previousNode;
        }

        public ArrayList<Edge> getEdgeList() {
            return edges;
        }

        public int getCost() {
            return cost;
        }

        public boolean getIsVisited(){
            return isVisited;
        }

        public Coordinate getCoor(){
            return coordinate;
        }

        public void setVisited(boolean status){
            isVisited = status;
        }

        public void updateDistFromSource(int prevNodeDistFromSource){
            distFromSource = prevNodeDistFromSource + cost;
        }

        public int getDistFromSource() {
            return distFromSource;
        }

        public void setDistToZero(){
            distFromSource = 0;
        }

        public void setDistToMAX() {
            distFromSource = Integer.MAX_VALUE;
        }

        public ArrayList<Node> getNeighbours(){
            for (Edge edge: edges){
                neighbours.add(edge.getToNode());
            }
            return neighbours;
        }

         @Override
         public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;
            return this.coordinate.getRow() == node.coordinate.getRow() && this.coordinate.getColumn() == node.coordinate.getColumn();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.coordinate.getRow(), this.coordinate.getColumn());
        }

        @Override
        public String toString() {
            return coordinate.toString();
        }

    }
} // end of class DijsktraPathFinder
