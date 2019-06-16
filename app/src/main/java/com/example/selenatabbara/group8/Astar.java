package com.example.selenatabbara.group8;

import android.os.Build;
import android.support.annotation.RequiresApi;


import android.util.Log;
import android.widget.TextView;

import com.example.selenatabbara.group8.Cell;

import java.util.PriorityQueue;




// work on inputs start and
//  shared pref to convert the end point  to actual coordinates end point

public class Astar {
    // costs for diagonal and vertical / horizontal moves based on pythagoras theorem
    // diagonal = 14, horizontal = 10

    public static final int DIAGONAL_COST = 14;
    public static final int V_H_COST = 10;
    int alpha = 90;//constant difference between mynorth and true north TBD;
    int angle = 0;

    //Cells of the grid
    private Cell[] []  grid;

    //define priority queue for open cells
    //Open Cells: the set of nodes to be evaluated
    //put cells with lowest cost in first
    private PriorityQueue<Cell> openCells;
    //Closed Cells: the set of nodes already evaluated
    private boolean[][] closedCells;
    int[] out = new int[]{0,0};        // Current Location
    int X1, Y1; // this will be the value passed on by the main activity.
    // Target Cell
    int X2, Y2;

//    public void setuserlocation (int[] location){
//        this.X1=location[0];
//        this.Y1=location[1];
//    }
//
//    public void setdestination (int[] location){
//        this.X2=location[0];
//        this.Y2=location[1];
//    }

    public int[] output () {
        return out;
    }

    @RequiresApi(api = Build.VERSION_CODES.N) //ini baru
    //blocks are obstacles
    public Astar(int width, int height, int x1, int y1, int x2, int y2){
//        Log.e("Astar", "x1: "+x1+" y1 :"+y1 + "x2: "+x2+" y2 :"+y2);
        this.X1=x1;
        this.Y1=y1;
        this.X2=x2;
        this.Y2=y2;

        grid = new Cell[width][height];
        closedCells = new boolean[width][height];
        openCells = new PriorityQueue<Cell>((Cell c1, Cell c2)->{
            return c1.finalcost <c2.finalcost ? -1: c1.finalcost> c2.finalcost ? 1:0;
        });
        //Cari tau lagi tentange Lamba knp ga bs
        startCell(X1,Y1);
        endCell(X2,Y2);
        //init Heuristics and cells
        for(int i = 0; i<grid.length;i++){
            for(int j = 0; j<grid[i].length;j++){
                grid[i][j] = new Cell(i,j);
                grid[i][j].heurcost = Math.abs(i-X2) + Math.abs(j-Y2);
                grid[i][j].solution = false;

            }
        }

        grid[X1][Y1].finalcost = 0;
        int[][] blocks = new int[][] {
                {42,13},{42,12},{42,11},{42,10},{42,9},{42,8},{42,7},{42,6},{42,5},{42,4},{42,3},{42,2},{42,1},{42,0},
                {41,13},{41,12},{41,11},{41,10},{41,9},{41,8},{41,7},{41,6},{41,5},{41,4},{41,3},{41,2},{41,1},{41,0},
                {40,13},{40,12},{40,11},{40,10},{40,9},{40,8},{40,7},{40,6},{40,5},{40,4},{40,3},{40,2},{40,1},{40,0},
                {39,13},{39,12},{39,11},{39,10},{39,9},{39,8},{39,7},{39,6},{39,5},{39,4},{39,3},{39,2},{39,1},{39,0},
                {38,13},{38,12},{38,11},{38,10},{38,9},{38,8},{38,7},{38,6},{38,5},{38,4},{38,3},{38,2},{38,1},{38,0},

                {37,5},{37,4},{37,3},{37,2},{37,1},{37,0},
                {36,5},{36,4},{36,3},{36,2},{36,1},{36,0},
                {35,5},{35,4},{35,3},{35,2},{35,1},{35,0},
                {34,5},{34,4},{34,3},{34,2},{34,1},{34,0},

                {33,12},{33,11},{33,10},{33,9},{33,8},{33,7},{33,6},{33,5},{33,4},{33,3},{33,2},{33,1},{33,0},
                {32,12},{32,11},{32,10},{32,9},{32,8},{32,7},{32,6},{32,5},{32,4},{32,3},{32,2},{32,1},{32,0},
                {31,12},{31,11},{31,10},{31,9},{31,8},{31,7},{31,6},{31,5},{31,4},{31,3},{31,2},{31,1},{31,0},
                {30,12},{30,11},{30,10},{30,9},{30,8},{30,7},{30,6},{30,5},{30,4},{30,3},{30,2},{30,1},{30,0},
                {29,12},{29,11},{29,10},{29,9},{29,8},{29,7},{29,6},{29,5},{29,4},{29,3},{29,2},{29,1},{29,0},
                {28,12},{28,11},{28,10},{28,9},{28,8},{28,7},{28,6},{28,5},{28,4},{28,3},{28,2},{28,1},{28,0},

                {27,6},{27,5},{27,4},{27,3},{27,2},{27,1},{27,0},
                {26,6},{26,5},{26,4},{26,3},{26,2},{26,1},{26,0},
                {25,6},{25,5},{25,4},{25,3},{25,2},{25,1},{25,0},
                {24,6},{24,5},{24,4},{24,3},{24,2},{24,1},{24,0},
                {23,6},{23,5},{23,4},{23,3},{23,2},{23,1},{23,0},
                {22,6},{22,5},{22,4},{22,3},{22,2},{22,1},{22,0},
                {21,6},{21,5},{21,4},{21,3},{21,2},{21,1},{21,0},
                {20,6},{20,5},{20,4},{20,3},{20,2},{20,1},{20,0},

                {19,12},{19,11},{19,10},{19,9},{19,8},{19,7},{19,6},{19,5},{19,4},{19,3},{19,2},{19,1},{19,0},
                {18,12},{18,11},{18,10},{18,9},{18,8},{18,7},{18,6},{18,5},{18,4},{18,3},{18,2},{18,1},{18,0},
                {17,12},{17,11},{17,10},{17,9},{17,8},{17,7},{17,6},{17,5},{17,4},{17,3},{17,2},{17,1},{17,0},
                {16,12},{16,11},{16,10},{16,9},{16,8},{16,7},{16,6},{16,5},{16,4},{16,3},{16,2},{16,1},{16,0},
                {15,12},{15,11},{15,10},{15,9},{15,8},{15,7},{15,6},{15,5},{15,4},{15,3},{15,2},{15,1},{15,0},
                {14,12},{14,11},{14,10},{14,9},{14,8},{14,7},{14,6},{14,5},{14,4},{14,3},{14,2},{14,1},{14,0},

                {13,10},{13,9},{13,8},{13,7},{13,6},{13,5},{13,4},{13,3},{13,2},{13,1},{13,0},
                {12,10},{12,9},{12,8},{12,7},{12,6},{12,5},{12,4},{12,3},{12,2},{12,1},{12,0},
                {11,10},{11,9},{11,8},{11,7},{11,6},{11,5},{11,4},{11,3},{11,2},{11,1},{11,0},

                {10,13},{10,12},{10,11},{10,10},{10,9},{10,8},{10,7},{10,6},{10,5},{10,4},{10,3},{10,2},{10,1},{10,0},
                {9,13},{9,12},{9,11},{9,10},{9,9},{9,8},{9,7},{9,6},{9,5},{9,4},{9,3},{9,2},{9,1},{9,0},

                {7,4},{7,3},{7,2},{7,1},{7,0},
                {6,4},{6,3},{6,2},{6,1},{6,0},
                {5,4},{5,3},{5,2},{5,1},{5,0},
                {4,4},{4,3},{4,2},{4,1},{4,0},
                {3,4},{3,3},{3,2},{3,1},{3,0},
                {2,4},{2,3},{2,2},{2,1},{2,0},
                {1,4},{1,3},{1,2},{1,1},{1,0},
                {0,4},{0,3},{0,2},{0,1},{0,0},
        };
        //put blocks on the grid
        for(int i = 0; i<blocks.length;i++){
            addBlockOnCell(blocks[i][0],blocks[i][1]);
        }

    }
//    public static void main(String[] args){
//        //x1 = row = Real Y
//        //y1 = column = Real X
//        //same with X2 and Y2 & it is zero indexing remember
//
//        // x1 y1 is for the start and x2, y2 is for the end
//        //just add set x1 and set x2;
//        Astar aStar = new Astar(43,18,17,17,4,8);
//        aStar.run();
//
//    }

    @RequiresApi(api = Build.VERSION_CODES.N)
        public int  run(){
            checkposition();
            display();
            process();//Apply a* algorithm
            displayScores();//display scores on grid
            displaySolution();//display solution path
            return angle;

        }

    public void addBlockOnCell(int i, int j){
        grid[i][j] = null;
    }

    public void endCell(int i, int j) {

        X2 = i;
        Y2 = j;

    }

    public void startCell(int i, int j) {
        X1 = i;
        Y1 = j;
    }

    public void updateCostIfNeeded(Cell current, Cell t, int cost){
        if(t == null || closedCells[t.i][t.j])
            return;
        int tFinalCost = t.heurcost + cost;
        boolean isOpen = openCells.contains(t);

        if(!isOpen || tFinalCost<t.finalcost){
            t.finalcost = tFinalCost;
            t.parent = current;

            if(!isOpen)
                openCells.add(t);
        }
    }

    public void process(){
        //add the start location to open list
        openCells.add(grid[X1][Y1]);
        Cell current;

        while(true){
            current = openCells.poll();

            if(current ==null)
                break;

            closedCells[current.i][current.j] = true;

            if(current.equals(grid[X2][Y2]))
                return;

            Cell t;

            if(current.i-1>=0){
                t = grid[current.i -1][current.j];
                updateCostIfNeeded(current,t,current.finalcost + V_H_COST);

                if(current.j -1>=0){
                    t = grid[current.i -1][current.j-1];
                    updateCostIfNeeded(current,t,current.finalcost + DIAGONAL_COST);
                }

                if(current.j +1<grid[0].length){
                    t = grid[current.i -1][current.j+1];
                    updateCostIfNeeded(current,t,current.finalcost + DIAGONAL_COST);
                }
            }

            if(current.j-1 >=0){
                t = grid[current.i][current.j-1];
                updateCostIfNeeded(current,t,current.finalcost + V_H_COST);
            }
            if(current.j +1<grid[0].length){
                t = grid[current.i][current.j+1];
                updateCostIfNeeded(current,t,current.finalcost + V_H_COST);
            }
            if(current.i +1<grid.length){
                t = grid[current.i+1][current.j];
                updateCostIfNeeded(current,t,current.finalcost + V_H_COST);

                if(current.j -1>=0){
                    t = grid[current.i+1][current.j-1];
                    updateCostIfNeeded(current,t,current.finalcost +DIAGONAL_COST);
                }

                if(current.j +1<grid[0].length){
                    t = grid[current.i+1][current.j+1];
                    updateCostIfNeeded(current,t,current.finalcost +DIAGONAL_COST);
                }
            }

        }
    }

    public void display(){
        System.out.println("Grid:");

        for(int i = 0; i<grid.length;i++){
            for(int j=0;j<grid[i].length;j++){
                if(i==X1 && j==Y1)
                    System.out.print("S   ");//source cell
                else if ( i ==X2 && j ==Y2)
                    System.out.print("E   ");//destination cell
                else if (grid[i][j] !=null)
                    System.out.printf("%-3d ", 0);
                else
                    System.out.print("W   "); //block cell
            }

            System.out.println();
        }

        System.out.println();
    }

    public void displayScores(){
        System.out.println("\nScores for cells :");

        for(int i = 0;i<grid.length;i++){
            for(int j=0;j<grid[i].length;j++){
                if(grid[i][j]!=null)
                    System.out.printf("%-3d ",grid[i][j].finalcost);
                else
                    System.out.print("W   ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void displaySolution() {
        if (closedCells[X2][Y2]) {
            //We track back the path
            System.out.println("Path: ");
            Cell current = grid[X2][Y2];
            System.out.println(current);
            grid[current.i][current.j].solution = true;
            // System.out.println(current.getClass());
            while (current.parent != null) {
                int x, y, xx, yy;

                x = current.j;
                y = current.i;
                xx = current.parent.j;
                yy = current.parent.i;
                System.out.println("->" + current.parent);
                current = current.parent;

                //start
//                if (x == xx && yy == y - 1) {
//                    angle = 0;
//                } else if (x + 1 == xx && yy == y) {
//                    angle = 90;
//                } else if (x - 1 == xx && yy == y) {
//                    angle = 270;
//                } else if (x == xx && yy == y + 1) {
//                    angle = 180;
//                } else if (x + 1 == xx && yy == y - 1) {
//                    angle = 45;
//                } else if (x + 1 == xx && yy == y + 1) {
//                    angle = 135;
//                } else if (x - 1 == xx && yy == y - 1) {
//                    angle = 315;
//                } else if (x - 1 == xx && yy == y + 1) {
//                    angle = 215;
//                }
                if (x == xx && yy == y - 1) {
                    angle = 270;
                } else if (x + 1 == xx && yy == y) {
                    angle = 0;
                } else if (x - 1 == xx && yy == y) {
                    angle = 180;
                } else if (x == xx && yy == y + 1) {
                    angle = 90;
                } else if (x + 1 == xx && yy == y - 1) {
                    angle = 315;
                } else if (x + 1 == xx && yy == y + 1) {
                    angle = 45;
                } else if (x - 1 == xx && yy == y - 1) {
                    angle = 215;
                } else if (x - 1 == xx && yy == y + 1) {
                    angle = 135;
                }
                //applying offset;
                angle = angle - alpha;
                if (angle < 0) {
                    angle = angle + 360;
                }
                System.out.println("angle: " + angle);

            }
        }
    }

    public void checkposition (){
        if(X1<0) X1=0;
        if(X1>=grid.length) X1=grid.length-1;
        if(Y1<0) Y1=0;
        if(Y1>=grid[0].length) Y1=grid[0].length-1;
        if(grid[X1][Y1]==null){
            for (int i = 0;i<grid.length;i++){
                if (((X1-i)>=0)&&(grid[X1-i][Y1]!=null)){
                    X1=X1-i;
                    return;
                }
                if (((Y1-i)>=0)&&(grid[X1][Y1-i]!=null)){
                    Y1=Y1-i;
                    return;
                }
                if (((X1+i)<grid.length)&&(grid[X1+i][Y1]!=null)){
                    X1=X1+i;
                    return;
                }
                if (((Y1+i)<grid[i].length)&&(grid[X1][Y1+i]!=null)){
                    Y1=Y1+i;
                    return;
                }

            }
        }

    }

}


