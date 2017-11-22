import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class MapReaderWriter implements MapIo {

	@Override
	public void read(Reader r, Map m) throws IOException, MapFormatException {
		BufferedReader br = new BufferedReader(r);
		String sCurrentLine;
		int lineNo = 1;
		try {
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.startsWith("#") || sCurrentLine.trim().length() == 0) {
					continue;
				}

				sCurrentLine = sCurrentLine.trim();
				String[] splited = sCurrentLine.split("\\s+");
				if ("place".equals(splited[0])) {

					if (splited.length < 4) {
						throw new MapFormatException(lineNo, "Invalid place line");
					}

					Place place = m.findPlace(splited[1]);
					if (place != null) {
						throw new MapFormatException(lineNo, "Place record place already exists");
					}

					if (!splited[1].matches("[a-zA-Z][_a-zA-Z0-9]*")) {
						throw new MapFormatException(lineNo, "Invalid Place's Name");
					}

					if (!isNumeric(splited[2])) {
						throw new MapFormatException(lineNo, "Not a numeric  [" + splited[2] + "]");
					}

					if (!isNumeric(splited[3])) {
						throw new MapFormatException(lineNo, "Not a numeric  [" + splited[3] + "]");
					}

					m.newPlace(splited[1], Integer.valueOf(splited[2]), Integer.valueOf(splited[3]));

				} else if ("road".equals(splited[0])) {
					if (splited.length < 5) {
						throw new MapFormatException(lineNo, "Invalid road line");
					}

					Place firstPlace = m.findPlace(splited[1]);
					Place secondPlace = m.findPlace(splited[4]);
					if (firstPlace == null || secondPlace == null) {
						throw new MapFormatException(lineNo, "Road record place is not defined");
					}
					String roadName = "-".equals(splited[2]) ? "" : splited[2];

					if (!isNumeric(splited[3])) {
						throw new MapFormatException(lineNo, "Not a numeric  [" + splited[3] + "]");
					}

					if (!"".equals(roadName) && !roadName.matches("[a-zA-Z][a-zA-Z0-9]*")
							|| Integer.valueOf(splited[3]) < 0) {
						throw new MapFormatException(lineNo, "Invalid Road's Name");
					}

					m.newRoad(firstPlace, secondPlace, roadName, Integer.valueOf(splited[3]));
				} else if ("start".equals(splited[0])) {

					if (splited.length < 2) {
						throw new MapFormatException(lineNo, "Invalid start place line");
					}
					Place startPlace = m.findPlace(splited[1]);
					if (startPlace == null) {
						throw new MapFormatException(lineNo, "Start record place is not defined");
					}
					m.setStartPlace(startPlace);

				} else if ("end".equals(splited[0])) {

					if (splited.length < 2) {
						throw new MapFormatException(lineNo, "Invalid end place line");
					}

					Place endPlace = m.findPlace(splited[1]);
					if (endPlace == null) {
						throw new MapFormatException(lineNo, "End record place is not defined");
					}
					m.setEndPlace(endPlace);

				} else {
					throw new MapFormatException(lineNo, "Unidentified record type");
				}
				lineNo++;
			}
		} catch (IllegalArgumentException ex) {
			throw new MapFormatException(lineNo, ex.getMessage());
		}

	}

	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public void write(Writer bw, Map m) throws IOException {

		for (Place place : m.getPlaces()) {
			bw.write("place " + place.getName() + " " + place.getX() + " " + place.getY());
			bw.write("\n");
		}

		for (Road road : m.getRoads()) {
			bw.write("road " + road.firstPlace().getName() + " " + road.roadName() + " " + road.length() + " "
					+ road.secondPlace().getName());
			bw.write("\n");
		}
		if (m.getStartPlace() != null) {
			bw.write("start " + m.getStartPlace().getName() + "\n");
		}
		if (m.getEndPlace() != null) {
			bw.write("end " + m.getEndPlace().getName() + "\n");
		}
	}

}
