package com.example.selenatabbara.group8;


public class Cell {

    //Coordinates
    public int i,j;
    //parent cells for path
    public Cell parent;
    //Heuristic Cost of the current cell
    public int heurcost;
    //Final cost
    public int finalcost; // G + H = F
    //G Cost of hte path from start node to n
    //H cost of n to target


    public boolean solution;//if cell is part of the solution of the path
    public Cell(int i , int j){
        this.i = i;
        this.j = j;
    }

    @Override
    public String toString(){
        return "["+i+","+j+"]";
    }

}
