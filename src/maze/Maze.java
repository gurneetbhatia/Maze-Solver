package maze;
import java.util.*;
import java.io.*;

public class Maze implements java.io.Serializable{

    public enum Direction
    {
        NORTH, SOUTH, EAST, WEST;
    }

    public static class Coordinate {
        private int x, y;
        
        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() {
            return this.x;
        }
        
        public int getY() {
            return this.y;
        }
        
        public String toString() {
            return "("+this.x+", "+this.y+")";
        }
    }
    
    private Tile entrance;
    private Tile exit;
    private List<List<Tile>> tiles;
    
    private Maze() {
        this.tiles = new ArrayList<List<Tile>>();
    }
    
    public static Maze fromTxt(String filename) throws InvalidMazeException {
        Maze maze = new Maze();
        try {
            FileReader fr = new FileReader(filename);
            int i;
            List<Tile> currentRow = new ArrayList<Tile>();
            boolean mazeValid = true;
            while((i=fr.read()) != -1) {
                char character = (char) i;
                if(!(character=='e' || character=='x' || character=='#' || character=='.' || character=='\n')) {
                    throw new InvalidMazeException("Maze file contains invalid character!");
                }
                if(character == '\n') {
                    maze.tiles.add(currentRow);
                    currentRow = new ArrayList<Tile>();
                } else {
                    Tile tile = Tile.fromChar(character);
                    if(character == 'e') {
                        try {
                            maze.setEntrance(tile);
                        } catch(MultipleEntranceException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if(character == 'x') {
                        try {
                            maze.setExit(tile);
                        } catch(MultipleExitException ex) {
                            ex.printStackTrace();
                        }
                    }
                    currentRow.add(tile);
                }
            }
            if(currentRow.size() > 0)
                maze.tiles.add(currentRow);
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        if(!(maze.tiles.stream().allMatch(l -> l.size() == maze.tiles.get(0).size()))) {
            throw new RaggedMazeException("Number of tiles in each row do not match! ");
        } else if(maze.getEntrance() == null) {
            throw new NoEntranceException("No Entrance found in Maze!");
        } else if(maze.getExit() == null) {
            throw new NoExitException("No Exit found in Maze!");
        }
        
        return maze;
    }
    
    public Tile getAdjacentTile(Tile tile, Direction direction) {
        Coordinate coords = getTileLocation(tile);
        Tile adjacentTile;
        switch(direction) {
            case NORTH:
                adjacentTile = coords.getY() < tiles.size() - 1? getTileAtLocation(new Coordinate(coords.getX(), coords.getY()+1)) : null;
                break;
            case SOUTH:
                adjacentTile = coords.getY() > 0? getTileAtLocation(new Coordinate(coords.getX(), coords.getY()-1)) : null;
                break;
            case EAST:
                adjacentTile = coords.getX() < tiles.get(0).size() - 1? getTileAtLocation(new Coordinate(coords.getX()+1, coords.getY())) : null;
                break;
            case WEST:
                adjacentTile = coords.getX() > 0? getTileAtLocation(new Coordinate(coords.getX()-1, coords.getY())): null;
                break;
            default:
                adjacentTile = null;
        }
        return adjacentTile;
    }
    
    public Tile getEntrance() {
        return this.entrance;
    }
    
    public Tile getExit() {
        return this.exit;
    }
    
    public Tile getTileAtLocation(Coordinate coord) {
        // (0, 0) is the bottom-left of the maze
        int numOfRows = tiles.size() - 1;
        int coordY = numOfRows - coord.getY();
        return tiles.get(coordY).get(coord.getX());
    }
    
    public Coordinate getTileLocation(Tile tile) {
        int row, col = 0;
        boolean found = false;
        for(row=0;row<tiles.size();row++) {
            for(col=0;col<tiles.get(0).size();col++) {
                if(tiles.get(row).get(col).equals(tile)) {
                    found = true;
                    break;
                }
            }
            if(found)
                break;
        }

        int numOfRows = tiles.size() - 1;
        row = numOfRows - row;
        
        return found ? new Coordinate(col, row) : null;
    }
    
    public List<List<Tile>> getTiles() {
        return tiles;
    }
    
    private void setEntrance(Tile tile) throws MultipleEntranceException, IllegalAccessException {
        if(tile == null) {
            throw new IllegalArgumentException("Illegal Access to Entrance Detected!");
        }
        else if(this.getEntrance() == null) {
            this.entrance = tile;
        } else if(getTileLocation(tile) == null){
            throw new IllegalArgumentException("Illegal Access to Entrance Detected!");
        } else {
            throw new MultipleEntranceException("Multiple entrances found in Maze!");
        }
    }
    
    private void setExit(Tile tile) throws MultipleExitException {
        if(this.getExit() == null) {
            this.exit = tile;
        } else {
            throw new MultipleExitException("Multiple exits found in Maze!");
        }
    }
    
    public String toString() {
        String output = "";
        for(List<Tile> row: tiles) {
            for(Tile tile: row) {
                output += tile.toString();
            }
            output += "\n";
        }
        return output;
    }
}