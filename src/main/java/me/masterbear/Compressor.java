package me.masterbear;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

/**
 * 压缩核心类
 */
public class Compressor {

    /**
     * 表长度
     */
    final static int TABLE_LENGTH = 256;
    /**
     * 偏移量
     */
    final static int OFFSET = 128;
    /**
     * 需要压缩的文件名
     */
    final String filename;
    /**
     * 编码表
     */
    final HashMap<Byte, String> encodeTable = new HashMap<>();
    /**
     * 解码表
     */
    final HashMap<String, Byte> decodeTable = new HashMap<>();
    /**
     *
     */
    int[] fre = new int[256];
    /**
     * 总原始字节
     */
    int totalOriginByte;
    /**
     * 总压缩位
     */
    int totalCompressedBit;

    /**
     * 树节点
     */
    CNode root;

    public Compressor(String file) {
        filename = file;
    }

    private void init() {
        genFreTable();
        root = TreeGenerator.generateTree(TABLE_LENGTH, fre, OFFSET);
        processTree();
    }

    /**
     * 获取输入流
     *
     * @param filename 文件名
     * @return 输入流
     */
    BufferedInputStream getReader(String filename) {
        FileInputStream f = null;
        try {
            // 读取目标文件的输入流
            f = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // 退出程序
            System.exit(-1);
        }
        return new BufferedInputStream(f);
    }


    /**
     *
     */
    void genFreTable() {
        BufferedInputStream r = getReader(filename);
        byte[] buf = new byte[1024];
        int sz;
        int total = 0;
        try {
            while ((sz = r.read(buf)) > 0) {
                for (int i = 0; i < sz; i++) {
                    fre[buf[i] + OFFSET]++;
                }
                total += sz;
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IO Error");
            System.exit(-1);
        }
        totalOriginByte = total;
        System.out.println("Total:" + total + " bytes");
    }

    void processTree() {
        dfs(root, "");
        for (byte b : encodeTable.keySet()) {
            decodeTable.put(encodeTable.get(b), b);
        }
    }

    void dfs(CNode root, String cur) {
        if (root == null) {
            return;
        }
        if (root.isLeaf) {
            encodeTable.put(root.val, cur);
            //return;
        }
        dfs(root.ch[0], cur + "0");
        dfs(root.ch[1], cur + "1");
    }

    public void compress(String to) {
        init();
        BufferedInputStream r = getReader(filename);
        BitWriter bitWriter = new BitWriter(to);
        bitWriter.writeFreqHead(fre);
        bitWriter.writeTotalBitPlaceHolder(); // write 0 first
        byte[] buf = new byte[1024];
        int sz;
        try {
            while ((sz = r.read(buf)) > 0) {
                for (int i = 0; i < sz; i++) {
                    String e = encodeTable.get(buf[i]);
                    assert e != null && e.length() != 0;
                    for (int j = 0; j < e.length(); j++) {
                        if (e.charAt(j) == '0') {
                            bitWriter.writeBit(0);
                        } else {
                            bitWriter.writeBit(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("IO Error");
            System.exit(-1);
        }
        bitWriter.close();
        totalCompressedBit = bitWriter.getTotalBit();
        System.out.println("Compressed bit: " + bitWriter.getTotalBit());
        bitWriter.writeTotalBit();
    }
}

