package gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;

import logic.*;
public class ElevatorSimulation {
	
	public JTextArea logArea; //Elevator logs
	JCheckBox passengerCheckbox;//Passenger sim checkbox
	JLabel[] stopLabels = new JLabel[ELEVATORS];
    public static final int FLOORS = 10;
    public static final int ELEVATORS = 4;
    public static final int MAX_CAPACITY=10;
    JFrame frame;
    JPanel gridPanel;
    public boolean isPassengerBehaviourEnabled=false;

    Elevator[] elevators = new Elevator[ELEVATORS];
    JLabel[][] elevatorCells = new JLabel[FLOORS][ELEVATORS];
    
    public JButton[] upButtons=new JButton[FLOORS];
    public JButton[] downButtons=new JButton[FLOORS];

    java.util.List<Request> pendingRequests = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ElevatorSimulation().start());
    }

    void start() {

        for (int i = 0; i < ELEVATORS; i++) {
            elevators[i] = new Elevator(i, 0, MAX_CAPACITY,this);
        }

        frame = new JFrame("Elevator Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());
        frame.setResizable(true);

        gridPanel = new JPanel(new GridLayout(FLOORS, ELEVATORS + 2));

        for (int floor = FLOORS - 1; floor >= 0; floor--) {

            int f= floor;

            JButton up = new JButton("↑");
            JButton down = new JButton("↓");
            up.setBackground(Color.gray);
            down.setBackground(Color.gray);
            
            this.upButtons[f]=up;
            this.downButtons[f]=down;

            up.addActionListener(e -> requestElevator(f, Direction.UP));
            down.addActionListener(e -> requestElevator(f, Direction.DOWN));

            gridPanel.add(up);
            gridPanel.add(down);

            for (int e = 0; e < ELEVATORS; e++) {

                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                elevatorCells[floor][e] = cell;
                gridPanel.add(cell);

            }
        }

        frame.add(gridPanel,BorderLayout.CENTER);
        
        //Additional UI building-START
        
     // EAST PANEL --> Checkbox and logs
        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BorderLayout());

        // Passenger checkbox
        passengerCheckbox = new JCheckBox("Enable Passenger Simulation");
        passengerCheckbox.addActionListener(e -> {
            isPassengerBehaviourEnabled = passengerCheckbox.isSelected();
        });

        // Panel for checkbox (top of east panel)
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.add(passengerCheckbox);

        // Log area
        logArea = new JTextArea(20, 20);
        logArea.setEditable(false);

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("Elevator Logs"));

        // Add to east panel
        eastPanel.add(checkboxPanel, BorderLayout.NORTH);
        eastPanel.add(logScroll, BorderLayout.CENTER);

        // Add east panel to frame
        frame.add(eastPanel, BorderLayout.EAST);
        
        
        //South Panel- Pending Stops for each elevator
    
        JPanel stopsPanel = new JPanel();
        stopsPanel.setLayout(new GridLayout(ELEVATORS, 1));
        stopsPanel.setBorder(BorderFactory.createTitledBorder("Elevator Stops"));

        for (int i = 0; i < ELEVATORS; i++) {

            stopLabels[i] = new JLabel("Elevator " + i + " stops: ");
            stopsPanel.add(stopLabels[i]);

        }

        frame.add(stopsPanel, BorderLayout.SOUTH);
        
        
        
        //Additional UI building-END
        frame.setVisible(true);
        
        Timer timer = new Timer(1000, (e)->update());
        timer.start();
    }
    
    void updateStopDisplay() {

        for (int i = 0; i < ELEVATORS; i++) {

            Elevator e = elevators[i];

            String stops =
                "UP: " + e.upStops.toString() +
                " | DOWN: " + e.downStops.toString();

            stopLabels[i].setText(
                "Elevator " + i + " stops -> " + stops
            );
        }
    }

    void requestElevator(int floor, Direction dir) {
    	JButton floorButton=dir.equals(Direction.DOWN)?this.downButtons[floor]:this.upButtons[floor];
        floorButton.setBackground(Color.ORANGE);
    	pendingRequests.add(new Request(floor, dir));
    }

    void update() {

        assignRequests();

        for (Elevator e : elevators) {
            e.move();
        }

        refreshGUI();
        updateStopDisplay();
    }
    
    //improved assignRequests to consider direction.
    void assignRequests() {

        Iterator<Request> it = pendingRequests.iterator();

        while (it.hasNext()) {

            Request r = it.next();
            Elevator best = null;
            int bestDistance = Integer.MAX_VALUE;

            // FIRST PASS: elevators already moving toward request
            for (Elevator e : elevators) {

                if (e.isFull())
                    continue;

                boolean toward =
                    (e.direction == Direction.UP && r.floor >= e.currentFloor) ||
                    (e.direction == Direction.DOWN && r.floor <= e.currentFloor);

                if (toward) {
                    int distance = Math.abs(e.currentFloor - r.floor);

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = e;
                    }
                }
            }

            // SECOND PASS: idle elevators
            if (best == null) {
                for (Elevator e : elevators) {

                    if (e.direction == Direction.IDLE && !e.isFull()) {

                        int distance = Math.abs(e.currentFloor - r.floor);

                        if (distance < bestDistance) {
                            bestDistance = distance;
                            best = e;
                        }
                    }
                }
            }

            if (best != null) {
                best.addStop(r.floor);
                it.remove();
            }
        }
    }
    
    

//    void assignRequests() {
//
//        Iterator<Request> it = pendingRequests.iterator();
//
//        while (it.hasNext()) {
//
//            Request r = it.next();
//            Elevator best = null;
//            int bestCost = Integer.MAX_VALUE;
//
//            for (Elevator e : elevators) {
//
//                if (e.isFull())
//                    continue;
//
//                int cost = Math.abs(e.currentFloor - r.floor);
//
//                if (cost < bestCost) {
//                    bestCost = cost;
//                    best = e;
//                }
//            }
//
//            if (best != null) {
//                best.addStop(r.floor);
//                it.remove();
//            }
//        }
//    }

    void refreshGUI() {

        for (int f = 0; f < FLOORS; f++) {
            for (int e = 0; e < ELEVATORS; e++) {

                elevatorCells[f][e].setBackground(null);
                elevatorCells[f][e].setOpaque(false);
                elevatorCells[f][e].setText("");

                if (elevators[e].currentFloor == f) {
                    elevatorCells[f][e].setOpaque(true);
                    elevatorCells[f][e].setBackground(Color.GREEN);
                    elevatorCells[f][e].setText("E" + e+"\n  ("+elevators[e].capacity+")  ");
                }
            }
        }
    }

}
