package com.tencent.tars.jmeter.gui.widgets;

import com.tencent.tars.jmeter.constants.ITarsConst;
import com.tencent.tars.jmeter.gui.CustomResUtils;
import com.tencent.tars.jmeter.utils.ArgTriple;
import com.tencent.tars.jmeter.utils.Triple;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.LinkedList;

public class ExtraArgumentsPanel extends ArgumentsPanelBase {
    private static final LinkedList<Triple<String, String, String>> DEFAULT_ARGS = new LinkedList<>();

    static {
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_KEEP_ALIVE,
                CustomResUtils.getResString("tars.param.extra.keep.alive"), "true"));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_PROXY_HOST,
                CustomResUtils.getResString("tars.param.extra.proxy.ip"), ""));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_PROXY_PORT,
                CustomResUtils.getResString("tars.param.extra.proxy.port"), ""));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_CONNECT_TIMEOUT,
                CustomResUtils.getResString("tars.param.extra.tcp.connect.timeout"), "8000"));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_READ_TIMEOUT,
                CustomResUtils.getResString("tars.param.extra.tcp.read.timeout"), "8000"));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_REQ_VERSION,
                CustomResUtils.getResString("tars.param.extra.tup.version"), "1"));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_REQ_PKT_TYPE,
                CustomResUtils.getResString("tars.param.extra.tup.packettype"), "0"));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_REQ_MSG_TYPE,
                CustomResUtils.getResString("tars.param.extra.tup.messagetype"), "0"));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_REQ_I_TIMEOUT,
                CustomResUtils.getResString("tars.param.extra.tup.timeout"), "0"));
        DEFAULT_ARGS.add(new ArgTriple(ITarsConst.KEY_RET_CODE,
                CustomResUtils.getResString("tars.param.extra.retcode"), "0"));
    }

    protected Arguments getDefaultArguments() {
        Arguments newArgs = new Arguments();
        for (Triple<String, String, String> triple : DEFAULT_ARGS) {
            newArgs.addArgument(triple.getLeft(), triple.getRight(), null, triple.getMiddle());
        }
        return newArgs;
    }

    public ExtraArgumentsPanel() {
        super(CustomResUtils.getResString("tars.param.extra.title")); //$NON-NLS-1$
        init();
        clearBorderForMainPanel();
    }

    @Override
    protected void initializeTableModel() {
        if (tableModel == null) {
            tableModel = new ObjectTableModel(new String[]{COLUMN_RESOURCE_NAMES_0, COLUMN_RESOURCE_NAMES_1, COLUMN_RESOURCE_NAMES_2},
                    Argument.class,
                    new Functor[]{
                            new Functor("getName"), // $NON-NLS-1$
                            new Functor("getValue"),  // $NON-NLS-1$
                            new Functor("getDescription")},  // $NON-NLS-1$
                    new Functor[]{
                            new Functor("setName"), // $NON-NLS-1$
                            new Functor("setValue"), // $NON-NLS-1$
                            new Functor("setDescription")},  // $NON-NLS-1$
                    new Class[]{String.class, String.class, String.class});
        }
    }

    @Override
    protected void sizeColumns(JTable _table) {
        super.sizeColumns(_table);
        TableColumn firstColumn = _table.getColumnModel().getColumn(0);
        TableColumn middleColumn = _table.getColumnModel().getColumn(1);
        TableColumn lastColumn = _table.getColumnModel().getColumn(2);
        firstColumn.setPreferredWidth(150);
        firstColumn.setMinWidth(150);
        firstColumn.setMaxWidth(150);
        middleColumn.setPreferredWidth(250);
        lastColumn.setPreferredWidth(250);
    }
}
