package com.longforus.apidebugger.ui;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 自定义树描述类,将树的每个节点设置成不同的图标
 *
 * @author RuiLin.Xie - xKF24276
 */
public class JsonTreeCellRenderer extends DefaultTreeCellRenderer {
    /**
     * ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 重写父类DefaultTreeCellRenderer的方法
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        //执行父类原型操作  
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        setText(value.toString());

        if (sel) {
            setForeground(getTextSelectionColor());
        } else {
            setForeground(getTextNonSelectionColor());
        }
        this.setIcon(null);
        return this;
    }
}  