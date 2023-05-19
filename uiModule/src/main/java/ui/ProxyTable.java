package ui;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.TreeSet;

public class ProxyTable {
    private final ArrayList<ProxyCell> cells = new ArrayList<>();

    public ArrayList<ProxyCell> getCells(){
        return cells;
    }

    public void addCell(ProxyCell newCell){
        cells.add(newCell);
    }

    public void fillRowNums(TreeSet<Integer> rowNums){
        rowNums.clear();
        int curRowNum;
        boolean add = true;
        boolean emptyRow = true;
        for(int i = 0; i < cells.size(); i++){
            curRowNum = cells.get(i).getY();
            if(!cells.get(i).getValue().equals("")){
                emptyRow = false;
            }
            if(cells.get(i).getWidthCount() != 1 || cells.get(i).getValue().contains("Итого: ") ||
                cells.get(i).getValue().contains("Всего: ")){
                add = false;
            }
            if((i + 1) == cells.size() || cells.get(i + 1).getY() != curRowNum){
                if(!emptyRow){
                    if(add){
                        rowNums.add(curRowNum);
                    }
                }
                add = true;
                emptyRow = true;
            }
        }
    }

    public void paintAll(Color color, TreeSet<Integer> rowNums){
        for(ProxyCell cell : cells){
            if(rowNums.contains(cell.getY())){
                if(!cell.getColor().equals(color)){
                    cell.setColor(color);
                }
            }
        }
    }

    public int maxColNum(){
        int maxColNum = -1;
        for(ProxyCell cell : cells){
            if(cell.getX() > maxColNum){
                maxColNum = cell.getX();
            }
        }
        return maxColNum;
    }

    public void paintAll(Color color){
        for (ProxyCell cell : cells){
            if(cell.getColor() != color){
                cell.setColor(color);
            }
        }
    }

    /*public boolean isRowContainsUnitedItems(Integer y) {
        for(var pc : cells) {
            if(pc.getY() == y && (pc.getHeightCount() != 1 || pc.getWidthCount() != 1)){
                return true;
            }
        }
        return  false;
    }*/
}
