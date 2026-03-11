package logic;
import java.util.*;
public class Elevator {

    int id;
    public int currentFloor;
    public Direction direction = Direction.IDLE;

    public int capacity = 0;
    public int maxCapacity;

    public TreeSet<Integer> upStops = new TreeSet<>();
    public TreeSet<Integer> downStops = new TreeSet<>(Collections.reverseOrder());

    public Elevator(int id, int startFloor, int maxCapacity) {
        this.id = id;
        this.currentFloor = startFloor;
        this.maxCapacity = maxCapacity;
    }

    public boolean isFull() {
        return capacity >= maxCapacity;
    }

    public void addStop(int floor) {

        if (floor > currentFloor)
            upStops.add(floor);
        else if (floor < currentFloor)
            downStops.add(floor);

        if (direction == Direction.IDLE) {
            direction = (floor > currentFloor) ? Direction.UP : Direction.DOWN;
        }
    }

    public void move() {

        if (direction == Direction.UP) {

            currentFloor++;

            if (upStops.contains(currentFloor)) {
                stop();
            }

            if (upStops.isEmpty()) {
                direction = downStops.isEmpty() ? Direction.IDLE : Direction.DOWN;
            }

        } else if (direction == Direction.DOWN) {

            currentFloor--;

            if (downStops.contains(currentFloor)) {
                stop();
            }

            if (downStops.isEmpty()) {
                direction = upStops.isEmpty() ? Direction.IDLE : Direction.UP;
            }
        }
    }

    public void stop() {

        upStops.remove(currentFloor);
        downStops.remove(currentFloor);

        if (capacity > 0)
            capacity--;
    }
}

