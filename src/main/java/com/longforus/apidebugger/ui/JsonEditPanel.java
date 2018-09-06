package com.longforus.apidebugger.ui;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.longforus.apidebugger.MyValueHandler;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Implements the embeddable swing widget for editing JSON.
 *
 * This class is not thread safe.
 *
 * @author Stephen Owens
 *
 *
 * <p>
 * Copyright 2011 Stephen P. Owens : steve@doitnext.com
 * </p>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * </p>
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
@Deprecated
public class JsonEditPanel extends JPanel {
    /**
     * Using default serial version id.
     */
    private static final long serialVersionUID = 1L;

    // Default visibility to let classes in this module access this element directly.
    JTree jTree;
    private final JPopupMenu mPopupMenu;

    public enum UpdateType {
        REPLACE,
        INSERT,
        APPEND,
        AS_CHILD
    }

    ;

    public enum AllowedOps {
        REPLACE,
        INSERT,
        APPEND,
        AS_CHILD,
        DELETE,
        RENAME,
        GET_JSON
    }

    ;

    /**
     * Default constructor for the JsonEditPanel object.
     *
     * Creates an empty tree.
     */
    public JsonEditPanel() {
        setLayout(new BorderLayout());
        JsonJTreeNode root = new JsonJTreeNode(null, -1, new JsonNull());
        jTree = new JTree(root);
        jTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        mPopupMenu = new JPopupMenu();
        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(e -> {
            JsonJTreeNode component = (JsonJTreeNode) jTree.getLastSelectedPathComponent();
            String text;
            switch (component.dataType) {
                case OBJECT:
                case ARRAY:
                    text = component.asJsonElement().toString();
                    break;
                default:
                    text = component.toSSearchStr();
            }
            MyValueHandler.INSTANCE.setSysClipboardText(text);
    });
        mPopupMenu.add(copy);
        jTree.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    mPopupMenu.show(jTree, e.getX(), e.getY());
                }
            }
        });
        add(new JScrollPane(jTree), BorderLayout.CENTER);
    }

    /**
     * Allow the owner of the component to listen for tree selection events.
     *
     * @param tsl A handler for tree selection events
     */
    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        jTree.addTreeSelectionListener(tsl);
    }

    /**
     * Replaces the current tree structure in the contained JTree component
     * with a new structure built from the JSON string provided.
     *
     * @param json - the JSON to update the tree control with
     * @param updateType - if a node is selected when this method is called
     * then the updateType indicates where the new json goes:
     * REPLACE - replace current selected node with new JSON
     * INSERT - place node before selected node
     * APPEND - place node after selected node
     * AS_CHILD - append to end of child nodes or insert new child node if no
     * children present.  Selected node must be of type ARRAY or OBJECT
     */
    @SuppressWarnings("unchecked")
    public void setJson(String json, UpdateType updateType) {
        TreePath selection = jTree.getSelectionPath();
        if (selection == null) {
            if (updateType == UpdateType.REPLACE) {
                JsonElement root = new JsonParser().parse(json);
                JsonJTreeNode rootNode = new JsonJTreeNode(null, -1, root);
                jTree.setModel(new DefaultTreeModel(rootNode));
            } else {
                JOptionPane.showMessageDialog(this, "Only replace JSON and get JSON are supported when no node is selected.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JsonJTreeNode selectedNode = (JsonJTreeNode) selection.getLastPathComponent();
            JsonJTreeNode parent = (JsonJTreeNode) selectedNode.getParent();
            switch (updateType) {
                case REPLACE: {
                    if (parent == null) {
                        JsonElement root = new JsonParser().parse(json);
                        JsonJTreeNode rootNode = new JsonJTreeNode(null, -1, root);
                        jTree.setModel(new DefaultTreeModel(rootNode));
                        return;
                    }
                    JsonElement root = new JsonParser().parse(json);
                    JsonJTreeNode replacementNode = new JsonJTreeNode(selectedNode.fieldName, selectedNode.index, root);
                    int index = selectedNode.getParent().getIndex(selectedNode);
                    selectedNode.removeFromParent();
                    parent.insert(replacementNode, index);
                    ((DefaultTreeModel) jTree.getModel()).reload(parent);
                }
                break;
                case INSERT:
                case APPEND: {
                    if (parent == null) {
                        JOptionPane.showMessageDialog(this, "You cannot append to the root element.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    JsonElement root = new JsonParser().parse(json);
                    JsonJTreeNode replacementNode = new JsonJTreeNode(selectedNode.fieldName, selectedNode.index, root);
                    int index = selectedNode.getParent().getIndex(selectedNode);
                    if (updateType.equals(UpdateType.APPEND)) {
                        index++;
                    }
                    parent.insert(replacementNode, index);
                    ((DefaultTreeModel) jTree.getModel()).reload(parent);
                }
                break;
                case AS_CHILD: {
                    JsonElement root = new JsonParser().parse(json);
                    String fieldName = null;
                    int arrayIndex = -1;
                    if (selectedNode.dataType.equals(JsonJTreeNode.DataType.ARRAY)) {
                        Enumeration en = selectedNode.children();
                        int count = 0;
                        while (en.hasMoreElements()) {
                            en.nextElement();
                            count++;
                        }
                        arrayIndex = count;
                    } else if (selectedNode.dataType.equals(JsonJTreeNode.DataType.OBJECT)) {
                        fieldName = "new-field";
                    } else {
                        JOptionPane.showMessageDialog(this, "Vaue type entities can not have children.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    JsonJTreeNode newNode = new JsonJTreeNode(fieldName, arrayIndex, root);
                    selectedNode.add(newNode);
                    ((DefaultTreeModel) jTree.getModel()).reload(selectedNode);
                }
                break;
            }
        }
    }

    /**
     * Renames the selected node if it is a renameable node.
     */
    public void renameNode() {
        TreePath selection = jTree.getSelectionPath();
        if (selection == null) {
            JOptionPane.showMessageDialog(this, "No node is selected for rename.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JsonJTreeNode node = (JsonJTreeNode) selection.getLastPathComponent();
        JsonJTreeNode parent = (JsonJTreeNode) node.getParent();
        if (parent == null) {
            JOptionPane.showMessageDialog(this, "It is not possible to assign a name to the root node.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (!parent.dataType.equals(JsonJTreeNode.DataType.OBJECT)) {
            JOptionPane.showMessageDialog(this, "Only object fields may be renamed.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String newName = JOptionPane.showInputDialog("Enter new name for " + node.fieldName);
        node.fieldName = newName;
        ((DefaultTreeModel) jTree.getModel()).nodeChanged(node);
    }

    /**
     * Deletes selected node or sets entire model to null if root node or no node is selected.
     */
    public void deleteNode() {
        TreePath selection = jTree.getSelectionPath();
        if (selection == null) {
            // Replace root with emptyness
            jTree.setModel(new DefaultTreeModel(new JsonJTreeNode(null, -1, new JsonNull())));
        } else {
            JsonJTreeNode node = (JsonJTreeNode) selection.getLastPathComponent();
            JsonJTreeNode parent = (JsonJTreeNode) node.getParent();
            if (parent == null) {
                // Replace root with emptyness
                jTree.setModel(new DefaultTreeModel(new JsonJTreeNode(null, -1, new JsonNull())));
            } else {
                node.removeFromParent();
                ((DefaultTreeModel) jTree.getModel()).reload(parent);
            }
        }
    }

    /**
     * Returns the current JSON from the JTree
     *
     * @return the JSON as it is represented by the current state of the Tree model
     */
    public String getJson() {
        TreePath selection = jTree.getSelectionPath();
        JsonJTreeNode node = null;
        if (selection == null) {
            ((DefaultTreeModel) jTree.getModel()).reload();
            node = (JsonJTreeNode) jTree.getModel().getRoot();
        } else {
            ((DefaultTreeModel) jTree.getModel()).reload(node);
            node = (JsonJTreeNode) selection.getLastPathComponent();
        }
        if (node != null) {
            return node.asJsonElement().toString();
        } else {
            return null;
        }
    }

    /**
     * Determine what can currently be asked of the component in terms of tree modifiation operations.
     *
     * @return a list of AllowedOps enumerations to indicate the commands that are currently allowed upon the Component
     */
    public List<AllowedOps> getAllowedOperations() {
        List<AllowedOps> result = new ArrayList<AllowedOps>();
        result.add(AllowedOps.REPLACE);
        result.add(AllowedOps.GET_JSON);

        TreePath selection = jTree.getSelectionPath();
        if (selection == null) {
            return result;
        }

        JsonJTreeNode selectedNode = (JsonJTreeNode) selection.getLastPathComponent();
        JsonJTreeNode parentNode = null;

        if (selectedNode != null) {
            result.add(AllowedOps.DELETE);
            parentNode = (JsonJTreeNode) selectedNode.getParent();
        }
        if (parentNode != null) {
            result.add(AllowedOps.APPEND);
            result.add(AllowedOps.INSERT);
        }
        if (selectedNode.dataType.equals(JsonJTreeNode.DataType.ARRAY) || selectedNode.dataType.equals(JsonJTreeNode.DataType.OBJECT)) {
            result.add(AllowedOps.AS_CHILD);
        }
        if ((parentNode != null) && (parentNode.dataType.equals(JsonJTreeNode.DataType.OBJECT))) {
            result.add(AllowedOps.RENAME);
        }
        return result;
    }
}
