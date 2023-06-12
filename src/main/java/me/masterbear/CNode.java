package me.masterbear;

/**
 * 哈夫曼树
 *
 * @author jugg
 */
public class CNode {
    /**
     * 值
     */
    final public byte val;
    /**
     * 频率
     */
    final int freq;
    /**
     * 是否为左节点
     */
    final boolean isLeaf;

    /**
     * 子节点
     */
    public final CNode[] ch = new CNode[2];

    public CNode(byte val, int fre, boolean isLeaf) {
        this.val = val;
        this.freq = fre;
        this.isLeaf = isLeaf;
    }

    public int getFreq() {
        return freq;
    }

    public boolean isLeaf() {
        return isLeaf;
    }
}
