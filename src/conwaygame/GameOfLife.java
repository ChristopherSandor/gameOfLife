package conwaygame;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 * Conway's Game of Life Class holds various methods that will
 * progress the state of the game's board through it's many iterations/generations.
 *
 * Rules 
 * Alive cells with 0-1 neighbors die of loneliness.
 * Alive cells with >=4 neighbors die of overpopulation.
 * Alive cells with 2-3 neighbors survive.
 * Dead cells with exactly 3 neighbors become alive by reproduction.

 * @author Seth Kelley 
 * @author Maxwell Goldberg
 */
public class GameOfLife {

    // Instance variables
    private static final boolean ALIVE = true;
    private static final boolean DEAD = false;

    private boolean[][] grid;    // The board has the current generation of cells
    private int totalAliveCells; // Total number of alive cells in the grid (board)

    /**
    * Default Constructor which creates a small 5x5 grid with five alive cells.
    * This variation does not exceed bounds and dies off after four iterations.
    */
    public GameOfLife() {
        grid = new boolean[5][5];
        totalAliveCells = 5;
        grid[1][1] = ALIVE;
        grid[1][3] = ALIVE;
        grid[2][2] = ALIVE;
        grid[3][2] = ALIVE;
        grid[3][3] = ALIVE;
    }

    /**
    * Constructor used that will take in values to create a grid with a given number
    * of alive cells
    * @param file is the input file with the initial game pattern formatted as follows:
    * An integer representing the number of grid rows, say r
    * An integer representing the number of grid columns, say c
    * Number of r lines, each containing c true or false values (true denotes an ALIVE cell)
    */
    public GameOfLife (String file) {
        StdIn.setFile(file);
        this.totalAliveCells = 0;

        int row = StdIn.readInt();
        int col = StdIn.readInt();
        grid = new boolean[row][col];

        for(int r = 0; r < row; r++){
            for(int c = 0; c < col; c++){
                if(StdIn.readBoolean() == true){
                    grid[r][c] = ALIVE; 
                    this.totalAliveCells++;
                } 
                else{
                    grid[r][c] = DEAD;
                }
            }
        }

    }

    /**
     * Returns grid
     * @return boolean[][] for current grid
     */
    public boolean[][] getGrid () {
        return grid;
    }
    
    /**
     * Returns totalAliveCells
     * @return int for total number of alive cells in grid
     */
    public int getTotalAliveCells () {
        return totalAliveCells;
    }

    /**
     * Returns the status of the cell at (row,col): ALIVE or DEAD
     * @param row row position of the cell
     * @param col column position of the cell
     * @return true or false value "ALIVE" or "DEAD" (state of the cell)
     */
    public boolean getCellState (int row, int col) {

        if(grid[row][col] == true){
            return ALIVE;
        } 
        else{
            return DEAD;
        }

    }

    /**
     * Returns true if there are any alive cells in the grid
     * @return true if there is at least one cell alive, otherwise returns false
     */
    public boolean isAlive () {

        for(int r = 0; r < grid.length; r++){
            for(int c = 0; c < grid[r].length; c++){
                if(grid[r][c] == true){
                    return true;
                } 
            }
        }
        
        return false; 
    }

    /**
     * Determines the number of alive cells around a given cell.
     * Each cell has 8 neighbor cells which are the cells that are 
     * horizontally, vertically, or diagonally adjacent.
     * 
     * @param col column position of the cell
     * @param row row position of the cell
     * @return neighboringCells, the number of alive cells (at most 8).
     */
    public int numOfAliveNeighbors (int row, int col) {
        int alive_neighbors = 0;
        int rows = grid.length;
        int cols = grid[0].length;
    
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                if (r == 0 && c == 0) {
                } 
                else {

                    int neighborRow = (row + r + rows) % rows; // Wrap around if out of bounds
                    int neighborCol = (col + c + cols) % cols; // Wrap around if out of bounds
        
                    // Check if the neighbor is alive and increment the counter if so
                    if (getCellState(neighborRow, neighborCol) == true) {
                        alive_neighbors++;
                    }
                }
            }
        }
        if(alive_neighbors > 8){
            return -1;
        }
        return alive_neighbors; 
    }

    /**
     * Creates a new grid with the next generation of the current grid using 
     * the rules for Conway's Game of Life.
     * 
     * @return boolean[][] of new grid (this is a new 2D array)
     */
    public boolean[][] computeNewGrid () {
        int row = grid.length;
        int col = grid[0].length;
        boolean[][] newGrid = new boolean[row][col];

        for(int new_row = 0; new_row < row; new_row++){
            for(int new_col = 0; new_col < col; new_col++){
                
                int cell_neighbors = numOfAliveNeighbors(new_row, new_col);

                boolean cell_status;

                switch(cell_neighbors){

                    case 2:

                        if(getCellState(new_row, new_col) == ALIVE){
                            cell_status = ALIVE;
                        }else{
                            cell_status = DEAD; 
                        }

                    break;

                    case 3:
                        cell_status = ALIVE;
                    break;

                    default:
                        cell_status = DEAD;
                    break;
                }
                
                newGrid[new_row][new_col] = cell_status;
            }
        }

        return newGrid;
    }

    /**
     * Updates the current grid (the grid instance variable) with the grid denoting
     * the next generation of cells computed by computeNewGrid().
     * 
     * Updates totalAliveCells instance variable
     */
    public void nextGeneration () {

        // Take care of grid:
        boolean[][] newGrid = computeNewGrid();
        this.grid = newGrid;

        // Updating the total Alive Cells:
        getTotalAliveCells();

    }

    /**
     * Updates the current grid with the grid computed after multiple (n) generations. 
     * @param n number of iterations that the grid will go through to compute a new grid
     */
    public void nextGeneration (int n) {

        for(int counter = 0; counter < n; counter++){
            nextGeneration();
        }

    }

    /**
     * Determines the number of separate cell communities in the grid
     * @return the number of communities in the grid, communities can be formed from edges
     */
    public int numOfCommunities() {

        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(grid.length, grid[0].length);


        int rows = grid.length;
        int cols = grid[0].length;

        for(int positionRow = 0; positionRow < rows; positionRow++){
            for(int positionCol = 0; positionCol < cols; positionCol++){

                if(grid[positionRow][positionCol] == true){

                
                    for (int r = -1; r < 2; r++) {
                        for (int c = -1; c < 2; c++) {
                            if (r == 0 && c == 0) {
                            } 
                            else {
            
                                int neighborRow = (positionRow + r + rows) % rows;
                                int neighborCol = (positionCol + c + cols) % cols; 
                    
                                
                                if(grid[neighborRow][neighborCol] == true){
                                    uf.union(positionRow, positionCol, neighborRow, neighborCol);
                                }
            
                            }
                        }
                    }

                }   

            }
        }
        

        ArrayList<Integer> roots = new ArrayList<>();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (grid[r][c]) {
                        int root = uf.find(r, c);
                        if (!roots.contains(root)) {
                            roots.add(root);
                        }
                    }
                }
            }
            
            return roots.size();
    }
}
