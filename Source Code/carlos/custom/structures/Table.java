package custom.structures;

import java.io.*;

public class Table { //it's a table like structure that I have created myself, it's not amazing, but it taught me a lot about OOP.


     private Column[] columns;
     private Cell[][] cells;
     private int columnNumber, rowNumber;

     public Table(){
        columns = new Column[0];
        rowNumber++;
     }

     public Table(String fileName) {
         columns = new Column[0];
         rowNumber++;
         load(fileName);
     }

     public int getColumnNumber(){
        return columnNumber;
     }

     public int getRowNumber(){
         return rowNumber;
     }


     public String[] getColumns(){ //returns column names
        String[] getColumns = new String[columnNumber];
        for(Column column : columns)
            getColumns[column.index] = column.name;
        return getColumns;
    }

    public float[][] getAllValues(){ //returns a 2D array of all of the values in the table
        return getValues(0, rowNumber, 0, columnNumber + 1 );
    }
    public float[][] extractRow(int rowIndex){ //returns a 2D array of values from a row (which in this case is not needed and could be converted to a 1D array
                                                //i kept it this way for consistency only.
        return getValues(rowIndex, rowIndex + 1, 0, columnNumber + 1);
    }
    public float[][] extractColumn(String columnName){ //returns  a 2D array of values from a column
         int columnIndex = indexOfColumn(columnName);
        return getValues(0, rowNumber, columnIndex, columnIndex + 1);
    }

    public float[][] getValues(int firstRow, int lastRow, int firstColumn, int lastColumn){ // returns values in between set rows and columns
         float[][] values = new float[lastRow - firstRow][lastColumn - firstColumn];
         int temp = firstColumn;
         for(int x = 0; firstRow < lastRow; firstRow++, x++){
             for(int y = 0; firstColumn < lastColumn;firstColumn++, y++)
                 values[x][y] = (cells[firstRow][firstColumn] != null) ? cells[firstRow][firstColumn].value : 0;
             firstColumn = temp;
         }
         return values;
    }

    public static float[] convert(float[][] array) { //converts a 2D array into 1D array.
         int size;
        if (array[0].length == 1) {
            size = array.length + array[0].length;
        } else {
            size = array.length * array[0].length;
        }
         float[] convertedArray = new float[size];
         int index = 0;
         for(float[] row : array){
             for(float value : row)
                 convertedArray[index++] = value;
         }
         return convertedArray;
    }


    public void addColumn(int number){ //adds a number of columns to the table
        while(number-- > 0) {
            var temp = new Column[columns.length + 1];
            for (Column column : columns)
                temp[column.index] = column;
            temp[columns.length] = new Column();
            columns = temp;
        }
    }
    public void sort() {                //sorts the table (this was my own take on sorting, I don't know much about sorting algorithms
         for(Column column : columns)   //I have watched it do the sorting by sleeping the thread after each array swap and it is not very efficient
             sort(column.name);         //but it was my first sorting algorithm I wrote without any help so I am happy with it, better than the bogo :D
    }
    public void sort(String columnName) {
         int columnIndex = indexOfColumn(columnName);
         sortColumn(columnIndex);
         for(int x = 1; x < rowNumber; x++) {
             if(cells[x][columnIndex] != null && cells[x][columnIndex].value < cells[x - 1][columnIndex].value)
                 sort(columnName);
        }
    }
    private void sortColumn(int columnIndex) {
        for(int x = 1; x < rowNumber; x++) {
            if(cells[x][columnIndex] != null && cells[x][columnIndex].value < cells[x - 1][columnIndex].value){
                for(int y = 0; y < rowNumber; y++){
                    if(cells[y][columnIndex] != null && y != rowNumber - 1 && cells[x][columnIndex].value >= cells[y + 1][columnIndex].value){
                        float temp = cells[x][columnIndex].value;
                        cells[x][columnIndex].value = cells[y][columnIndex].value;
                        cells[y][columnIndex].value = temp;
                    }
                }
            }
        }
    }

    public void addColumn(String name) { //add a column with a specific name
        var temp = new Column[columns.length + 1];
        for(Column column : columns)
            temp[column.index] = column;
        temp[columns.length] = new Column(name);
        columns = temp;
    }

    private void checkCells(){ //checks if cells are null (to avoid null pointer exceptions)
         for(Cell[] row : cells){
             for(Cell cell : row) {
                 if (cell != null && cell.value == -1f)
                     cell = null;
             }
         }
    }

    public void addCell(float value, String columnName) { //adds a cell to the table
        if (cells == null)
            cells = new Cell[rowNumber][columnNumber];
        var temp = copyRowsToTemp(columnName);
        if (temp == null) {
            addCellToExistingRow(value, columnName);
        }
        else
            addCellToNewRow(value, columnName, temp);
        checkCells();
    }

    public void removeCell(int rowIndex, int columnIndex) throws IndexOutOfBoundsException{ //removes a cell from the table
            cells[rowIndex][columnIndex] = null;
            int count = 0;
            for(int i = 0; i < columnNumber; i++)
                count += (cells[rowNumber - 1][i] == null) ? 1 : 0;
            if(count == columnNumber){
                var temp = new Cell[rowNumber - 1][columnNumber];
                for(int x = 0; x < rowNumber - 1; x++){
                    for(int y = 0; y < columnNumber; y++)
                        temp[x][y] = cells[x][y];
                }
                cells = temp;
            }
    }

    public void load(String fileName){ //loads a table from a CSV file
        try {
            var rowReader = new BufferedReader ( new FileReader(fileName + ".csv"));
            readRows(rowReader);
            var csvReader = new BufferedReader ( new FileReader(fileName + ".csv"));
            String[] columnNames = csvReader.readLine().split(",");
            for(String column : columnNames) {
                addColumn(column);
            }
            while(csvReader.ready()) {
                int i = 0;
                String[] row = csvReader.readLine().split(",");
                for(Column column : columns) {
                    addCell(Float.parseFloat(row[i++]), column.name);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readRows(BufferedReader rowReader) throws IOException { //finds out how many rows there are in a CSV file
        rowNumber = -1; // offset column row                             //useful to assign the proper size of the array so that the table doesn't have to be
        while(rowReader.ready()) {                                       //resized and copied every time a new row is added
            rowReader.readLine();
            rowNumber++;
        }
        rowReader.close();
    }

    public void generate(int nRows, int nColumns, int multiplier){ //generates a bunch of data for the table (I have used this to test sorting)
        addColumn(nColumns);
        for(int i = 1; i <= nRows; i++){
            for(Column column : columns) {
                addCell((int) (Math.random()*(Math.random() * multiplier)), column.name);
            }
        }
    }

    public void save(String fileName){ //saves a table to a CSV file
        try{
            var csvWriter = new BufferedWriter (new FileWriter(fileName + ".csv"));
            for (Column column : columns) {
                csvWriter.append(column.name);
                if (column.index == columnNumber - 1)
                    csvWriter.append("\n");
                else
                    csvWriter.append(",");
            }
            for(int i = 0; i < cells.length; i++) {
                float[] values = Table.convert(extractRow(i));
                for(int j = 0; j < values.length; j++) {
                    if(j == values.length - 1)
                        csvWriter.append("\n");
                    else
                        csvWriter.append(String.valueOf(values[j]));
                }
            }
            csvWriter.flush();
            csvWriter.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public void print(){ //prints the table to the console
        for(Column column : columns)
            System.out.printf("%-12s", column.name);
        System.out.println();
        for(Cell[] row : cells){
            for(Cell cell : row) {
                if (cell != null)
                    System.out.printf("%-12.2f", cell.value);
                else System.out.printf("%-12s", "null");
            }
            System.out.println();
        }
    }

    private boolean cellInLastRowIsOccupied(String columnName){
         return cells[rowNumber - 1][indexOfColumn(columnName)] != null;
    }

     private Cell[][] copyRowsToTemp (String columnName){ //a method which copies the data from the original Cell array to a larger temporary array.
        var temp = (cellInLastRowIsOccupied(columnName)) ? new Cell[++rowNumber][columnNumber] : null;
        if (temp == null) return null;
        int rowIndex = 0;
        for (Cell[] row : cells) {
            int columnIndex = 0;
            for (Cell cell : row) {
                temp[rowIndex][columnIndex++] = cell;
            }
            rowIndex++;
        }
        return temp;
    }

     private int indexOfColumn(String name){ //returns the index of a column
        for (Column column : columns) {
            if (column.name.equals(name))
                return column.index;
        }
        return columns[0].index;
    }

     private void addCellToNewRow(float value, String columnName, Cell[][] temp){ // this method and the one below affect whether a temporary Cell array must be constructed
        int rowIndex = 0;
        int columnIndex = indexOfColumn(columnName);
        for (Cell[] row : temp) {
            if (row[columnIndex] == null) {
                temp[rowIndex][columnIndex] = new Cell(rowIndex, columnIndex, value);
                cells = temp;
                return;
            }
            rowIndex++;
        }
    }

     private void addCellToExistingRow(String columnName) {
         int rowIndex = 0;
         int columnIndex = indexOfColumn(columnName);
         for (Cell[] row : cells) {
             if (row[columnIndex] == null ) {
                 cells[rowIndex][columnIndex] = new Cell();
                 return;
             }
             rowIndex++;
         }
     }


     private void addCellToExistingRow(float value, String columnName){
        int rowIndex = 0;
        int columnIndex = indexOfColumn(columnName);
        for (Cell[] row : cells) {
            if (row[columnIndex] == null ) {
                cells[rowIndex][columnIndex] = new Cell(rowIndex, columnIndex, value);
                return;
            }
            rowIndex++;
        }
    }



     private class Column{  //these two are simple classes which define sub structures of the table

         String name;

         final int index;

         Column(String name){
             this.name = name;
             index = columnNumber++;
         }

         Column(){
             name = "Column" + (columnNumber + 1);
             index = columnNumber++;
         }
     }

      static class Cell {

         float value;

         int rowIndex, columnIndex;

         Cell(int rowIndex, int columnIndex, float value){
             this.rowIndex = rowIndex;
             this.columnIndex = columnIndex;
             this.value = value;
         }

         Cell(){}

          void getInfo(){ //retrieves info about a certain cell
              System.out.println("value: " + value);
              System.out.println("position: " + rowIndex + ", " + columnIndex);
         }
     }
}
