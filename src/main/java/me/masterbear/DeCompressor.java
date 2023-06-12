package me.masterbear;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static me.masterbear.TreeGenerator.generateTree;

/**
 * 解压核心类
 *
 * @author jugg
 */
public class DeCompressor {

    int tableLength;

    final static int OFFSET = 128;
    final String filename;
    int[] fre;
    int totalCompressedBit;
    CNode root;

    public DeCompressor(String filename) {
        this.filename = filename;
    }


    private static int readInt(InputStream inputStream) {
        byte[] b = new byte[1];
        int ans = 0;
        try {
            int l = 0;
            int offset = 24;
            while (inputStream.read(b) > 0) {
                l++;
                int val = (int) b[0];
                if (val < 0) {
                    val += 256;
                }
                ans = (ans | (val << offset));
                offset -= 8;
                if (l == 4) {
                    break;
                }
            }
            if (l != 4) {
                (new RuntimeException("Read Int Error")).printStackTrace();
                System.exit(-1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    void readHead(BufferedInputStream inputStream) {
        int tableSize = readInt(inputStream);
        fre = new int[tableSize];
        for (int i = 0; i < tableSize; i++) {
            fre[i] = readInt(inputStream);
        }
        tableLength = tableSize;
        totalCompressedBit = readInt(inputStream);
        System.out.println("Table Size:" + tableLength + ", totalByte: " + totalCompressedBit / 8 + "bytes");
    }

    void processTree(BufferedInputStream in) {
        readHead(in);
        root = generateTree(tableLength, fre, OFFSET);
    }

    public void decompress(String to) {
        try {
            BufferedInputStream r = new BufferedInputStream(Files.newInputStream(Paths.get(filename)));
            BufferedOutputStream w = new BufferedOutputStream(Files.newOutputStream(Paths.get(to)));
            processTree(r);
            byte[] buf = new byte[1];
            CNode temp = root;
            int processed = 0;
            while (r.read(buf) > 0) {
                byte b = buf[0];
                for (int i = 0; i < Math.min(totalCompressedBit - processed, 8); i++) {
                    if ((b & (1 << (8 - i - 1))) != 0) {
                        temp = temp.ch[1];
                    } else {
                        temp = temp.ch[0];
                    }

                    if (temp.isLeaf) {
                        w.write(new byte[]{temp.val});
                        temp = root;
                    }
                }
                processed += 8;
            }
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
