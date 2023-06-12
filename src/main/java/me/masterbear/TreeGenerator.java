package me.masterbear;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * 树生成器
 *
 * @author 北落燕门
 */
public class TreeGenerator {
    public static CNode generateTree(int tableLength, int[] fre, int offset) {
        PriorityQueue<CNode> q = new PriorityQueue<>(Comparator.comparing(CNode::getFreq));
        for (int i = 0; i < tableLength; i++) {
            if (fre[i] == 0) {
                continue;
            }
            q.add(new CNode((byte) (i - offset), fre[i], true));
        }
        while (q.size() != 1) {
            CNode left = q.poll();
            CNode right = q.poll();
            assert left != null;
            assert right != null;
            CNode t = new CNode((byte) 0, left.freq + right.freq, false);
            t.ch[0] = left;
            t.ch[1] = right;
            q.add(t);
        }
        return q.peek();
    }
}
