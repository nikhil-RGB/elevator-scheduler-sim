package logic;
import java.util.*;
class Elevator {

    int id;
    int currentFloor;
    Direction direction = Direction.IDLE;

    int capacity = 0;
    int maxCapacity;

    TreeSet<Integer> upStops = new TreeSet<>();
    TreeSet<Integer> downStops = new TreeSet<>(Collections.reverseOrder());

    Elevator(int id, int startFloor, int maxCapacity) {
        this.id = id;
        this.currentFloor = startFloor;
        this.maxCapacity = maxCapacity;
    }

    boolean isFull() {
        return capacity >= maxCapacity;
    }

    void addStop(int floor) {

        if (floor > currentFloor)
            upStops.add(floor);
        else if (floor < currentFloor)
            downStops.add(floor);

        if (direction == Direction.IDLE) {
            direction = (floor > currentFloor) ? Direction.UP : Direction.DOWN;
        }
    }

    void move() {

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

    void stop() {

        upStops.remove(currentFloor);
        downStops.remove(currentFloor);

        if (capacity > 0)
            capacity--;
    }
}

