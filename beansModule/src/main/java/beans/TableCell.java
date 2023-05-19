package beans;

import java.io.IOException;
import java.util.ArrayList;

public class TableCell {
    private final String value;
    private TableCellType type;
    private final Integer colNum;

    public TableCell(Integer colNum, String value, TableCellType type){
        this.colNum = colNum;
        this.value = value;
        this.type = type;
    }

    public String getValue(){
        return value;
    }

    public TableCellType getType(){
        return type;
    }

    public void setType(TableCellType type){
        this.type = type;
    }

    private String dateToString() throws IOException {
        if(type != TableCellType.DATE){
            throw new IOException("Невозможно преобразовать тип " + getType() + " к типу DATE");
        }
        String value = getValue();
        String[] parts = value.split(" ");
        switch (parts[1]){
            case("Jan"): parts[1] = "01";
            case("Feb"): parts[1] = "02";
            case("Mar"): parts[1] = "03";
            case("Apr"): parts[1] = "04";
            case("May"): parts[1] = "05";
            case("Jun"): parts[1] = "06";
            case("Jul"): parts[1] = "07";
            case("Aug"): parts[1] = "08";
            case("Sep"): parts[1] = "09";
            case("Oct"): parts[1] = "10";
            case("Nov"): parts[1] = "11";
            case("Dec"): parts[1] = "12";
        }
        return "date '" + parts[5] + "-" + parts[1] + "-" +  parts[2] + "'";
    }

    private String numToString(){
        String buffer = "";
        char c = 160;
        buffer += c;
        String s = value.replaceAll(buffer, "");
        return s.replaceAll(",", ".");
    }

    public boolean isDate(){
        String[] parts = new String[0];
        ArrayList<Integer> partsNum = new ArrayList<>();
        if(value.contains(".")){
            parts = value.split("\\.");
        } else if(value.contains("-")){
            parts = value.split("-");
        } else if(value.contains(" ")){
            parts = value.split(" ");
        } else if(value.contains("/")){
            parts = value.split("/");
        }
        if(parts.length == 0){
            return false;
        }
        for(String part : parts){
            try {
                partsNum.add(Integer.parseInt(part));
            } catch (NumberFormatException ex){
                return false;
            }
        }
        return (partsNum.get(0) < 32 && partsNum.get(1) < 13 && partsNum.get(2) < 2100) ||
                (partsNum.get(0) < 2100 && partsNum.get(1) < 13 && partsNum.get(2) < 32);
    }
    public boolean isNumeric(){
        try{
            Long.parseLong(value);
        } catch (NumberFormatException ex){
            return false;
        }
        return true;
    }

    public String toString(){
        if(value == null){
            return "null";
        }
        if(type == TableCellType.STRING){
            return "'" + value + "'";
        } else if(type == TableCellType.NUMERIC){
            return numToString();
        } else{
            try {
                return dateToString();
            } catch (IOException ex){
                return value;
            }
        }
    }

    public int getColNum() {
        return colNum;
    }
}
