package ui;

import java.util.ArrayList;

public class ProxyTable {
    private final ArrayList<ProxyCell> cells = new ArrayList<>();

    public ArrayList<ProxyCell> getCells(){
        return cells;
    }

    public void addCell(ProxyCell newCell){
        cells.add(newCell);
    }
    public boolean isRowContainsUnitedItems(Integer y) {
        for(var pc : cells) {
            if(pc.getY() == y && (pc.getHeightCount() != 1 || pc.getWidthCount() != 1)){
                return true;
            }
        }
        return  false;
    }
}
