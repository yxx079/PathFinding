import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @Author: Elizabeth Monica
 * @Date: 2022-06-21
 * @Discription:
 */
public class test {

    public static final int[][] NODES = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    };
    public static int[][] temp=NODES;

    public static final int STEP = 10;

    private final ArrayList<Node> openList = new ArrayList();
    private final ArrayList<Node> closeList = new ArrayList();

    public Node findMinFNodeInOpneList() {
        Node tempNode = openList.get(0);
        for (Node node : openList) {
            if (node.F < tempNode.F) {
                tempNode = node;
            }
        }
        return tempNode;
    }

    public ArrayList<Node> findNeighborNodes(Node currentNode) {
        ArrayList<Node> arrayList = new ArrayList();
        // 只考虑上下左右，不考虑斜对角
        int topX = currentNode.x;
        int topY = currentNode.y - 1;
        if (canReach(topX, topY) && !exists(closeList, topX, topY)) {
            arrayList.add(new Node(topX, topY));
        }
        int bottomX = currentNode.x;
        int bottomY = currentNode.y + 1;
        if (canReach(bottomX, bottomY) && !exists(closeList, bottomX, bottomY)) {
            arrayList.add(new Node(bottomX, bottomY));
        }
        int leftX = currentNode.x - 1;
        int leftY = currentNode.y;
        if (canReach(leftX, leftY) && !exists(closeList, leftX, leftY)) {
            arrayList.add(new Node(leftX, leftY));
        }
        int rightX = currentNode.x + 1;
        int rightY = currentNode.y;
        if (canReach(rightX, rightY) && !exists(closeList, rightX, rightY)) {
            arrayList.add(new Node(rightX, rightY));
        }
        return arrayList;
    }

    public boolean canReach(int x, int y) {
        if (x >= 0 && x < temp.length && y >= 0 && y < temp[0].length) {
            return temp[x][y] == 0;
        }
        return false;
    }

    public Node findPath(Node startNode, Node endNode) {

        // 把起点加入 open list
        openList.add(startNode);

        while (openList.size() > 0) {
            // 遍历 open list ，查找 F值最小的节点，把它作为当前要处理的节点
            Node currentNode = findMinFNodeInOpneList();
            // 从open list中移除
            openList.remove(currentNode);
            // 把这个节点移到 close list
            closeList.add(currentNode);

            ArrayList<Node> neighborNodes = findNeighborNodes(currentNode);
            for (Node node : neighborNodes) {
                if (exists(openList, node)) {
                    foundPoint(currentNode, node);
                } else {
                    notFoundPoint(currentNode, endNode, node);
                }
            }
            if (find(openList, endNode) != null) {
                return find(openList, endNode);
            }
        }

        return find(openList, endNode);
    }

    private void foundPoint(Node tempStart, Node node) {
        int G = calcG(tempStart, node);
        if (G < node.G) {
            node.parent = tempStart;
            node.G = G;
            node.calcF();
        }
    }

    private void notFoundPoint(Node tempStart, Node end, Node node) {
        node.parent = tempStart;
        node.G = calcG(tempStart, node);
        node.H = calcH(end, node);
        node.calcF();
        openList.add(node);
    }

    private int calcG(Node start, Node node) {
        int G = STEP;
        int parentG = node.parent != null ? node.parent.G : 0;
        return G + parentG;
    }

    private int calcH(Node end, Node node) {
        int step = Math.abs(node.x - end.x) + Math.abs(node.y - end.y);
        return step * STEP;
    }

    public static int[][] randomAdditionalNum() {


        Random r1 = new Random();
        Random r2 = new Random();
        int obstacleNum = 0;
        while (obstacleNum < 21) {

            int x = r1.nextInt(9);
            int y= r2.nextInt(9);
            if (temp[x][y] == 0) {
                temp[x][y] = 1;
                obstacleNum++;
            }
            if(temp[0][0]==1){
                obstacleNum--;
            }

        }
        return temp;
    }

    public static void main(String[] args) {
        randomAdditionalNum();
        Node startNode = new Node(0, 0);
        Node endNode = new Node(9, 9);
        Node parent = new test().findPath(startNode, endNode);

        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                System.out.print(temp[i][j] + ", ");
            }
            System.out.println();
        }
        ArrayList<Node> arrayList = new ArrayList<Node>();

        while (parent != null) {
            // System.out.println(parent.x + ", " + parent.y);
            arrayList.add(new Node(parent.x, parent.y));
            parent = parent.parent;
        }
        System.out.println("\n");
        System.out.print("[");
        int stepNum = 0;
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                if (exists(arrayList, i, j)) {
                    System.out.printf("(%d,%d)", i, j);
                    stepNum++;
                }
            }

        }
        System.out.println("]");
        System.out.println("Min step is " + stepNum);

    }

    public static Node find(List<Node> nodes, Node point) {
        for (Node n : nodes)
            if ((n.x == point.x) && (n.y == point.y)) {
                return n;
            }
        return null;
    }

    public static boolean exists(List<Node> nodes, Node node) {
        for (Node n : nodes) {
            if ((n.x == node.x) && (n.y == node.y)) {
                return true;
            }
        }
        return false;
    }

    public static boolean exists(List<Node> nodes, int x, int y) {
        for (Node n : nodes) {
            if ((n.x == x) && (n.y == y)) {
                return true;
            }
        }
        return false;
    }

    public static class Node {
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int x;
        public int y;

        public int F;
        public int G;
        public int H;

        public void calcF() {
            this.F = this.G + this.H;
        }

        public Node parent;
    }
}
