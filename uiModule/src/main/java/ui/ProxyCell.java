package ui;


import javafx.scene.paint.Color;

public class ProxyCell {
    private int x;
    private int y;

    private int xPix;
    private int yPix;
    private int width;
    private int height;
    private int widthCount;
    private int heightCount;
    private String value;
    private Color color = Color.WHITE;
    private int page = 1;

    public ProxyCell(int x, int y, int width, int height, int widthCount, int heightCount, String value) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widthCount = widthCount;
        this.heightCount = heightCount;
        this.value = value;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getXPix() {
        return xPix;
    }

    public void setXPix(int xPix) {
        this.xPix = xPix;
    }

    public int getYPix() {
        return yPix;
    }

    public void setYPix(int yPix) {
        this.yPix = yPix;
    }

    public boolean equals(ProxyCell other){
        return this.x == other.getX() && this.xPix == other.getXPix() && this.width == other.getWidth() &&
                this.widthCount == other.getWidthCount() && this.value.equals(other.getValue());
    }

    public boolean equalsValue(ProxyCell other){
        return this.value.equals(other.getValue()) && this.x == other.getX();
    }

}
