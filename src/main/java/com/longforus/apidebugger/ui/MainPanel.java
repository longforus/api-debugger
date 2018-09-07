package com.longforus.apidebugger.ui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.longforus.apidebugger.ExtFunKt;
import com.longforus.apidebugger.MyValueHandler;
import com.longforus.apidebugger.OB;
import com.longforus.apidebugger.UIActionHandler;
import com.longforus.apidebugger.UILifecycleHandler;
import com.longforus.apidebugger.bean.ApiBean;
import com.longforus.apidebugger.encrypt.IEncryptHandler;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserException;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.ProtocolService;
import com.teamdev.jxbrowser.chromium.URLResponse;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SizeRequirements;
import javax.swing.event.MouseInputAdapter;
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
    private ParamsTableModel mParamsTableModel;
    private JComboBox mCbBaseUrl;
    private JButton mBtnSaveBaseUrl;
    private JComboBox mCbApiUrl;
    private JButton mBtnSend;
    private JComboBox mCbEncrypt;
    private JTable mTbParams;
    private JTextPane mTpInfo;
    private JLabel lbStatus;
    private JPanel baseP;
    private JButton btnDelUrl;
    private JButton btnDelApi;
    private JComboBox mCbMethod;
    private JButton btnAddRow;
    private JButton btnDelRow;
    private JButton btnClear;
    private BrowserView mBrowserView;
    private JButton mbtnDp;
    private JTextField tvTestCount;
    private JButton mBtnStartTest;
    private JProgressBar mPb;
    private Browser mBrowser;

    public JProgressBar getPb() {
        return mPb;
    }

    public JTextField getTvTestCount() {
        return tvTestCount;
    }

    public JComboBox getCbMethod() {
        return mCbMethod;
    }

    public JTable getTbParams() {
        return mTbParams;
    }

    public ParamsTableModel getParamsTableModel() {
        return mParamsTableModel;
    }

    public JComboBox getCbEncrypt() {
        return mCbEncrypt;
    }

    public JLabel getLbStatus() {
        return lbStatus;
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
        return mCbMethod.getSelectedIndex();
    }

    public MainPanel(String title) throws HeadlessException {
        super(title);
        $$$setupUI$$$();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String filename = getClass().getResource("") + "send.png";
        try {
            ImageIcon icon = new ImageIcon(new URL(filename));
            setIconImage(icon.getImage());
            icon.setImage(icon.getImage().getScaledInstance(68, 68, Image.SCALE_SMOOTH));

            mBtnSend.setIcon(icon);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mCbMethod.setModel(new DefaultComboBoxModel(new String[] { "POST", "GET" }));
        //限制只能输入数字
        tvTestCount.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (keyChar < KeyEvent.VK_0 || keyChar > KeyEvent.VK_9) {
                    e.consume(); //关键，屏蔽掉非法输入
                }
            }
        });

        setContentPane(baseP);
        setJMenuBar(UILifecycleHandler.INSTANCE.getMenuBar());
        initEvent();
        //mCbApiUrl.setRenderer(new DeleteBtnComboBoxRenderer(o -> UIActionHandler.INSTANCE.onDelApiUrl((ApiBean) o)));
        //mCbBaseUrl.setRenderer(new DeleteBtnComboBoxRenderer(UIActionHandler.INSTANCE :: onDelBaseUrl));
        initTextPanel();
        initTable();
        pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - baseP.getPreferredSize().width / 2;
        int y = 0;
        setLocation(x, y);
        setVisible(true);
        browserInit();
        //Observable.create((ObservableOnSubscribe<String>) emitter -> emitter.onNext("{}")).delay(10, TimeUnit.MILLISECONDS).subscribe(
        //    s -> mBrowser.executeJavaScript("app.doc" + "._id || (document.location.href = document.location.pathname + \"#/new\", location.reload())"));

    }

    public void setJsonData(String jsonData) {
        JSValue app = mBrowser.executeJavaScriptAndReturnValue("app");
        JSValue write = app.asObject().getProperty("setData");
        write.asFunction().invoke(app.asObject(), jsonData);
    }

    private void browserInit() {
        BrowserContext browserContext = mBrowser.getContext();
        ProtocolService protocolService = browserContext.getProtocolService();
        protocolService.setProtocolHandler("jar", request -> {
            try {
                URLResponse response = new URLResponse();
                URL path = new URL(request.getURL());
                InputStream inputStream = path.openStream();
                DataInputStream stream = new DataInputStream(inputStream);
                byte[] data = new byte[stream.available()];
                stream.readFully(data);
                response.setData(data);
                String mimeType = getMimeType(path.toString());
                response.getHeaders().setHeader("Content-Type", mimeType);
                return response;
            } catch (Exception ignored) {
            }
            return null;
        });

        // Assume that we need to load a resource related to this class in the JAR file
        String url = getClass().getResource("") + "jsonView/index.html";
        mBrowser.loadURL(url);
    }

    private static String getMimeType(String path) {
        if (path.endsWith(".html")) {
            return "text/html";
        }
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        if (path.endsWith(".png")) {
            return "image/png";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (path.endsWith(".ico")) {
            return "image/x-icon";
        }
        return "text/html";
    }

    private void initEvent() {
        mBtnSaveBaseUrl.addActionListener(e -> UIActionHandler.INSTANCE.onSaveBaseUrl(mCbBaseUrl.getModel().getSelectedItem()));
        btnDelUrl.addActionListener(e -> UIActionHandler.INSTANCE.onDelBaseUrl(mCbBaseUrl.getModel().getSelectedItem()));
        btnDelApi.addActionListener(e -> UIActionHandler.INSTANCE.onDelApiUrl((ApiBean) mCbApiUrl.getModel().getSelectedItem()));
        mCbApiUrl.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (e.getItem() instanceof ApiBean) {
                    UIActionHandler.INSTANCE.onApiItemChanged(((ApiBean) e.getItem()));
                }
            }
        });
        mBtnSend.addActionListener(e -> UIActionHandler.INSTANCE.onSend());

        mCbMethod.addItemListener(e -> UIActionHandler.INSTANCE.onMethodChanged(mCbMethod.getSelectedIndex()));
        mCbEncrypt.addItemListener(e -> UIActionHandler.INSTANCE.onEncryptTypeChanged(((IEncryptHandler) e.getItem()).getTypeCode()));
        mbtnDp.addActionListener(e -> showDefaultParamsDialog());
        mBtnStartTest.addActionListener(e -> UIActionHandler.INSTANCE.onStartTest());
    }

    private void showDefaultParamsDialog() {
        if (MyValueHandler.INSTANCE.getCurProject() == null) {
            ExtFunKt.showErrorMsg("Please create the project first");
            return;
        }
        DefaultParamsDialog paramsDialog = new DefaultParamsDialog();
        paramsDialog.pack();
        paramsDialog.setLocation((getX() + getWidth() / 2) - (paramsDialog.getWidth() / 2), getY() + getHeight() / 2 - paramsDialog.getHeight() / 2);
        paramsDialog.setVisible(true);
    }

    private void initTextPanel() {
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
        HTMLEditorKit editorKit = new HTMLEditorKit() {
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
        };
        mTpInfo.setEditorKit(editorKit);
    }

    public void resetParamsTbModel() {
        mParamsTableModel = MainPanel.resetParamsTbModel(mTbParams);
    }

    public static ParamsTableModel resetParamsTbModel(JTable table) {
        ParamsTableModel model = new ParamsTableModel();
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        return model;
    }

    private void initTable() {
        mParamsTableModel = resetParamsTbModel(mTbParams);
        mTbParams.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    mParamsTableModel.removeRow(mTbParams.getSelectedRow());
                }
            }
        });
        btnAddRow.addActionListener(e -> {
            mParamsTableModel.addEmptyRow();
            mTbParams.requestFocus();
            int index = mParamsTableModel.getRowCount() - 1;
            mTbParams.setRowSelectionInterval(index, index);//最后一行获得焦点
            mTbParams.editCellAt(index, 1);
        });
        btnDelRow.addActionListener(e -> mParamsTableModel.removeRow(mTbParams.getSelectedRow()));
        btnClear.addActionListener(e -> UIActionHandler.INSTANCE.onClearParams());
    }

    private void createUIComponents() {
        try {
            mBrowser = new Browser();
        } catch (BrowserException e) {
            ExtFunKt.showErrorMsg("already been opened !!!");
        }
        mBrowserView = new BrowserView(mBrowser);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        OB.INSTANCE.onExit();
    }

    public String getCurApiUrl() {
        if (mCbApiUrl.getEditor().getItem() != null) {
            return mCbApiUrl.getEditor().getItem().toString();
        }
        return "";
    }

    public int getCurMethod() {
        return mCbMethod.getSelectedIndex();
    }

    public int getCurEncryptCode() {
        return mCbEncrypt.getSelectedIndex();
    }

    public String getCurBaseUrl() {
        return (String) mCbBaseUrl.getSelectedItem();
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
                "left:4dlu:noGrow,fill:d:noGrow,left:4dlu:noGrow,fill:600px:grow",
            "center:d:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:33px:noGrow,center:33px:noGrow,center:200px:noGrow,center:40px:noGrow,top:4dlu:noGrow,center:max(p;" +
                "600px):grow,center:max(d;4px):noGrow"));
        baseP.setName("Api debugger");
        baseP.setPreferredSize(new Dimension(1341, 980));
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
        baseP.add(scrollPane1, cc.xyw(1, 6, 11, CellConstraints.FILL, CellConstraints.FILL));
        scrollPane1.setBorder(BorderFactory.createTitledBorder("Request Parameter"));
        mTbParams = new JTable();
        mTbParams.setRowHeight(25);
        scrollPane1.setViewportView(mTbParams);
        lbStatus = new JLabel();
        lbStatus.setText("Status:");
        baseP.add(lbStatus, cc.xyw(1, 10, 11));
        final JScrollPane scrollPane2 = new JScrollPane();
        baseP.add(scrollPane2, cc.xywh(13, 1, 1, 7, CellConstraints.FILL, CellConstraints.FILL));
        scrollPane2.setBorder(BorderFactory.createTitledBorder("Request Information"));
        mTpInfo = new JTextPane();
        mTpInfo.setText("");
        scrollPane2.setViewportView(mTpInfo);
        mCbEncrypt = new JComboBox();
        baseP.add(mCbEncrypt, cc.xy(11, 2));
        btnDelUrl = new JButton();
        btnDelUrl.setText("Delete");
        baseP.add(btnDelUrl, cc.xy(7, 2));
        mCbMethod = new JComboBox();
        baseP.add(mCbMethod, cc.xy(9, 2));
        final JLabel label2 = new JLabel();
        label2.setText("Api Url:");
        baseP.add(label2, cc.xy(1, 4));
        mCbApiUrl = new JComboBox();
        mCbApiUrl.setEditable(true);
        baseP.add(mCbApiUrl, cc.xyw(3, 4, 5));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
        baseP.add(panel1, cc.xyw(1, 7, 11));
        btnAddRow = new JButton();
        btnAddRow.setText("Add Row");
        panel1.add(btnAddRow);
        btnDelRow = new JButton();
        btnDelRow.setText("Delete Row");
        panel1.add(btnDelRow);
        btnClear = new JButton();
        btnClear.setText("Clear");
        panel1.add(btnClear);
        mBtnSend = new JButton();
        mBtnSend.setBorderPainted(false);
        mBtnSend.setIconTextGap(1);
        mBtnSend.setInheritsPopupMenu(false);
        mBtnSend.setMargin(new Insets(5, 5, 5, 5));
        mBtnSend.setMaximumSize(new Dimension(78, 78));
        mBtnSend.setMinimumSize(new Dimension(78, 78));
        mBtnSend.setOpaque(true);
        mBtnSend.setPreferredSize(new Dimension(78, 78));
        mBtnSend.setRequestFocusEnabled(false);
        mBtnSend.setText("");
        mBtnSend.setToolTipText("Send");
        baseP.add(mBtnSend, cc.xywh(11, 4, 1, 2));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setBackground(new Color(-15856893));
        baseP.add(panel2, cc.xyw(1, 5, 3));
        final JLabel label3 = new JLabel();
        label3.setForeground(new Color(-1));
        label3.setText("Pressure test");
        panel2.add(label3, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tvTestCount = new JTextField();
        tvTestCount.setToolTipText("test count");
        panel2.add(tvTestCount, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        mBtnStartTest = new JButton();
        mBtnStartTest.setText("Start");
        panel2.add(mBtnStartTest, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
            com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        baseP.add(mBrowserView, cc.xywh(1, 8, 13, 2));
        btnDelApi = new JButton();
        btnDelApi.setText("Delete");
        baseP.add(btnDelApi, cc.xy(9, 4));
        mbtnDp = new JButton();
        mbtnDp.setText(" Default Parameter");
        mbtnDp.setToolTipText(" Set Current Project Default Parameter");
        baseP.add(mbtnDp, cc.xyw(7, 5, 3));
        mPb = new JProgressBar();
        mPb.setMaximumSize(new Dimension(50, 10));
        mPb.setPreferredSize(new Dimension(50, 10));
        baseP.add(mPb, cc.xy(13, 10, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /** @noinspection ALL */
    public JComponent $$$getRootComponent$$$() {
        return baseP;
    }
}
