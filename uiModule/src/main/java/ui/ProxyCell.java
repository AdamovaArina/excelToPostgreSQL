package ui;

import beans.TableCellType;

public class ProxyCell {
    private int x;
    private int y;
    private int width;
    private int height;
    private int widthCount;
    private int heightCount;
    private String value;
    private TableCellType type;
    private String colour = "white";

    public ProxyCell(int x, int y, int width, int height, int widthCount, int heightCount, String value, TableCellType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widthCount = widthCount;
        this.heightCount = heightCount;
        this.value = value;
        this.type = type;
    }

    public String getValue(){
        return value;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public int getWidthCount(){
        return widthCount;
    }

    public int getHeightCount(){
        return heightCount;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}
