package com.longforus.apidebugger.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.longforus.apidebugger.MyValueHandler;
import com.longforus.apidebugger.OB;
import com.longforus.apidebugger.UIActionHandler;
import com.longforus.apidebugger.UILifecycleHandler;
import com.longforus.apidebugger.bean.ApiBean;
import com.longforus.apidebugger.encrypt.IEncryptHandler;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SizeRequirements;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;

/**
 * Created by XQ Yang on 8/30/2018  10:34 AM.
 * Description :
 */

public class MainPanel extends JFrame {
    private JComboBox mCbBaseUrl;
    private JButton mBtnSaveBaseUrl;
    private JComboBox mCbApiUrl;
    private JButton mBtnSend;
    private JComboBox mCbEncrypt;
    private JTextPane mTpResponse;
    private JTable mTbParame;
    private JTextPane mTpInfo;
    private JsonEditPanel mJep;
    private JButton mBtnSaveApi;
    private JButton mBtnNewApi;
    private JLabel lbStatus;
    private JPanel baseP;
    private JButton btnDelUrl;
    private JButton btnDelApi;
    private JComboBox mCbMethod;

    public JComboBox getCbMethod() {
        return mCbMethod;
    }

    public JTable getTbParame() {
        return mTbParame;
    }

    public JComboBox getCbEncrypt() {
        return mCbEncrypt;
    }

    public JsonEditPanel getJep() {
        return mJep;
    }

    public JLabel getLbStatus() {
        return lbStatus;
    }

    public JTextPane getTpResponse() {
        return mTpResponse;
    }

    public JTextPane getTpInfo() {
        return mTpInfo;
    }

    public JComboBox getCbBaseUrl() {
        return mCbBaseUrl;
    }

    public JComboBox getCbApiUrl() {
        return mCbApiUrl;
    }

    public int getSelectedEncryptID() {
        return MyValueHandler.INSTANCE.encryptIndex2Id(mCbEncrypt.getSelectedIndex());
    }

    public int getSelectedMethodType() {
        return MyValueHandler.INSTANCE.encryptIndex2Id(mCbEncrypt.getSelectedIndex());
    }

    public MainPanel(String title) throws HeadlessException {
        super(title);
        $$$setupUI$$$();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mCbMethod.setModel(new DefaultComboBoxModel(new String[] { "POST", "GET" }));
        setContentPane(baseP);
        setJMenuBar(UILifecycleHandler.INSTANCE.getMenuBar());
        mTbParame.setModel(new DefaultTableModel(new Object[] { "key", "value" }, MyValueHandler.PARAME_TABLE_ROW_COUNT));
        mBtnSaveBaseUrl.addActionListener(e -> UIActionHandler.INSTANCE.onSaveBaseUrl(mCbBaseUrl.getModel().getSelectedItem()));
        btnDelUrl.addActionListener(e -> UIActionHandler.INSTANCE.onDelBaseUrl(mCbBaseUrl.getModel().getSelectedItem()));
        btnDelApi.addActionListener(e -> UIActionHandler.INSTANCE.onDelApiUrl((ApiBean) mCbApiUrl.getModel().getSelectedItem()));
        mBtnSaveApi.addActionListener(e -> UIActionHandler.INSTANCE.onSaveApi(mCbApiUrl.getModel().getSelectedItem()));
        mCbApiUrl.addItemListener(e -> UIActionHandler.INSTANCE.onApiItemChanged(((ApiBean) e.getItem())));
        mBtnNewApi.addActionListener(e -> UIActionHandler.INSTANCE.onNewApi());
        mBtnSend.addActionListener(e -> UIActionHandler.INSTANCE.onSend());
        mCbMethod.addItemListener(e -> UIActionHandler.INSTANCE.onMethodChanged(mCbMethod.getSelectedIndex()));
        mCbEncrypt.addItemListener(e -> UIActionHandler.INSTANCE.onEncryptTypeChanged(((IEncryptHandler) e.getItem()).getTypeCode()));
        mTpResponse.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu menu = new JPopupMenu("Clear");
                    JMenuItem clear1 = menu.add(new JMenuItem("clear"));
                    clear1.addActionListener(e1 -> {
                        mJep.setJson("{}", JsonEditPanel.UpdateType.REPLACE);
                        mTpResponse.setText("");
                    });
                    menu.show(mTpResponse, e.getX(), e.getY());
                }
            }
        });
        mTpInfo.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu menu = new JPopupMenu("Clear");
                    JMenuItem clear1 = menu.add(new JMenuItem("clear"));
                    clear1.addActionListener(e1 -> mTpInfo.setText(""));
                    menu.show(mTpInfo, e.getX(), e.getY());
                }
            }
        });
        //支持自动换行
        mTpResponse.setEditorKit(new HTMLEditorKit() {
            @Override
            public ViewFactory getViewFactory() {

                return new HTMLFactory() {
                    public View create(Element e) {
                        View v = super.create(e);
                        if (v instanceof InlineView) {
                            return new InlineView(e) {
                                public int getBreakWeight(int axis, float pos, float len) {
                                    return GoodBreakWeight;
                                }

                                public View breakView(int axis, int p0, float pos, float len) {
                                    if (axis == View.X_AXIS) {
                                        checkPainter();
                                        int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
                                        if (p0 == getStartOffset() && p1 == getEndOffset()) {
                                            return this;
                                        }
                                        return createFragment(p0, p1);
                                    }
                                    return this;
                                }
                            };
                        } else if (v instanceof ParagraphView) {
                            return new ParagraphView(e) {
                                protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
                                    if (r == null) {
                                        r = new SizeRequirements();
                                    }
                                    float pref = layoutPool.getPreferredSpan(axis);
                                    float min = layoutPool.getMinimumSpan(axis);
                                    // Don't include insets, Box.getXXXSpan will include them.
                                    r.minimum = (int) min;
                                    r.preferred = Math.max(r.minimum, (int) pref);
                                    r.maximum = Integer.MAX_VALUE;
                                    r.alignment = 0.5f;
                                    return r;
                                }
                            };
                        }
                        return v;
                    }
                };
            }
        });

        mJep.jTree.setCellRenderer(new JsonTreeCellRenderer());
        pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - baseP.getPreferredSize().width / 2;
        int y = (int) screensize.getHeight() / 2 - baseP.getPreferredSize().height / 2 - 40;
        setLocation(x, y);
        setVisible(true);
    }

    private void createUIComponents() {
        mJep = new JsonEditPanel();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        OB.INSTANCE.onExit();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        baseP = new JPanel();
        baseP.setLayout(new FormLayout(
            "fill:d:noGrow,left:4dlu:noGrow,fill:300px:noGrow,left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:d:noGrow," +
                "left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:max(p;600px):grow",
            "center:d:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:200px:noGrow," +
                "top:4dlu:noGrow,center:max(p;600px):grow,center:max(d;4px):noGrow"));
        baseP.setName("Api debugger");
        baseP.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1), null));
        final JLabel label1 = new JLabel();
        label1.setText("Base Url:");
        CellConstraints cc = new CellConstraints();
        baseP.add(label1, cc.xy(1, 2));
        mCbBaseUrl = new JComboBox();
        mCbBaseUrl.setEditable(true);
        baseP.add(mCbBaseUrl, cc.xy(3, 2));
        mBtnSaveBaseUrl = new JButton();
        mBtnSaveBaseUrl.setText("Save");
        baseP.add(mBtnSaveBaseUrl, cc.xy(5, 2));
        final JScrollPane scrollPane1 = new JScrollPane();
        baseP.add(scrollPane1, cc.xyw(1, 8, 11, CellConstraints.FILL, CellConstraints.FILL));
        scrollPane1.setBorder(BorderFactory.createTitledBorder("Request Parameter"));
        mTbParame = new JTable();
        mTbParame.setRowHeight(25);
        scrollPane1.setViewportView(mTbParame);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setHorizontalScrollBarPolicy(31);
        baseP.add(scrollPane2, cc.xyw(1, 10, 11, CellConstraints.FILL, CellConstraints.FILL));
        scrollPane2.setBorder(BorderFactory.createTitledBorder("Response"));
        mTpResponse = new JTextPane();
        mTpResponse.setPreferredSize(new Dimension(500, 600));
        scrollPane2.setViewportView(mTpResponse);
        lbStatus = new JLabel();
        lbStatus.setText("Status:");
        baseP.add(lbStatus, cc.xyw(1, 11, 11));
        final JScrollPane scrollPane3 = new JScrollPane();
        baseP.add(scrollPane3, cc.xywh(13, 1, 1, 8, CellConstraints.FILL, CellConstraints.FILL));
        scrollPane3.setBorder(BorderFactory.createTitledBorder("Request Information"));
        mTpInfo = new JTextPane();
        scrollPane3.setViewportView(mTpInfo);
        baseP.add(mJep, cc.xywh(13, 9, 1, 2));
        mJep.setBorder(BorderFactory.createTitledBorder("Json Tree"));
        final JLabel label2 = new JLabel();
        label2.setText("Encryption:");
        baseP.add(label2, cc.xy(9, 2));
        mCbEncrypt = new JComboBox();
        baseP.add(mCbEncrypt, cc.xy(11, 2));
        btnDelUrl = new JButton();
        btnDelUrl.setText("Delete");
        baseP.add(btnDelUrl, cc.xy(7, 2));
        final JLabel label3 = new JLabel();
        label3.setText("Mthod:");
        baseP.add(label3, cc.xy(1, 4));
        mCbApiUrl = new JComboBox();
        mCbApiUrl.setEditable(true);
        baseP.add(mCbApiUrl, cc.xyw(3, 6, 7));
        mCbMethod = new JComboBox();
        baseP.add(mCbMethod, cc.xy(3, 4));
        final JLabel label4 = new JLabel();
        label4.setText("Api Url:");
        baseP.add(label4, cc.xy(1, 6));
        btnDelApi = new JButton();
        btnDelApi.setText("Delete");
        baseP.add(btnDelApi, cc.xy(11, 4));
        mBtnSaveApi = new JButton();
        mBtnSaveApi.setText("Save");
        baseP.add(mBtnSaveApi, cc.xy(7, 4));
        mBtnNewApi = new JButton();
        mBtnNewApi.setText("Copy New");
        baseP.add(mBtnNewApi, cc.xy(9, 4));
        mBtnSend = new JButton();
        mBtnSend.setText("Send");
        baseP.add(mBtnSend, cc.xy(11, 6));
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return baseP;
    }
}
