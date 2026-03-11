package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;

import logic.*;
public class ElevatorSimulation {

    static final int FLOORS = 10;
    static final int ELEVATORS = 4;

    JFrame frame;
    JPanel gridPanel;

    Elevator[] elevators = new Elevator[ELEVATORS];
    JLabel[][] elevatorCells = new JLabel[FLOORS][ELEVATORS];

    java.util.List<Request> pendingRequests = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ElevatorSimulation().start());
    }

    void start() {

        for (int i = 0; i < ELEVATORS; i++) {
            elevators[i] = new Elevator(i, 0, 8);
        }

        frame = new JFrame("Elevator Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);

        gridPanel = new JPanel(new GridLayout(FLOORS, ELEVATORS + 2));

        for (int floor = FLOORS - 1; floor >= 0; floor--) {

            int f = floor;

            JButton up = new JButton("↑");
            JButton down = new JButton("↓");

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

        frame.add(gridPanel);
        frame.setVisible(true);

        Timer timer = new Timer(1000, (e)->update());
        timer.start();
    }

    void requestElevator(int floor, Direction dir) {
        pendingRequests.add(new Request(floor, dir));
    }

    void update() {

        assignRequests();

        for (Elevator e : elevators) {
            e.move();
        }

        refreshGUI();
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
                    elevatorCells[f][e].setText("E" + e);
                }
            }
        }
    }

}
