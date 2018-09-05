package com.longforus.apidebugger.ui;

import com.longforus.apidebugger.bean.TableBean;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class MyParamsTableModel extends AbstractTableModel {
    //单元格元素类型
    private Class[] cellType = { Boolean.class, String.class, String.class};
    //表头
    private String title[] = { "select", "key", "value" };
    //模拟数据
    //private Object data[][] = {
    //    { "1", new ImageIcon("e://image/3.jpg"), new Boolean(true), 0, new JButton("start1") }, {
    //    "2", new ImageIcon("e://image/1.jpg"), new Boolean(false), 60, new JButton("start2") }, {
    //    "3", new ImageIcon("e://image/4.png"), new Boolean(false), 25, new JButton("start3") } };

    private List<TableBean> data = new ArrayList<>();

    public MyParamsTableModel() {
    }

    public List<TableBean> getData() {
        return data;
    }

    public void setData(List<TableBean> data) {
        this.data.clear();
        this.data.addAll(data);
        fireTableDataChanged();
    }

    public void clear() {
        int size = this.data.size();
        this.data.clear();
        fireTableRowsDeleted(0,size);
    }

    public void addEmptyRow() {
        data.add(new TableBean(true, "", ""));
        fireTableRowsInserted(data.size()-1,data.size()-1);
    }


    public void removeRow(int row) {
        if (row > -1 && row < data.size()) {
            data.remove(row);
        }
        fireTableRowsDeleted(row, row);
    }

    @Override
    public Class<?> getColumnClass(int arg0) {
        return cellType[arg0];
    }

    @Override
    public String getColumnName(int arg0) {
        return title[arg0];
    }

    @Override
    public int getColumnCount() {
        return title.length;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex<data.size()) {
            switch (columnIndex) {
                case 0:
                    return data.get(rowIndex).getSelected();
                case 1:
                    return data.get(rowIndex).getKey();
                case 2:
                    return data.get(rowIndex).getValue();
            }
        }
        return null;
    }

    //重写isCellEditable方法

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    //重写setValueAt方法

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex<data.size()) {
            switch (columnIndex) {
                case 0:
                    data.get(rowIndex).setSelected((Boolean) aValue);
                    break;
                case 1:
                    data.get(rowIndex).setKey((String) aValue);
                    break;
                case 2:
                    data.get(rowIndex).setValue((String) aValue);
                    break;
            }
        }
        this.fireTableCellUpdated(rowIndex, columnIndex);
    }


}
