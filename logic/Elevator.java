package logic;
import java.awt.Color;
import java.util.*;
import gui.ElevatorSimulation;
public class Elevator {
	private static Random r=new Random();
    ElevatorSimulation controller;
    int id;
    public int currentFloor;
    public Direction direction = Direction.IDLE;

    public int capacity = 0;
    public int maxCapacity;

    public TreeSet<Integer> upStops = new TreeSet<>();
    public TreeSet<Integer> downStops = new TreeSet<>(Collections.reverseOrder());

    public Elevator(int id, int startFloor, int maxCapacity,ElevatorSimulation cont) {
        this.id = id;
        this.currentFloor = startFloor;
        this.maxCapacity = maxCapacity;
        this.controller=cont;
    }

    public boolean isFull() {
        return capacity >= maxCapacity;
    }

    public void addStop(int floor) {
       if(floor==currentFloor) {
    	   stop();
    	   return;
    	   }
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
    	//At each stop increase and decrease capacity from 1-3, create new random stops based on people who enter
       
        	this.controller.upButtons[currentFloor].setBackground(Color.GRAY);
        
       
        	this.controller.downButtons[currentFloor].setBackground(Color.GRAY);
       
        
        upStops.remove(currentFloor);
        downStops.remove(currentFloor);
        if(this.controller.isPassengerBehaviourEnabled) 
        {
        	
        	int entering=r.nextInt(3);
        	int exiting=r.nextInt(3);
        //Simulating passengers entering and exiting.
        if(capacity>exiting) 
        {
        	this.capacity=capacity-exiting;
        }	
        else if(capacity>0) 
        {
        	exiting=1;
        	--this.capacity;
        }
        else 
        {
        	exiting=0;
        }
        
        
        if(!((capacity+entering)>ElevatorSimulation.MAX_CAPACITY)) 
        {
        	this.capacity+=entering;
        }
        else if(capacity<ElevatorSimulation.MAX_CAPACITY) 
        {
        	++this.capacity;
        	entering=1;
        }
        else 
        {
        	entering=0;
        }
        
    	HashSet<Integer> newStops=new HashSet<>(0);
    	for(int i=0;i<entering;++i) 
    	{
    		int floor=0;
    		do
    		{floor=r.nextInt(ElevatorSimulation.FLOORS);}
    		while(floor==currentFloor);
    		newStops.add(floor);
    	}
    	for(int floor_stop:newStops) 
    	{
    		this.addStop(floor_stop);
    		//Adding additional stops here, simulated to be requested by entering passengers. Passengers may request the same floor
    	}
    	
    	controller.logArea.append(
    		    "Elevator " + id +
    		    " \n| floor " + currentFloor +
    		    " \n| exited: " + exiting +
    		    " \n| entered: " + entering +
    		    " \n| capacity: " + capacity +
    		    " \n| New Requests: "+newStops.toString()+
    		    "\n"
    		);

    		
        
        
        }
        else if (capacity > 0)
        {capacity--;
        controller.logArea.append(
    		    "Elevator " + id +
    		    " \n| floor " + currentFloor +
    		    " \n| exited: 1" +
    		    " \n| entered: 0"+
    		    " \n| capacity: " + capacity +
    		    "\n"
    		);

    		
        }
        else 
        {
        	controller.logArea.append(
        		    "Elevator " + id +
        		    " \n| floor " + currentFloor +
        		    " \n| exited: 0" +
        		    " \n| entered: 0"+
        		    " \n| capacity: " + capacity +
        		    "\n"
        		);
        }
     // auto scroll
		controller.logArea.setCaretPosition(
		    controller.logArea.getDocument().getLength()
		);
        
    }
}

