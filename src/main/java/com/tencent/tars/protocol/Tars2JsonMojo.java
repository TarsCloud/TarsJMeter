package com.tencent.tars.protocol;

import com.tencent.tars.protocol.parse.TarsContent;
import com.tencent.tars.protocol.parse.TarsLexer;
import com.tencent.tars.protocol.parse.TarsParser;
import com.tencent.tars.protocol.parse.ast.*;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Tars struct to json for performance test.
 *
 * @author brookechen
 */
public class Tars2JsonMojo {

    protected static final Logger log = LoggerFactory.getLogger(Tars2JsonMojo.class);

    private static final String SINGLE_TAB = "  ";

    //源文件
    private String tarsFilePath = "";
    private String[] tarsFiles = new String[0];

    //需要那个数据结构的json
    private String rootStructName = "";

    private List<TarsEnum> allEnum = new ArrayList<>();

    //json文件写到哪儿
    private String outputDir = System.getProperty("user.dir");

    private String outPutJsonStr = "";

    private static final int MODE_TO_FILE = 0;
    private static final int MODE_TO_STRING_RET = 1;
    private int mode = MODE_TO_FILE;

    private void traversalTarsDir(ArrayList<String> tarsFileList, File dir) {
        File[] tarsFiles = dir.listFiles();
        if (tarsFiles == null) {
            return;
        }
        for (File tarsFile : tarsFiles) {
            if (tarsFile.isFile() && (tarsFile.getName().endsWith(".tars"))) {
                tarsFileList.add(tarsFile.getAbsolutePath());
            } else if (tarsFile.isDirectory()) {
                traversalTarsDir(tarsFileList, tarsFile);
            }
        }
    }

    public void execute() {
//        getLog().info(this.outputDir);

        if (tarsFilePath.isEmpty()) {
            log.error("tars file or dir path error :" + tarsFilePath);
            return;
        }

        // 1 . load tars files
        File tarsFileOrDir = new File(tarsFilePath);
        if (tarsFileOrDir.isDirectory()) {
            ArrayList<String> tarsFileList = new ArrayList<>();
            traversalTarsDir(tarsFileList, tarsFileOrDir);
            this.tarsFiles = tarsFileList.toArray(new String[0]);
        } else if (tarsFileOrDir.isFile()) {
            tarsFiles = new String[]{tarsFilePath};
        }

        if (tarsFiles.length <= 0) {
            log.error("not tars file or tars dir :" + tarsFilePath);
            return;
        }

        // 2. parse tars files
        Map<String, TarsContent> nsMap = new HashMap<>();
        for (String tarsFile : this.tarsFiles) {
            File tempFile = new File(tarsFile.trim());
            String fileName = tempFile.getName();
            try {
                log.info("Parse " + tarsFile + " ...");
                TarsLexer tarsLexer = new TarsLexer(new ANTLRFileStream(tarsFile));
                CommonTokenStream tokens = new CommonTokenStream(tarsLexer);
                TarsParser tarsParser = new TarsParser(tokens);
                TarsRoot root = (TarsRoot) tarsParser.start().getTree();
                root.setTokenStream(tokens);

                TarsContent content = new TarsContent();
                content.setNamespaces(root.namespaceList());
                content.setIncludes(root.includeFileList());
                nsMap.put(fileName, content);
            } catch (Throwable th) {
                log.error("Parse " + tarsFile + " Error!", th);
            }
        }

        // 3. generate json files.
        try {
            log.info("generate json for : " + this.rootStructName + " ...");
            getJson(nsMap);
        } catch (Throwable th) {
            log.error("generate json for : " + this.rootStructName + " Error!", th);
            th.printStackTrace();
        }
    }

    private void getJson(Map<String, TarsContent> nsMap) throws FileNotFoundException {
        String ret = "";
        // collect Enum
        for (TarsContent content : nsMap.values()) {
            for (TarsNamespace namespace : content.getNamespaces()) {
                for (TarsConst item : namespace.constList()) {
                    log.debug("const:" + item.constName());
                }
                for (TarsEnum item : namespace.enumList()) {
                    log.debug("enum:" + namespace.namespace() + "." + item.enumName());
                }
                for (TarsStruct item : namespace.structList()) {
                    log.debug("struct:" + namespace.namespace() + "." + item.structName());
                }
                this.allEnum.addAll(namespace.enumList());
            }
        }

        if (mode == MODE_TO_FILE) {
            new File(this.outputDir).mkdirs();
        }
        StringWriter sw = new StringWriter();
        String fileNamePath = "";
        for (String key : nsMap.keySet()) {
            TarsContent content = nsMap.get(key);
            for (TarsNamespace ns : content.getNamespaces()) {
                for (TarsStruct s : ns.structList()) {
                    if (!s.structName().equals(this.rootStructName)) {
                        continue;
                    }
                    List<TarsNamespace> namespaces = getIncludeNamespace(content, nsMap);
                    ret = getStruct(SINGLE_TAB, s.structName(), namespaces);
                    PrintWriter out = new PrintWriter(sw);
                    out.println("{");
                    out.println(ret);
                    out.println("}");
                    log.debug("output file : \n" + rootStructName + ".json\n{\n" + ret + "\n}");
                    out.close();
                }
                String fileNameWithoutSuffix = key.replaceAll(".tars", "");
                fileNamePath = this.outputDir + File.separator +
                        fileNameWithoutSuffix + "." + ns.namespace() + "." + this.rootStructName + ".json";
            }
        }
        this.outPutJsonStr = sw.toString();
        if (mode == MODE_TO_FILE) {
            try {
                FileWriter writer = new FileWriter(fileNamePath);
                BufferedWriter bufWriter = new BufferedWriter(writer);
                bufWriter.write(this.outPutJsonStr);
                bufWriter.flush();
                bufWriter.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从根节点遍历出所有的相关tars文件，保证能够完全解析出一个数据结构，并且不会因为有重复命名的结构而导致json格式错误
     */
    private List<TarsNamespace> getIncludeNamespace(TarsContent content, Map<String, TarsContent> nsMap) {
        List<TarsNamespace> ret = content.getNamespaces();
        for (TarsInclude include : content.getIncludes()) {
            TarsContent subContent = nsMap.get(include.fileName());
            if (subContent != null) {
                ret.addAll(getIncludeNamespace(subContent, nsMap));
            }
        }
        return ret;
    }

    private void printMap(PrintWriter out, String prefix, String memberName, int tag, TarsType type, List<TarsNamespace> namespaces) {
        String sub1Prefix = getSubPrefix(prefix);
        String sub2Prefix = getSubPrefix(sub1Prefix);
        String sub3Prefix = getSubPrefix(sub2Prefix);
        TarsType keyType = type.asMap().keyType();
        TarsType valueType = type.asMap().valueType();
        String key = "\"" + memberName + "\": ";
        if (memberName.isEmpty()) { //数组里面的
            key = "";
        }
        out.println(prefix + key + "{");
        out.println(sub1Prefix + "\"type\": \"map\",");
        out.println(sub1Prefix + "\"value\": [");
        out.println(sub2Prefix + "{");
        if (isTypeAtomic(keyType)) {
            printPrimitive(out, sub3Prefix, "key", 0, keyType, namespaces);
        } else if (keyType.isVector()) {
            printVector(out, sub3Prefix, "key", 0, keyType, namespaces);
        } else if (keyType.isMap()) {
            printMap(out, sub3Prefix, "key", 0, keyType, namespaces);
        } else {
            printTars(out, sub3Prefix, "key", 0, keyType, namespaces);
        }
        out.println(",");
        if (isTypeAtomic(valueType)) {
            printPrimitive(out, sub3Prefix, "value", 1, valueType, namespaces);
        } else if (valueType.isVector()) {
            printVector(out, sub3Prefix, "value", 1, valueType, namespaces);
        } else if (valueType.isMap()) {
            printMap(out, sub3Prefix, "value", 1, valueType, namespaces);
        } else {
            printTars(out, sub3Prefix, "value", 1, valueType, namespaces);
        }
        out.println();
        out.println(sub2Prefix + "}");
        out.println(sub1Prefix + "],");
        out.println(sub1Prefix + "\"tag\": " + tag);
        out.print(prefix + "}");
    }

    private void printVector(PrintWriter out, String prefix, String memberName, int tag, TarsType type, List<TarsNamespace> namespaces) {
        String sub1Prefix = getSubPrefix(prefix);
        String sub2Prefix = getSubPrefix(sub1Prefix);
        TarsType subType = type.asVector().subType();
        String key = "\"" + memberName + "\": ";
        if (memberName.isEmpty()) { //数组里面的
            key = "";
        }
        out.println(prefix + key + "{");
        out.println(sub1Prefix + "\"type\": \"vector\",");
        out.println(sub1Prefix + "\"value\": [");
        if (isTypeAtomic(subType)) {
            printPrimitive(out, sub2Prefix, "", 0, subType, namespaces);
        } else if (subType.isVector()) {
            printVector(out, sub2Prefix, "", 0, subType, namespaces);
        } else if (subType.isMap()) {
            printMap(out, sub2Prefix, "", 0, subType, namespaces);
        } else {
            printTars(out, sub2Prefix, "", 0, subType, namespaces);
        }
        out.println();
        out.println(sub1Prefix + "],");
        out.println(sub1Prefix + "\"tag\": " + tag);
        out.print(prefix + "}");
    }

    private void printPrimitive(PrintWriter out, String prefix, String memberName, int tag, TarsType type, List<TarsNamespace> namespaces) {
        String sub1Prefix = getSubPrefix(prefix);
        String key = "\"" + memberName + "\": ";
        if (memberName.isEmpty()) { //数组里面的
            key = "";
        }
        out.println(prefix + key + "{");
        out.println(sub1Prefix + "\"type\": \"" + type(type) + "\",");
        out.println(sub1Prefix + "\"value\": " + typeInit(type) + ",");
        out.println(sub1Prefix + "\"tag\": " + tag);
        out.print(prefix + "}");
    }

    private void printTars(PrintWriter out, String prefix, String memberName, int tag, TarsType type, List<TarsNamespace> namespaces) {
        String sub1Prefix = getSubPrefix(prefix);
        String sub2Prefix = getSubPrefix(sub1Prefix);
        String subStructStr;
        String key = "\"" + memberName + "\": ";
        if (memberName.isEmpty()) { //数组里面的
            key = "";
        }
        out.println(prefix + key + "{");
        out.println(sub1Prefix + "\"type\": \"tars\",");
        out.println(sub1Prefix + "\"value\": {");
        subStructStr = getStruct(sub2Prefix, type.typeName(), namespaces);
        if (subStructStr.isEmpty()) {
            subStructStr = sub2Prefix + "\"unsupported\":\"无法关联子结构,请手动添加:" + type.typeName() + "\"";
        }
        out.println(subStructStr);
        out.println(sub1Prefix + "},");
        out.println(sub1Prefix + "\"tag\": " + tag);
        out.print(prefix + "}");
    }


    private String getStruct(String prefix, String structName, List<TarsNamespace> namespaces) {
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        for (TarsNamespace ns : namespaces) {
            for (TarsStruct s : ns.structList()) {
                if (!structName.isEmpty() && !structName.equals(s.structName())) {
                    continue;
                }
                int index = 0;
                for (TarsStructMember m : s.memberList()) {
                    index++;
                    if (index > 1) {
                        out.println(",");
                    }
                    TarsType memberType = m.memberType();
                    if (isTypeAtomic(memberType)) {
                        printPrimitive(out, prefix, m.memberName(), m.tag(), memberType, namespaces);
                    } else if (memberType.isVector()) {
                        printVector(out, prefix, m.memberName(), m.tag(), memberType, namespaces);
                    } else if (memberType.isMap()) {
                        printMap(out, prefix, m.memberName(), m.tag(), memberType, namespaces);
                    } else {
                        printTars(out, prefix, m.memberName(), m.tag(), memberType, namespaces);
                    }
                }
            }
        }
        return sw.toString();
    }

    /**
     * 不需要再嵌套其他结构
     */
    private boolean isTypeAtomic(TarsType jt) {
        if (jt.isPrimitive()) {
            return true;
        } else if (jt.isVector()) {
            TarsVectorType v = jt.asVector();
            return v.subType().isPrimitive();
//        } else if (jt.isMap()) {
//            TarsType keyType = jt.asMap().keyType();
//            TarsType valueType = jt.asMap().valueType();
//            return isTypeAtomic(keyType) && isTypeAtomic(valueType);
        } else {
            return jt.isCustom() && isEnum(jt);
        }
    }


    private String type(TarsType jt) {
        if (jt.isPrimitive()) {
            TarsPrimitiveType p = jt.asPrimitive();
            switch (p.primitiveType()) {
                case VOID:
                    return "void";
                case BOOL:
                    return "boolean";
                case BYTE:
                    return "byte";
                case SHORT:
                    return "short";
                case INT:
                    return "int";
                case LONG:
                    return "long";
                case FLOAT:
                    return "float";
                case DOUBLE:
                    return "double";
                case STRING:
                    return "String";
                default:
                    return "";
            }
        } else if (jt.isVector()) {
            TarsVectorType v = jt.asVector();
            if (v.subType().isPrimitive()) {
                return type(v.subType()) + "[]";
            }
            return "vector";
        } else if (jt.isMap()) {
            return "map";
        } else if (jt.isCustom()) {
            if (isEnum(jt)) {
                return "int";
            }
            return "tars";
        } else {
            return "";
        }
    }

    private String typeInit(TarsType jt) {
        if (jt.isPrimitive()) {
            TarsPrimitiveType p = jt.asPrimitive();
            switch (p.primitiveType()) {
                case BOOL:
                    return "false";
                case BYTE:
                case SHORT:
                case INT:
                case LONG:
                    return "0";
                case FLOAT:
                case DOUBLE:
                    return "0.0";
                case STRING:
                    return "\"\"";
                case VOID:
                default:
                    return "";
            }
        } else if (jt.isVector()) {
            TarsVectorType v = jt.asVector();
            TarsType subType = v.subType();
            return "[ " + typeInit(subType) + " ]";
        } else if (jt.isMap()) {
            return "{}";
        } else if (jt.isCustom()) {
            if (isEnum(jt)) {
                return "0";
            }
            return "\"暂未支持\"";
        } else {
            return "";
        }
    }

    private boolean isEnum(TarsType jt) {
        if (!this.allEnum.isEmpty()) {
            for (TarsEnum tarsEnum : this.allEnum) {
                if (jt.typeName().equals(tarsEnum.enumName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getSharpWithLen(int len) {
        char sharp = '#';
        char[] repeat = new char[len];
        Arrays.fill(repeat, sharp);
        return new String(repeat);
    }

    private static String getSubPrefix(String parentPrefix) {
        int len = parentPrefix.length() + 2;
        char blank = ' ';
        char[] repeat = new char[len];
        Arrays.fill(repeat, blank);
        return new String(repeat);
    }


    public static void main(String[] args) {
        Tars2JsonMojo mojo = new Tars2JsonMojo();
        if (args.length < 2) {
//            mojo.rootStructName = "AccountDataResp";
//            mojo.tarsFilePath = "tars";
//            mojo.execute();
            help();
            System.exit(0);
        }
        mojo.rootStructName = args[0];
        mojo.tarsFilePath = args[1];
        mojo.execute();
    }

    public static String getJsonStr(String rootStructName, String tarsFilePath) {
        Tars2JsonMojo mojo = new Tars2JsonMojo();
        mojo.mode = MODE_TO_STRING_RET;
        mojo.rootStructName = rootStructName;
        mojo.tarsFilePath = tarsFilePath;
        mojo.execute();
        return mojo.outPutJsonStr;
    }

    private static void help() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println(getSharpWithLen(90));
        out.println("使用方法：");
        out.println("\tjava -jar tars2json.jar [structName] [tarsDirOrFilePath]");
        out.println("\t参数1: 需要生成json的tars数据结构名称");
        out.println("\t参数2: tars文件路径或者tars文件集合所在的文件夹");
        out.println("\t\t如果是文件，只以一个tars文件为源文件。如果是目录，将load目录下所有tars，代表");
        out.println("\t\t目标数据结构有引用其他tars文件的子结构会递归解析出来。");
        out.println(getSharpWithLen(90));
        System.out.println(writer.toString());
    }

}
