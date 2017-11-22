import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MapEditor {

	private static int X_WIDTH = 500;

	private static int Y_HEIGHT = 500;

	private JFrame mainFrame;

	private MapPanel mapPanel;

	private JMenuBar menuBar;

	private Map inputMap;

	private MapReaderWriter mapReaderWriter;

	public MapEditor() {
		mainFrame = new JFrame("Map");
		mainFrame.setSize(X_WIDTH, Y_HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		buildMenu();
		mapReaderWriter = new MapReaderWriter();
		createNewMap();
		mainFrame.add(mapPanel);

		mainFrame.setVisible(true);
	}

	private void buildMenu() {
		menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");

		menuBar.add(fileMenu);

		JMenuItem openFile = new JMenuItem("Open...");
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Open selected");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.map", "map"));
				boolean isError = false;
				Reader input = null;
				do {
					isError = false;
					int returnValue = fileChooser.showOpenDialog(mainFrame);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						try {
							File file = fileChooser.getSelectedFile();
							input = new FileReader(file);
							createNewMap();
							mapReaderWriter.read(input, inputMap);

						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(fileChooser, "File not found");
							isError = true;
						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(fileChooser, "Unexpected Error");
							isError = true;
						} catch (MapFormatException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(fileChooser, "Invalid File Format:\n" + e1.toString());
							isError = true;
						}

					}
				} while (isError);
			}
		});

		fileMenu.add(openFile);

		JMenuItem saveFile = new JMenuItem("Save as...");
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Save as selected");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.map", "map"));
				int returnValue = fileChooser.showOpenDialog(mainFrame);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					try {
						File file = fileChooser.getSelectedFile();
						FileWriter fw = new FileWriter(file);
						mapReaderWriter.write(fw, inputMap);
						fw.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
			}
		});

		fileMenu.add(saveFile);

		JMenuItem appendFile = new JMenuItem("Append...");
		appendFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		appendFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Append selected");
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.map", "map"));
				boolean isError = false;
				Reader input = null;
				do {
					isError = false;
					int returnValue = fileChooser.showOpenDialog(mainFrame);
					if (returnValue == JFileChooser.APPROVE_OPTION) {
						try {
							File file = fileChooser.getSelectedFile();
							input = new FileReader(file);
							mapReaderWriter.read(input, inputMap);
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(fileChooser, "File not found");
							isError = true;
						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(fileChooser, "Unexpected Error");
							isError = true;
						} catch (MapFormatException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(fileChooser, "Invalid File Format:\n" + e1.toString());
							isError = true;
						}
					}
				} while (isError);
			}
		});
		fileMenu.add(appendFile);

		JMenuItem quit = new JMenuItem("Quit...");
		quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(-1);
			}
		});
		fileMenu.add(quit);

		JMenu editMenu = new JMenu("Edit");
		JMenuItem newPlaceItem = new JMenuItem("New place");
		newPlaceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object result = JOptionPane.showInputDialog(mainFrame, "Enter place's name");
				if (result != null) {
					try {
						inputMap.newPlace((String) result, X_WIDTH / 2, Y_HEIGHT / 2);
					} catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(mainFrame, "Invalid Place Name");
					}
				}
			}
		});
		editMenu.add(newPlaceItem);
		JMenuItem newRoadItem = new JMenuItem("New road");

		newRoadItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPanel myPanel = new JPanel();
				JTextField nameField = new JTextField(5);
				JTextField lengthField = new JTextField(5);
				myPanel.add(new JLabel("Name:"));
				myPanel.add(nameField);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("Length:"));
				myPanel.add(lengthField);

				int result = JOptionPane.showConfirmDialog(mainFrame, myPanel, "Please Enter Road's Name and length",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {

					try {
						Integer.parseInt(lengthField.getText());
						mapPanel.addNewRoad(nameField.getText(), Integer.parseInt(lengthField.getText()));

					} catch (NumberFormatException s) {
						JOptionPane.showMessageDialog(mainFrame, "length must be numeric");
					}

				}
			}
		});
		editMenu.add(newRoadItem);

		JMenuItem setStartItem = new JMenuItem("Set start");
		setStartItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component[] components = mapPanel.getComponents();
				PlaceIcon selectedPlace = null;
				int selectedCount = 0;
				for (Component component : components) {
					if (component instanceof PlaceIcon && ((PlaceIcon) component).isSelected) {
						selectedCount++;
						selectedPlace = ((PlaceIcon) component);
					}
				}
				System.out.println("selectedCount:" + selectedCount);

				if (selectedCount > 1) {
					JOptionPane.showMessageDialog(mainFrame, "Only 1 place can be selected");

				} else {
					if (selectedPlace != null) {
						inputMap.setStartPlace(selectedPlace.place);
					} else {
						JOptionPane.showMessageDialog(mainFrame, "Please select 1 place");
					}
				}

			}
		});
		editMenu.add(setStartItem);

		JMenuItem unsetStartItem = new JMenuItem("Unset start");

		unsetStartItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inputMap.setStartPlace(null);
			}
		});
		editMenu.add(unsetStartItem);

		JMenuItem setEndItem = new JMenuItem("Set end");
		setEndItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component[] components = mapPanel.getComponents();
				PlaceIcon selectedPlace = null;
				int selectedCount = 0;
				for (Component component : components) {
					if (component instanceof PlaceIcon && ((PlaceIcon) component).isSelected) {
						selectedCount++;
						selectedPlace = ((PlaceIcon) component);
					}
				}
				System.out.println("selectedCount:" + selectedCount);

				if (selectedCount > 1) {
					JOptionPane.showMessageDialog(mainFrame, "Only 1 place can be selected");

				} else {
					if (selectedPlace != null) {
						inputMap.setEndPlace(selectedPlace.place);
					} else {
						JOptionPane.showMessageDialog(mainFrame, "Please select 1 place");
					}
				}

			}
		});
		editMenu.add(setEndItem);

		JMenuItem unsetEndItem = new JMenuItem("Unset end");
		unsetEndItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				inputMap.setEndPlace(null);
			}
		});
		editMenu.add(unsetEndItem);

		JMenuItem deleteItem = new JMenuItem("Delete");
		deleteItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Component[] components = mapPanel.getComponents();
				for (Component component : components) {
					if (component instanceof PlaceIcon && ((PlaceIcon) component).isSelected) {
						inputMap.deletePlace(((PlaceIcon) component).place);
					} else if (component instanceof RoadIcon && ((RoadIcon) component).isSelected) {
						System.out.println("delete -----road");
						inputMap.deleteRoad(((RoadIcon) component).road);
					}
				}
			}
		});
		editMenu.add(deleteItem);

		menuBar.add(editMenu);

		mainFrame.setJMenuBar(menuBar);
	}

	private void createNewMap() {
		inputMap = new MapImpl();
		if (mapPanel != null) {
			mainFrame.remove(mapPanel);
		}
		mapPanel = new MapPanel();
		mainFrame.add(mapPanel);
		inputMap.addListener(mapPanel);

	}

	public static void main(String[] args) {
		new MapEditor();
	}

	private class MapPanel extends JPanel implements MapListener, MouseListener, MouseMotionListener {

		private static final long serialVersionUID = 1L;

		private Set<Place> placesBeforeChange;

		private Set<Road> roadBeforeChange;

		private int startX;

		private int startY;

		private int currentX;

		private int currentY;

		private States state;

		private int tripDistance;

		private FSA fsa;

		public MapPanel() {
			super(null);
			setBounds(0, 0, X_WIDTH, Y_HEIGHT);
			addMouseListener(this);
			addMouseMotionListener(this);
			placesBeforeChange = new HashSet<Place>();
			roadBeforeChange = new HashSet<Road>();
			this.state = States.NONNEWROAD;
			this.fsa = new FSA();
		}

		@Override
		public void placesChanged() {
			System.out.println("places change");
			addRemovePlaceIcon();
			repaint();
		}

		private void addRemovePlaceIcon() {
			Set<Place> placesAfterChange = inputMap.getPlaces();

			if (placesAfterChange.size() > placesBeforeChange.size()) {
				// add new place
				for (final Place place : placesAfterChange) {
					if (placesBeforeChange.add(place)) {
						System.out.println("add a place:" + place.toString());

						PlaceIcon placeIcon = new PlaceIcon(place);
						mapPanel.add(placeIcon, 0);
					}
				}
			} else {
				Set<Place> placesToDelete = new HashSet<Place>();
				Set<Road> roadsToDelete = new HashSet<Road>();

				for (Place place : placesBeforeChange) {
					// delete a place
					PlaceIcon selectedPlace = null;
					RoadIcon selectedRoad = null;
					if (!placesAfterChange.contains(place)) {
						for (Component component : mapPanel.getComponents()) {
							if (component instanceof PlaceIcon && ((PlaceIcon) component).place == place) {
								selectedPlace = ((PlaceIcon) component);
								selectedPlace.removeMouseListener(selectedPlace);
								selectedPlace.removeMouseMotionListener(selectedPlace);
								mapPanel.remove(selectedPlace);

							} else if (component instanceof RoadIcon
									&& place.toRoads().contains(((RoadIcon) component).road)) {
								selectedRoad = ((RoadIcon) component);
								selectedRoad.removeMouseListener(selectedRoad);
								selectedRoad.removeMouseMotionListener(selectedRoad);
								mapPanel.remove(selectedRoad);
								roadsToDelete.add(selectedRoad.road);
							}
						}

						placesToDelete.add(place);
					}
				}
				placesBeforeChange.removeAll(placesToDelete);
				roadBeforeChange.removeAll(roadsToDelete);
			}
		}

		public void addNewRoad(String name, int length) {
			state = States.NEWROAD;
			fsa.length = length;
			fsa.roadName = name;
			fsa.fsa("newroad");
		}

		@Override
		public void roadsChanged() {
			System.out.println("road change");
			addRemoveRoad();
			repaint();

		}

		private void addRemoveRoad() {
			Set<Road> roadsAfterChange = inputMap.getRoads();

			if (roadsAfterChange.size() > roadBeforeChange.size()) {
				// add new place
				for (final Road road : roadsAfterChange) {
					if (roadBeforeChange.add(road)) {
						System.out.println("add a road:" + road.toString());

						RoadIcon roadIcon = new RoadIcon(road);
						mapPanel.add(roadIcon);
					}
				}
			} else {
				Set<Road> roadsToDelete = new HashSet<Road>();
				for (final Road road : roadBeforeChange) {
					// delete a place
					if (!roadsAfterChange.contains(road)) {
						System.out.println("delete a road:" + road.toString());
						RoadIcon selectedRoad = null;
						for (Component component : mapPanel.getComponents()) {
							if (component instanceof RoadIcon && ((RoadIcon) component).road == road) {
								selectedRoad = ((RoadIcon) component);
								selectedRoad.removeMouseListener(selectedRoad);
								selectedRoad.removeMouseMotionListener(selectedRoad);
								mapPanel.remove(selectedRoad);
							}
						}
						roadsToDelete.add(road);
					}
				}
				roadBeforeChange.removeAll(roadsToDelete);
			}

		}

		@Override
		public void otherChanged() {
			System.out.println("other change");
			this.tripDistance = inputMap.getTripDistance();
			repaint();

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("panel mouseClicked");
			if (state == States.NONNEWROAD) {
				deSelectAll();
				repaint();
			}
		}

		public void deSelectAll() {
			Component[] components = getComponents();
			for (Component component : components) {
				if (component instanceof PlaceIcon) {
					((PlaceIcon) component).isSelected = false;
				} else if (component instanceof RoadIcon) {
					((RoadIcon) component).isSelected = false;
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			startX = e.getX();
			startY = e.getY();

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (state == States.NONNEWROAD) {
				currentX = e.getX();
				currentY = e.getY();
				int x = Math.min(startX, currentX);
				int y = Math.min(startY, currentY);
				int width = Math.abs(startX - currentX);
				int height = Math.abs(startY - currentY);

				Component[] components = getComponents();
				Rectangle dragRectagle = new Rectangle(x, y, width, height);

				for (Component component : components) {
					if ((currentX - startX) > 0) {
						if (component instanceof PlaceIcon) {
							if (dragRectagle.intersects(component.getBounds())) {

								PlaceIcon placeIcon = ((PlaceIcon) component);
								placeIcon.isSelected = true;
							}
						} else if (component instanceof RoadIcon) {
							RoadIcon roadIcon = ((RoadIcon) component);

							int x1 = roadIcon.road.firstPlace().getX();
							int y1 = roadIcon.road.firstPlace().getY();
							int x2 = roadIcon.road.secondPlace().getX();
							int y2 = roadIcon.road.secondPlace().getY();

							if (x1 == x2) {
								x1 = x1 + PlaceIcon.PLACE_ICON_SIZE / 2;
								x2 = x2 + PlaceIcon.PLACE_ICON_SIZE / 2;
								if (y1 > y2) {
									y2 = y2 + PlaceIcon.PLACE_ICON_SIZE;
								} else {
									y1 = y1 + PlaceIcon.PLACE_ICON_SIZE;
								}

							} else if (x1 > x2) {
								x2 = x2 + PlaceIcon.PLACE_ICON_SIZE;
								y1 = y1 + PlaceIcon.PLACE_ICON_SIZE / 2;
								y2 = y2 + PlaceIcon.PLACE_ICON_SIZE / 2;
							} else {
								x1 = x1 + PlaceIcon.PLACE_ICON_SIZE;
								y1 = y1 + PlaceIcon.PLACE_ICON_SIZE / 2;
								y2 = y2 + PlaceIcon.PLACE_ICON_SIZE / 2;

							}

							int xx = Math.min(x1, x2);
							int yy = Math.min(y1, y2);
							int w_width = Math.abs(x1 - x2);
							int h_height = Math.abs(y1 - y2);
							Rectangle rec = new Rectangle(xx, yy, w_width, h_height);

							if (dragRectagle.contains(rec.getBounds())) {
								roadIcon.isSelected = true;
							}

						}
					}
				}
				currentX = 0;
				currentY = 0;
				startX = 0;
				startY = 0;
				repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (state == States.NONNEWROAD) {
				currentX = e.getX();
				currentY = e.getY();
				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			if (state == States.CHOOSEEND) {
				currentX = e.getX();
				currentY = e.getY();
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (state == States.NONNEWROAD) {
				g.setColor(Color.BLUE);
				if (currentX - startX > 0) {
					int x = Math.min(startX, currentX);
					int y = Math.min(startY, currentY);
					int width = Math.abs(startX - currentX);
					int height = Math.abs(startY - currentY);
					g.drawRect(x, y, width, height);
				}
			} else if (state == States.CHOOSEEND) {
				((Graphics2D) g).setStroke(new BasicStroke(2));
				g.setColor(Color.magenta);

				int x1 = fsa.startPlace.getX();
				int y1 = fsa.startPlace.getY();
				int x2 = currentX;
				int y2 = currentY;
				System.out.println("x:" + currentX);
				System.out.println("y:" + currentY);

				if (x1 == x2) {
					x1 = x1 + PlaceIcon.PLACE_ICON_SIZE / 2;
					x2 = x2 + PlaceIcon.PLACE_ICON_SIZE / 2;
					if (y1 > y2) {
						y2 = y2 + PlaceIcon.PLACE_ICON_SIZE;
					} else {
						y1 = y1 + PlaceIcon.PLACE_ICON_SIZE;
					}

				} else if (x1 > x2) {
					x2 = x2 + PlaceIcon.PLACE_ICON_SIZE;
					y1 = y1 + PlaceIcon.PLACE_ICON_SIZE / 2;
					y2 = y2 + PlaceIcon.PLACE_ICON_SIZE / 2;
				} else {
					x1 = x1 + PlaceIcon.PLACE_ICON_SIZE;
					y1 = y1 + PlaceIcon.PLACE_ICON_SIZE / 2;
					y2 = y2 + PlaceIcon.PLACE_ICON_SIZE / 2;

				}

				g.drawLine(x1, y1, x2, y2);

			}
			g.setColor(Color.black);

			if (tripDistance > 0) {
				g.drawString("Distance:" + tripDistance, 10, 10);
			} else {
				g.drawString("No route", 10, 10);
			}
		}

	}

	private class PlaceIcon extends JComponent implements PlaceListener, MouseListener, MouseMotionListener {

		private static final long serialVersionUID = 1L;

		public static final int PLACE_ICON_SIZE = 10;

		private Place place;

		private boolean isSelected;

		private int startX;

		private int startY;

		public PlaceIcon(Place place) {
			super();
			this.place = place;
			this.isSelected = false;
			setBounds(place.getX(), place.getY(), PLACE_ICON_SIZE, PLACE_ICON_SIZE);
			addMouseListener(this);
			addMouseMotionListener(this);
			place.addListener(this);
		}

		@Override
		public void placeChanged() {
			System.out.println("place icon - place change");
			setLocation(place.getX(), place.getY());
			repaint();

		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (isSelected) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.DARK_GRAY);
			}
			if (place.isStartPlace()) {
				g.setColor(Color.GREEN);
			} else if (place.isEndPlace()) {
				g.setColor(Color.BLUE);
			}
			if (isSelected) {
				g.fillRect(0, 0, PLACE_ICON_SIZE, PLACE_ICON_SIZE);
			} else {
				((Graphics2D) g).setStroke(new BasicStroke(5));
				g.drawRect(0, 0, PLACE_ICON_SIZE, PLACE_ICON_SIZE);
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			System.out.println("place icon - mouseClicked");
			if (mapPanel.state == States.NONNEWROAD) {
				isSelected = !isSelected;
				repaint();
			} else {
				if (mapPanel.state == States.CHOOSESTART) {
					mapPanel.fsa.startPlace = place;
				} else if (mapPanel.state == States.CHOOSEEND) {
					mapPanel.fsa.endPlace = place;
				}
				mapPanel.fsa.fsa("click");
			}

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			System.out.println("mouseEntered");

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			System.out.println("mouseExited");

		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			startX = arg0.getX();
			startY = arg0.getY();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			System.out.println("mouseReleased");

		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			if (mapPanel.state == States.NONNEWROAD) {
				place.moveBy(arg0.getX() - startX, arg0.getY() - startY);
			}
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			System.out.println("mouseMoved");

		}
	}

	private class RoadIcon extends JComponent implements RoadListener, MouseListener, MouseMotionListener {

		private static final long serialVersionUID = 1L;

		private static final int OFFSET = 200;

		private Road road;

		private boolean isSelected;

		private int x1;
		private int x2;

		private int y1;
		private int y2;

		public RoadIcon(Road road) {
			super();
			this.road = road;
			road.addListener(this);
			setRoadBounds();
			addMouseListener(this);
			addMouseMotionListener(this);

		}

		private void setRoadBounds() {
			int x = Math.min(road.firstPlace().getX(), road.secondPlace().getX());
			int y = Math.min(road.firstPlace().getY(), road.secondPlace().getY());
			int width = Math.abs(road.firstPlace().getX() - road.secondPlace().getX());
			int height = Math.abs(road.firstPlace().getY() - road.secondPlace().getY());
			setBounds(x, y, width + OFFSET, height + OFFSET);
		}

		@Override
		public void roadChanged() {
			System.out.println("roadIcon roadChanged");
			setRoadBounds();
			revalidate();
			repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			x1 = road.firstPlace().getX();
			x2 = road.secondPlace().getX();
			y1 = road.firstPlace().getY();
			y2 = road.secondPlace().getY();

			if (x1 == x2) {
				x1 = x1 + PlaceIcon.PLACE_ICON_SIZE / 2;
				x2 = x2 + PlaceIcon.PLACE_ICON_SIZE / 2;
				if (y1 > y2) {
					y2 = y2 + PlaceIcon.PLACE_ICON_SIZE;
				} else {
					y1 = y1 + PlaceIcon.PLACE_ICON_SIZE;
				}

			} else if (x1 > x2) {
				x2 = x2 + PlaceIcon.PLACE_ICON_SIZE;
				y1 = y1 + PlaceIcon.PLACE_ICON_SIZE / 2;
				y2 = y2 + PlaceIcon.PLACE_ICON_SIZE / 2;
			} else {
				x1 = x1 + PlaceIcon.PLACE_ICON_SIZE;
				y1 = y1 + PlaceIcon.PLACE_ICON_SIZE / 2;
				y2 = y2 + PlaceIcon.PLACE_ICON_SIZE / 2;

			}

			((Graphics2D) g).setStroke(new BasicStroke(2));
			if (isSelected) {
				g.setColor(Color.RED);
			} else {
				if (road.isChosen()) {
					g.setColor(Color.ORANGE);
				} else {
					g.setColor(Color.DARK_GRAY);
				}

			}
			x1 = x1 - getX();
			x2 = x2 - getX();
			y1 = y1 - getY();
			y2 = y2 - getY();

			g.drawString(road.roadName() + "(" + road.length() + ")", Math.abs(getWidth() - OFFSET) / 2,
					Math.abs(getHeight() - OFFSET) / 2 + 20);

			g.drawLine(x1, y1, x2, y2);

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			System.out.println("Road mouse click");
			if (mapPanel.state == States.NONNEWROAD) {
				int x = e.getX();
				int y = e.getY();
				int xInPanel = x + this.getX();
				int yInPanel = y + this.getY();

				if (!checkInterSectForAllRoad(xInPanel, yInPanel)) {
					mapPanel.deSelectAll();

				}

			}

		}

		private boolean checkInterSectForAllRoad(int x, int y) {
			boolean intersect = false;
			for (Component component : mapPanel.getComponents()) {
				if (component instanceof RoadIcon) {
					RoadIcon roadIcon = (RoadIcon) component;
					if (roadIcon.isIntersect(roadIcon, x - roadIcon.getX(), y - roadIcon.getY())) {
						roadIcon.isSelected = !isSelected;
						intersect = true;
						roadIcon.repaint();
					}
				}

			}
			return intersect;
		}

		private boolean isIntersect(RoadIcon roadIcon, int x, int y) {
			Rectangle r1 = new Rectangle(x-1, y-1, 4, 4);
			Line2D l1 = new Line2D.Float(roadIcon.x1, roadIcon.y1, roadIcon.x2, roadIcon.y2);
			return l1.intersects(r1);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (mapPanel.state == States.NONNEWROAD) {
				int x = e.getX();
				int y = e.getY();
				mapPanel.startX = x + this.getX();
				mapPanel.startY = y + this.getY();

			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (mapPanel.state == States.NONNEWROAD) {
				int x = e.getX();
				int y = e.getY();
				if ((y - y1) * (x2 - x1) != (y2 - y1) * (x - x1)) {
					mapPanel.mouseReleased(e);
				}
			}

		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			if (mapPanel.state == States.NONNEWROAD) {
				int x = arg0.getX();
				int y = arg0.getY();
				if ((y - y1) * (x2 - x1) != (y2 - y1) * (x - x1)) {
					mapPanel.mouseDragged(arg0);
				}
			}

		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			if (mapPanel.state  == States.CHOOSEEND) {
				int x = arg0.getX();
				int y = arg0.getY();
				mapPanel.currentX = x + this.getX();
				mapPanel.currentY = y + this.getY();
				mapPanel.repaint();
			}

		}
	}

	public enum States {
		NEWROAD, CHOOSESTART, CHOOSEEND, NONNEWROAD
	};

	private class FSA {

		private Place startPlace;

		private Place endPlace;

		private String roadName;

		private int length;

		public FSA() {
		}

		public void fsa(String event) {
			States currentState = mapPanel.state;

			switch (currentState) {
			case NEWROAD:
				if ("newroad".equals(event)) {
					mapPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
				}
				mapPanel.state = States.CHOOSESTART;
				break;
			case CHOOSESTART:

				mapPanel.state = States.CHOOSEEND;

				break;
			case CHOOSEEND:
				if ("click".equals(event) && endPlace != startPlace) {
					mapPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					mapPanel.currentX = 0;
					mapPanel.currentY = 0;
					mapPanel.startX = 0;
					mapPanel.startY = 0;
					try {
						if (inputMap.getRoads().contains(startPlace.roadTo(endPlace))) {
							throw new IllegalArgumentException("Road already exists");
						}

						inputMap.newRoad(startPlace, endPlace, roadName, length);
					} catch (IllegalArgumentException ex) {
						JOptionPane.showMessageDialog(mainFrame, "Error adding road:" + ex.toString());
						mapPanel.repaint();

					}

					mapPanel.state = States.NONNEWROAD;
					this.startPlace = null;
					this.endPlace = null;
				}

				break;
			default:
				mapPanel.state = States.NONNEWROAD;
			}

		}
	}

}
