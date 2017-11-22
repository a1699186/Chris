import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MapImpl implements Map {

	private Place startPlace;

	private Place endPlace;

	private Set<Place> places;

	private Set<Road> roads;

	private List<MapListener> mapListeners;

	private List<Road> selectedRoads;

	public MapImpl() {
		places = new HashSet<Place>();
		roads = new HashSet<Road>();
		mapListeners = new ArrayList<MapListener>();
		selectedRoads = new LinkedList<Road>();
	}

	@Override
	public void addListener(MapListener ml) {
		mapListeners.add(ml);
	}

	private void notifyPlacesChange() {
		for (MapListener ml : mapListeners) {
			ml.placesChanged();
		}
	}

	private void notifyRoadsChange() {
		for (MapListener ml : mapListeners) {
			ml.roadsChanged();
		}
	}

	private void notifyOtherChange() {
		for (MapListener ml : mapListeners) {
			ml.otherChanged();
		}
	}

	@Override
	public void deleteListener(MapListener ml) {
		mapListeners.remove(ml);
	}

	@Override
	public Place newPlace(String placeName, int xPos, int yPos) throws IllegalArgumentException {
		// start with letter [a-zA-Z],
		// zero or more letter, digit, or underscore characters [_a-zA-Z0-9]*
		if (placeName == null || !placeName.matches("[a-zA-Z][_a-zA-Z0-9]*")) {
			throw new IllegalArgumentException("Invalid place's name");
		}

		if (findPlace(placeName) != null) {
			throw new IllegalArgumentException("Places existes in the map");
		}
		Place place = new PlaceImpl(placeName, xPos, yPos);

		if (!places.add(place)) {
			// place already existed in the map
			throw new IllegalArgumentException("Places existes in the map");
		} else {
			notifyPlacesChange();
		}
		return place;
	}

	@Override
	public void deletePlace(Place s) {

		if (places.contains(s)) {
			places.remove(s);
			notifyPlacesChange();

			for (Road road : s.toRoads()) {
				if (road.firstPlace().equals(s)) {
					((PlaceImpl) road.secondPlace()).notifyPlaceChanged();
				} else {
					((PlaceImpl) road.firstPlace()).notifyPlaceChanged();
				}
			}

			if (roads.removeAll(s.toRoads())) {
				notifyRoadsChange();
			}

			if (startPlace == s) {
				startPlace = null;
				((PlaceImpl) s).notifyPlaceChanged();
				notifyOtherChange();
			}
			if (endPlace == s) {
				endPlace = null;
				((PlaceImpl) s).notifyPlaceChanged();
				notifyOtherChange();
			}
		}

	}

	@Override
	public Place findPlace(String placeName) {
		for (Place place : places) {
			if (place.getName().equals(placeName)) {
				return place;
			}
		}
		return null;
	}

	@Override
	public Set<Place> getPlaces() {
		return new HashSet<Place>(places);
	}

	@Override
	public Road newRoad(Place from, Place to, String roadName, int length) throws IllegalArgumentException {
		if (!places.contains(from) || !places.contains(to) || length < 0) {
			throw new IllegalArgumentException();
		}

		if (roadName == null || (!"".equals(roadName) && !roadName.matches("[a-zA-Z][a-zA-Z0-9]*"))) {
			throw new IllegalArgumentException("Invalid road's name");
		}

		Road road = new RoadImpl(from, to, roadName, length);
		int tripDistanceBeforeAdd = getTripDistance();
		if (roads.add(road)) {
			notifyRoadsChange();
			((PlaceImpl) from).notifyPlaceChanged();
			((PlaceImpl) to).notifyPlaceChanged();
			int tripDistanceAfterAdd = getTripDistance();
			if (tripDistanceBeforeAdd != tripDistanceAfterAdd) {
				notifyOtherChange();
			}
		}
		return road;
	}

	@Override
	public void deleteRoad(Road r) {
		if (roads.contains(r)) {
			roads.remove(r);
			notifyRoadsChange();
			((PlaceImpl) r.firstPlace()).notifyPlaceChanged();
			((PlaceImpl) r.secondPlace()).notifyPlaceChanged();
		}
	}

	@Override
	public Set<Road> getRoads() {
		return new HashSet<Road>(roads);
	}

	@Override
	public void setStartPlace(Place p) throws IllegalArgumentException {
		if (p != null && !places.contains(p)) {
			throw new IllegalArgumentException();
		}

		if (startPlace != p) {
			if (startPlace != null) {
				((PlaceImpl) startPlace).notifyPlaceChanged();
			}
			if (p != null) {
				((PlaceImpl) p).notifyPlaceChanged();
			}
			this.startPlace = p;
			notifyOtherChange();

		}

	}

	@Override
	public Place getStartPlace() {
		return startPlace;
	}

	@Override
	public void setEndPlace(Place p) throws IllegalArgumentException {
		if (p != null && !places.contains(p)) {
			throw new IllegalArgumentException();
		}

		if (endPlace != p) {
			if (endPlace != null) {
				((PlaceImpl) endPlace).notifyPlaceChanged();
			}
			if (p != null) {
				((PlaceImpl) p).notifyPlaceChanged();
			}
			this.endPlace = p;
			notifyOtherChange();

		}

	}

	@Override
	public Place getEndPlace() {
		return endPlace;
	}

	@Override
	public int getTripDistance() {
		selectedRoads = new LinkedList<Road>();
		if (startPlace == null || endPlace == null) {
			return -1;
		}
		Set<Place> settledNodes = new HashSet<Place>();
		Set<Place> unSettledNodes = new HashSet<Place>();
		java.util.Map<Place, Integer> distance = new HashMap<Place, Integer>();
		java.util.Map<Place, Place> path = new HashMap<Place, Place>();
		distance.put(startPlace, 0);
		unSettledNodes.add(startPlace);
		while (unSettledNodes.size() > 0) {
			Place node = getMinimum(unSettledNodes, distance);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node, unSettledNodes, settledNodes, distance, path);
		}

		Place step = endPlace;
		// check if a path exists
		if (path.get(step) == null) {
			return -1;
		}
		// path.add(step);
		while (path.get(step) != null) {
			Road road = step.roadTo(path.get(step));
			if (road != null) {
				selectedRoads.add(road);
			}
			step = path.get(step);
		}
		//
		if (distance.get(endPlace) != null) {
			return distance.get(endPlace);
		} else {
			return -1;
		}
	}

	private Place getMinimum(Set<Place> vertexes, java.util.Map<Place, Integer> distance) {
		Place minimum = null;
		for (Place vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex, distance) < getShortestDistance(minimum, distance)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private void findMinimalDistances(Place node, Set<Place> unSettledNodes, Set<Place> settledNodes,
			java.util.Map<Place, Integer> distance, java.util.Map<Place, Place> path) {

		for (Road edge : node.toRoads()) {
			Place target = null;
			if (edge.firstPlace() == node && !settledNodes.contains(edge.secondPlace())) {
				target = edge.secondPlace();
			} else if (edge.secondPlace() == node && !settledNodes.contains(edge.firstPlace())) {
				target = edge.firstPlace();
			}
			if (target != null) {
				if (getShortestDistance(target, distance) > getShortestDistance(node, distance) + edge.length()) {
					distance.put(target, getShortestDistance(node, distance) + getDistance(node, target));
					path.put(target, node);
					unSettledNodes.add(target);
				}
			}
		}
	}

	private int getShortestDistance(Place destination, java.util.Map<Place, Integer> distance) {
		Integer d = distance.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
	}

	private int getDistance(Place node, Place target) {
		return node.roadTo(target).length();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Place place : places) {
			sb.append("PLACE " + place.toString() + "\n");
		}

		for (Road road : roads) {
			sb.append("ROAD " + road.toString() + "\n");
		}

		if (startPlace != null) {
			sb.append("START " + startPlace.getName() + "\n");
		}
		if (endPlace != null) {
			sb.append("END " + endPlace.getName() + "\n");
		}
		return sb.toString();
	}

	private class PlaceImpl implements Place {

		private String name;
		private int X;
		private int Y;
		private List<PlaceListener> placeListeners;

		public PlaceImpl(String name, int X, int Y) {
			this.name = name;
			this.X = X;
			this.Y = Y;
			placeListeners = new ArrayList<PlaceListener>();
		}

		@Override
		public void addListener(PlaceListener pl) {
			placeListeners.add(pl);

		}

		@Override
		public void deleteListener(PlaceListener pl) {
			placeListeners.remove(pl);
		}

		@Override
		public Set<Road> toRoads() {
			Set<Road> toRoads = new HashSet<Road>();
			for (Road road : roads) {
				if (road.firstPlace().equals(this) || road.secondPlace().equals(this)) {
					toRoads.add(road);
				}
			}
			return toRoads;
		}

		@Override
		public Road roadTo(Place dest) {
			for (Road road : toRoads()) {
				if (road.firstPlace().equals(this) && road.secondPlace().equals(dest)
						|| road.secondPlace().equals(this) && road.firstPlace().equals(dest)) {
					return road;
				}
			}
			return null;
		}

		@Override
		public void moveBy(int dx, int dy) {
			this.X = this.X + dx;
			this.Y = this.Y + dy;
			notifyPlaceChanged();
			for (Road road : roads) {
				((RoadImpl) road).notifyRoadChanged();
			}
		}

		private void notifyPlaceChanged() {
			for (PlaceListener listener : placeListeners) {
				listener.placeChanged();
			}
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getX() {
			return X;
		}

		@Override
		public int getY() {
			return Y;
		}

		@Override
		public boolean isStartPlace() {
			return startPlace != null && startPlace.equals(this);
		}

		@Override
		public boolean isEndPlace() {
			return endPlace != null && endPlace.equals(this);
		}

		@Override
		public String toString() {
			return name + "(" + X + "," + Y + ")";
		}
	}

	private class RoadImpl implements Road {

		private Place firstPlace;
		private Place secondPlace;
		private String roadName;
		private boolean chosen;
		private int length;
		private List<RoadListener> roadListeners;

		public RoadImpl(Place from, Place to, String roadName, int length) {
			int compare = from.getName().compareTo(to.getName());

			if (compare < 0) {
				this.firstPlace = from;
				this.secondPlace = to;
			} else {
				this.firstPlace = to;
				this.secondPlace = from;
			}
			// add all road to this place
			this.firstPlace.toRoads().add(this);
			this.secondPlace.toRoads().add(this);
			this.roadName = roadName;
			this.length = length;
			this.chosen = false;
			roadListeners = new ArrayList<RoadListener>();

		}

		@Override
		public void addListener(RoadListener rl) {
			roadListeners.add(rl);

		}

		@Override
		public void deleteListener(RoadListener rl) {
			roadListeners.remove(rl);
		}

		private void notifyRoadChanged() {
			for (RoadListener listener : roadListeners) {
				listener.roadChanged();
			}
		}

		@Override
		public Place firstPlace() {
			return firstPlace;
		}

		@Override
		public Place secondPlace() {
			return secondPlace;
		}

		@Override
		public boolean isChosen() {
			return selectedRoads.contains(this);
		}

		@Override
		public String roadName() {
			return roadName;
		}

		@Override
		public int length() {
			return length;
		}

		@Override
		public String toString() {
			return firstPlace.getName() + "(" + roadName + ":" + length + ")" + secondPlace.getName();

		}
	}

}
