package pathFinder;

import map.Coordinate;
import map.PathMap;

import java.util.*;

public class DijkstraPathFinder implements PathFinder
{
    // TODO: You might need to implement some attributes
	private PathMap map;
	private int coordinatesExplored = 0;
	private Coordinate cells[][];
	private List<Coordinate> toVisit = new ArrayList<>();
	private List<Coordinate> evaluatedNodes = new ArrayList<>();
	List<Coordinate> previousNodes = new ArrayList<>();

    public DijkstraPathFinder(PathMap map) {
        // TODO :Implement
		this.map = map;
		cells = map.cells;
		addAllUnvisited();
    } // end of DijkstraPathFinder()

    @Override
    public List<Coordinate> findPath() {
        // You can replace this with your favourite list, but note it must be a
        // list type
		Coordinate origin = map.originCells.get(0);
		Coordinate destination = map.destCells.get(0);
        List<Coordinate> path = new ArrayList<>();
		
		path.add(origin);
		visitCoordinate(origin, 0, path);
		
        // TODO: Implement

        return path;
    } // end of findPath()


    @Override
    public int coordinatesExplored() {
        // TODO: Implement (optional)
		
        // placeholder
        return coordinatesExplored;
    } // end of cellsExplored()
	
	private void addAllUnvisited() {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j].getImpassable() == false) {
					toVisit.add(cells[i][j]);
				}
			}
		}
	}
	
	private Coordinate validCoordinate(List<Coordinate> coordinates) {
		Coordinate valid = null;
		
		for (Coordinate c : coordinates) {
			if (c.getDistanceFromSource() == Integer.MAX_VALUE) {
				valid = c;
				break;
			}
		}
		
		return valid;
	}
	
	private void visitCoordinate(Coordinate coordinate, int distanceFromSource, List<Coordinate> path) {
		coordinate.setDistanceFromSource(distanceFromSource);
		previousNodes.add(coordinate);
		
		List<Coordinate> neighbours = new ArrayList<>();
		for (Coordinate c : previousNodes) {
			neighbours.addAll(getAdjacentCoordinates(c));
		}
		
		for (int i = 0; i < neighbours.size(); i++) {
			int distance = previousNodes.get(0).getDistanceFromSource() + 1;
			if (neighbours.get(i).getDistanceFromSource() > distance) {
				neighbours.get(i).setDistanceFromSource(distance);
			}
		}
		
		evaluatedNodes.addAll(previousNodes);
		previousNodes.clear();
		previousNodes.addAll(neighbours);
		
		for (Coordinate c : neighbours) {
			System.out.printf("%s: %d\n", c, c.getDistanceFromSource());
			path.add(c);
		}
		
		neighbours.clear();
		
		for (int i = 0; i < 10; i++) {
			for (Coordinate c : previousNodes) {
				neighbours.addAll(getAdjacentCoordinates(c));
			}
			
			for (int j = 0; j < neighbours.size(); j++) {
				int distance = previousNodes.get(0).getDistanceFromSource() + 1;
				Coordinate currentNode = neighbours.get(j);
				if (evaluatedNodes.contains(currentNode)) {
					for (Coordinate c : evaluatedNodes) {
						if (c.equals(currentNode)) {
							neighbours.get(j).setDistanceFromSource(c.getDistanceFromSource());
						}
					}
				}
				if (currentNode.getDistanceFromSource() > distance) {
					neighbours.get(j).setDistanceFromSource(distance);
				} 
			}
			
			evaluatedNodes.addAll(previousNodes);
			previousNodes.clear();
			previousNodes.addAll(neighbours);
			
			for (Coordinate c : neighbours) {
				System.out.printf("%s: %d\n", c, c.getDistanceFromSource() );
				path.add(c);
			}
			
			neighbours.clear();
		}
	}
	
	private List<Coordinate> getAdjacentCoordinates(Coordinate coordinate) {
		List<Coordinate> nearestCoordinates = new ArrayList<>();
		int coordX = coordinate.getRow();
		int coordY = coordinate.getColumn();
		
		if (coordX + 1 < map.sizeC && cells[coordX + 1][coordY] != null && !cells[coordX + 1][coordY].getImpassable()) {
			nearestCoordinates.add(cells[coordX + 1][coordY]);
		}
		if (coordX - 1 >= 0 && cells[coordX - 1][coordY] != null && !cells[coordX - 1][coordY].getImpassable()) {
			nearestCoordinates.add(cells[coordX - 1][coordY]);
		}
		if (coordY + 1 < map.sizeR && cells[coordX][coordY + 1] != null && !cells[coordX][coordY + 1].getImpassable()) {
			nearestCoordinates.add(cells[coordX][coordY + 1]);
		}
		if (coordY - 1 >= 0 && cells[coordX][coordY - 1] != null && !cells[coordX][coordY - 1].getImpassable()) {
			nearestCoordinates.add(cells[coordX][coordY - 1]);
		}
		
		return nearestCoordinates;
	}
} // end of class DijsktraPathFinder
