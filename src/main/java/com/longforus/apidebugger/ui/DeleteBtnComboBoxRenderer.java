package com.longforus.apidebugger.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;


@Deprecated
public class DeleteBtnComboBoxRenderer implements ListCellRenderer {

    private DefaultListCellRenderer defaultCellRenderer = new DefaultListCellRenderer();

    private Consumer<Object> onDelete;

    public DeleteBtnComboBoxRenderer(Consumer<Object> onDelete) {
        super();
        this.onDelete = onDelete;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel renderer = (JLabel) defaultCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(renderer,BorderLayout.WEST);
        JButton x = new JButton("X");
        x.setFocusable(true);
        x.setEnabled(true);
        x.requestFocus();
        //点击事件无法被响应
        x.addActionListener(e -> {
            if (onDelete!=null) {
                onDelete.accept(value);
            }
        });
        panel.add(x,BorderLayout.EAST);
        panel.setFocusable(true);
        panel.setEnabled(true);
        return panel;
    }
}