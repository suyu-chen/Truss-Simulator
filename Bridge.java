import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.awt.*;
import Jama.*;
import static java.lang.Math.*;

public class Bridge {

    /* BRIDGE VARS */
    public static final double TOL = 1e-13;
    public static double bridgeSpan = 14;
    public static double maxFloorBeamSpacing = 3.5;
    public static int maxMultiplier = 3;
    public static double minBeam = 1;
    public static double loadForce = 2.5;

    public static double maxForce = 6 - TOL;
    public static double minForce = -9 + TOL;

    public static double memberCost = 15;
    public static double nodeCost = 5;

    /* GENETIC VARS */
    private static int genPop = 200000;
    private static final int NUM_GENS = 50;    
    private static int survivors = 20;
    private static final double INIT_MOVE_AMOUNT = 2;
    private static final double DECK_MOVE_RATIO = .5;
    private static final double MOVE_DECAY = 0.95;
    private static final double GEN_DECAY = 0.9;
    private static final double SURVIVORS_DECAY = 0.9;
    private static boolean moveDeck = true;

    /* NODES AND MEMBERS */
    private ArrayList<Node> nodes = new ArrayList<Node>();
    private ArrayList<Member> members = new ArrayList<Member>();
    private HashMap<Node, Node> mirroredNodes = new HashMap<Node, Node>();
    private Node leftSupport = null;
    private Node rightSupport = null;

    /* DRAWING STUFF */
    private Font font;

    /* HEAT MAP */
    private Color[][] heatMapArray;
    private int heatMapYRange = 8;
    private int heatMapXRange = 10;
    private int heatMapCostMax = 1800;
    private int heatMapCostMin = 1300;

    /* BRIDGE SPECS */
    private double cost;

    public Bridge() {
        new SimulatorFrame(this);
        // this.addMirrorSupportNodes(7, 0);
        // this.addMirrorNodes(4.2, 0);
        // this.addMirrorNodes(1.1, 0);
        // this.addMirrorNodes(4, -4);
        // this.addCenterNode(-6);

        // this.connectNode(0, 2);
        // this.connectNode(1, 3);
        // this.connectNode(2, 4);
        // this.connectNode(3, 5);
        // this.connectNode(4, 5);
        // this.connectNode(6, 0);
        // this.connectNode(6, 2);
        // this.connectNode(6, 4);
        // this.connectNode(7, 1);
        // this.connectNode(7, 3);
        // this.connectNode(7, 5);
        // this.connectNode(8, 4);
        // this.connectNode(8, 5);
        // this.connectNode(8, 6);
        // this.connectNode(8, 7);

        this.addMirrorSupportNodes(7, 0);
        this.addMirrorNodes(3.5, 0);
        this.addCenterNode(0);
        this.addMirrorNodes(3.5 ,5.1041666667); // best top bridge
        // this.addMirrorNodes(3.569114903906847,-3.8029270178615824); // best bottom bridge
        // this.addMirrorNodes(4,-4);

        connectNode(0,2);
        connectNode(1,3);
        connectNode(3,4);
        connectNode(4,2);
        connectNode(5,0);
        connectNode(5,2);
        connectNode(5,4);
        connectNode(6,1);
        connectNode(6,3);
        connectNode(6,4);
        connectNode(5,6);

        try {
            this.font = new Font("Verdana", Font.PLAIN, SimPanel.gridSize / 2);
        } catch (Exception e) {
            this.font = new Font(Font.SANS_SERIF, Font.PLAIN, SimPanel.gridSize / 2);
        }

        calculate();
        getCost();
        
        calcHeatMapForTwoNodeBridge(nodes.get(5), nodes.get(6));
        // try {
        //     Thread.sleep(1000);
        // } catch (InterruptedException ex) {
        //     Thread.currentThread().interrupt();
        // }

        // optimize();
        // optimizeTwoPoints(nodes.get(5), nodes.get(6));
        // optimizeExtreme(nodes.get(5), nodes.get(6));
        printBridge();
    }

    public void addNode(double x, double y) {
        nodes.add(new Node(x, y));
    }

    public void addCenterNode(double y) {
        nodes.add(new Node(0, y));
    }

    public void addMirrorNodes(double x, double y) {
        Node rightNode = new Node(x, y);
        Node leftNode = new Node(-x, y);
        nodes.add(rightNode);
        nodes.add(leftNode);
        mirroredNodes.put(rightNode, leftNode);
    }

    public boolean addMirrorSupportNodes(double x, double y) {
        if (leftSupport != null)
            return false;
        rightSupport = new Node(x, y, true);
        leftSupport = new Node(-x, y, true);
        nodes.add(rightSupport);
        nodes.add(leftSupport);
        mirroredNodes.put(rightSupport, leftSupport);
        return true;
    }

    public int getNodeIndexAtPos(double x, double y) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).isAt(x, y, 0.1))
                return i;
        }
        return -1;
    }

    public void connectNode(int i1, int i2) {
        Member newMember = new Member(nodes.get(i1), nodes.get(i2));
        members.add(newMember);
        nodes.get(i1).addAdjacentNode(nodes.get(i2));
        nodes.get(i2).addAdjacentNode(nodes.get(i1));
        nodes.get(i1).addConnectingMember(newMember);
        nodes.get(i2).addConnectingMember(newMember);
    }

    public void connectNode(double x1, double y1, double x2, double y2) {
        connectNode(getNodeIndexAtPos(x1, y1), getNodeIndexAtPos(x2, y2));
    }

    public double getCost() {
        this.cost = nodes.size() * nodeCost;
        for (Member member : members) {
            cost += member.getCost();
        }
        return cost;
    }

    public boolean isSimpleTruss() {
        return members.size() == 2 * nodes.size() - 3;
    }

    public void draw(Graphics g) {
        for (Member member : members)
            member.draw(g);
        for (Node node : nodes) {
            node.draw(g);
        }

        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString("$" + round(this.cost * 100) / 100.0, 20, 20 + SimPanel.gridSize / 2);
    }

    public void drawDeckOnly(Graphics g) {
        for (Member member : members)
            if(member.isDeck()) member.drawBare(g);
        for (Node node : nodes) {
            if(node.isDeck()) node.draw(g);
        }

        // g.setFont(font);
        // g.setColor(Color.WHITE);
        // g.drawString("$" + round(this.cost * 100) / 100.0, 20, 20 + SimPanel.gridSize / 2);
    }

    public void drawHeatMap(Graphics g){
        int i = 0;
        int j = 0;
        for (double x = 0; x < heatMapXRange; x+=1.0/SimPanel.gridSize) {
            j=0;
            for (double y = -heatMapYRange; y<heatMapYRange; y+=1.0/SimPanel.gridSize) {
                g.setColor(heatMapArray[i][j]);
                g.fillRect(SimPanel.mapX(x), SimPanel.mapY(y), 1,1);
                g.fillRect(SimPanel.mapX(-x), SimPanel.mapY(y), 1,1);
                j++;
            }
            i++;
        }
    }

    private void calcHeatMapForTwoNodeBridge(Node right, Node left){
        double xi = right.getX();
        double yi = right.getY();
        Color invalid = new Color(0,0,0,0);
        Color pxColor;
        float redHue = 0;
        float maxHue = (float) 0.85;
        int i = 0;
        int j = 0;
        heatMapArray = new Color[SimPanel.screenW/2+1][SimPanel.screenH+1];
        for (double x = 0; x < heatMapXRange; x+=1.0/SimPanel.gridSize) {
            j=0;
            for (double y = -heatMapYRange; y<heatMapYRange; y+=1.0/SimPanel.gridSize) {
                right.setX(x);
                right.setY(y);
                left.setX(-x);
                left.setY(y);
                calculate();
                getCost();
                if(!validBridge()) pxColor = invalid;
                else if(cost>heatMapCostMax) pxColor = Color.RED;
                else pxColor = Color.getHSBColor((float) (maxHue*(heatMapCostMax-cost)/(heatMapCostMax - heatMapCostMin)), (float)1.0, (float)1.0);
                heatMapArray[i][j] = pxColor;
                // System.out.println(i + " " + j);
                j++;
            }
            i++;
        }
        right.setX(xi);
        right.setY(yi);
        left.setX(-xi);
        left.setY(yi);
        calculate();
        getCost();

    }

    public void optimizeTwoNode(Node right, Node left){
        double inc = 0.001;
        double yRange = 6;

        double bestCost = Double.MAX_VALUE;
        double[] bestXY = new double[2];

        for(double x=0; x<bridgeSpan/2; x+=inc){
            right.setX(x);
            left.setX(-x);
            for(double y=-yRange; y<yRange; y+=inc){
                right.setY(y);
                left.setY(y);
                calculate();
                if(getCost() < bestCost && validBridge()){
                    bestCost = cost;
                    bestXY[0] = x;
                    bestXY[1] = y;
                }
            }
        }

        right.setX(bestXY[0]);
        left.setX(-bestXY[0]);
        right.setY(bestXY[1]);
        left.setY(bestXY[1]);
        calculate();
        getCost();
        printBridge();
    }

    public void optimizeTwoNodeExtreme(Node right, Node left){
        double inc   = 0.0000000001;
        double range = 0.0000001;

        double bestCost = Double.MAX_VALUE;
        double[] bestXY = new double[2];

        double x1 = right.getX() - range;
        double x2 = right.getX() + range;
        double y1 = right.getY() - range;
        double y2 = right.getY() + range;

        for(double x=x1; x<x2; x+=inc){
            right.setX(x);
            left.setX(-x);
            for(double y = y1; y<y2; y += inc){
                right.setY(y);
                left.setY(y);
                calculate();
                if(getCost() < bestCost && validBridge()){
                    bestCost = cost;
                    System.out.println(cost + " " + right.getX() + " " + y);
                    bestXY[0] = right.getX();
                    bestXY[1] = y;
                }
            }
        }

        right.setX(bestXY[0]);
        left.setX(-bestXY[0]);
        right.setY(bestXY[1]);
        left.setY(bestXY[1]);
        calculate();
        getCost();
        printBridge();
    }

    public void calculateExternalForces() {
        double loadForce;
        double totalRxnForce = 0;
        double totalLoadMomentAboutLeft = 0;
        for (Node node : nodes) {
            loadForce = node.calcLoadForce();
            totalRxnForce -= loadForce;
            if (node.isDeck() && node != leftSupport) {
                totalLoadMomentAboutLeft -= loadForce * abs(node.getX() - leftSupport.getX());
            }

            node.setExtForce(loadForce);
        }

        double rightRxnForce = totalLoadMomentAboutLeft / abs(rightSupport.getX() - leftSupport.getX());
        rightSupport.setExtForce(rightSupport.getExtForce() + rightRxnForce);
        leftSupport.setExtForce(leftSupport.getExtForce() + (totalRxnForce - rightRxnForce));

    }

    /** @return 0 if success, -1 if not simple truss, -2 if unsolvable */
    public int calculate() {
        if (!isSimpleTruss())
            return -1;
        calculateExternalForces();

        int cols = members.size(); // # of vars
        int rows = 2 * nodes.size(); // # of eqns
        double[][] lhs = new double[rows][cols];
        double[] rhs = new double[rows];

        for (int i = 0; i < rows; i += 2) {
            Node node = nodes.get(i / 2);

            for (int j = 0; j < node.getAdjacentNodes().size(); j++) {
                int colToInsert = members.indexOf(node.getConnectedMembers().get(j));
                double dx = node.getAdjacentNodes().get(j).getX() - node.getX();
                double dy = node.getAdjacentNodes().get(j).getY() - node.getY();
                lhs[i][colToInsert] = dx / hypot(dx, dy);
                lhs[i + 1][colToInsert] = dy / hypot(dx, dy);
                rhs[i + 1] = node.getExtForce();
            }
        }

        // Creating Matrix Objects with arrays
        Matrix a = new Matrix(lhs);
        Matrix b = new Matrix(rhs, rhs.length);

        // a.print(13,10);
        // b.print(13,10);
        Matrix ans = a.solve(b);
        // ans.print(13, 10);
        double[] soln = ans.getRowPackedCopy();

        for (int i = 0; i < lhs.length; i++) {
            double ls = 0;
            for (int j = 0; j < lhs[0].length; j++) {
                ls += lhs[i][j] * soln[j];
            }
            if (abs(ls - rhs[i]) > TOL) {
                // System.out.println("Unsolvable Bridge");
                return -2;
            }
        }

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            member.setForce(soln[i]);
            member.calcMultiplier();
        }

        // System.out.println("Solved");

        return 0;
    }

    public boolean validBridge() {
        for (Member member : members) {
            if (!member.isValid() || member.isOverloaded()) {
                return false;
            }
        }
        return true;
    }

    public boolean optimize() {
        if (this.calculate() < 0) {
            return false;
        }

        int numVars = determineNumVars();
        boolean[] deckNodes = getDeckBoolArray(numVars);
        double[] originalVars = getVarArray(numVars);

        ConcurrentSkipListMap<Double, double[]> bestCosts = new ConcurrentSkipListMap<Double, double[]>(); // cost:vars
        bestCosts.put(this.getCost(), Arrays.copyOf(originalVars, numVars));
        for(int i=1; i<survivors; i++){
            bestCosts.put(Double.MAX_VALUE,  Arrays.copyOf(originalVars, numVars));
        }

        double[] vars;
        double maxMoveAmount = INIT_MOVE_AMOUNT;

        System.out.println(cost);
        System.out.println(Arrays.toString(originalVars));

        for (int i = 0; i < NUM_GENS; i++) {
            for(Map.Entry<Double, double[]> e : bestCosts.entrySet()){
                for (int j = 0; j < genPop; j++) {
                    vars = Arrays.copyOf(e.getValue(), numVars);
                    for (int k = 0; k < numVars; k++) {
                        if(deckNodes[k] == true)
                            vars[k] += maxMoveAmount *DECK_MOVE_RATIO* Math.random();
                        else   
                            vars[k] += maxMoveAmount * Math.random();
                    }
                    updateBridgeFromVars(vars);
                    int cr = calculate();
                    if (cr == 0 && getCost() < bestCosts.lastKey() && validBridge()) {
                        System.out.println(cost);
                        System.out.println(Arrays.toString(vars));
                        bestCosts.remove(bestCosts.lastKey());
                        bestCosts.put(cost, Arrays.copyOf(vars, numVars));
                    }
                }
            }
            maxMoveAmount *= MOVE_DECAY;
            genPop *= GEN_DECAY;
            survivors *= SURVIVORS_DECAY;
        }

        updateBridgeFromVars(bestCosts.firstEntry().getValue());
        calculate();
        this.getCost();

        return true;
    }

    private int determineNumVars() {
        int numVars = 0;
        for (Node node : nodes) {
            if (!mirroredNodes.containsKey(node) && !mirroredNodes.containsValue(node) && !node.isDeck()) {
                // center nodes
                numVars++;
            } else if (mirroredNodes.containsKey(node)) {
                // right side nodes only
                if (moveDeck && node.isDeck() && abs(node.getX() - bridgeSpan / 2) > TOL) {
                    // non-end mirrored deck nodes
                    numVars++;
                } else if (!node.isDeck()) {
                    // non-deck mirrored nodes
                    numVars += 2;
                }
            }
        }
        return numVars;
    }

    private double[] getVarArray(int numVars) {
        double[] vars = new double[numVars];
        int idx = 0;
        for (Node node : nodes) {
            if (!mirroredNodes.containsKey(node) && !mirroredNodes.containsValue(node) && !node.isDeck()) {
                // center nodes
                vars[idx] = node.getY();
                // System.out.println("Center" + nodes.indexOf(node));
                idx++;
            } else if (mirroredNodes.containsKey(node)) {
                // right side nodes only
                if (moveDeck && node.isDeck() && abs(node.getX() - bridgeSpan / 2) > TOL) {
                    // non-end mirrored deck nodes
                    // System.out.println("Deck" + nodes.indexOf(node));
                    vars[idx] = node.getX();
                    idx++;
                } else if (!node.isDeck()) {
                    // non-deck mirrored nodes
                    // System.out.println("Non" + nodes.indexOf(node));
                    vars[idx] = node.getX();
                    idx++;
                    vars[idx] = node.getY();
                    idx++;
                }
            }
        }
        return vars;
    }

    private boolean[] getDeckBoolArray(int numVars) {
        boolean[] deck = new boolean[numVars];
        int idx = 0;
        for (Node node : nodes) {
            if (!mirroredNodes.containsKey(node) && !mirroredNodes.containsValue(node) && !node.isDeck()) {
                // center nodes
                deck[idx] = false;
                // System.out.println("Center" + nodes.indexOf(node));
                idx++;
            } else if (mirroredNodes.containsKey(node)) {
                // right side nodes only
                if (moveDeck && node.isDeck() && abs(node.getX() - bridgeSpan / 2) > TOL) {
                    // non-end mirrored deck nodes
                    // System.out.println("Deck" + nodes.indexOf(node));
                    deck[idx] = true;
                    idx++;
                } else if (!node.isDeck()) {
                    // non-deck mirrored nodes
                    // System.out.println("Non" + nodes.indexOf(node));
                    deck[idx] = false;
                    idx++;
                    deck[idx] = false;
                    idx++;
                }
            }
        }
        return deck;
    }

    private void updateBridgeFromVars(double[] vars) {
        int idx = 0;
        for (Node node : nodes) {
            if (!mirroredNodes.containsKey(node) && !mirroredNodes.containsValue(node) && !node.isDeck()) {
                // center nodes
                node.setY(vars[idx]);
                idx++;
            } else if (mirroredNodes.containsKey(node)) {
                // right side nodes only
                if (moveDeck && node.isDeck() && abs(node.getX() - bridgeSpan / 2) > TOL) {
                    // non-end mirrored deck nodes
                    node.setX(vars[idx]);
                    mirroredNodes.get(node).setX(-vars[idx]);
                    idx++;
                } else if (!node.isDeck()) {
                    // non-deck mirrored nodes
                    node.setX(vars[idx]);
                    mirroredNodes.get(node).setX(-vars[idx]);
                    idx++;
                    node.setY(vars[idx]);
                    mirroredNodes.get(node).setY(vars[idx]);
                    idx++;
                }
            }
        }
    }

    public void printBridge(){
        System.out.println("COST: " + getCost());

        for(int i=0; i<nodes.size(); i++){
            Node node = nodes.get(i);
            System.out.println("Node " + i + " : (" + node.getX() + ", " + node.getY() + ")");
        }
        for(Member m : members){
            System.out.println("Member Connecting Nodes: " + nodes.indexOf(m.getNodes()[0]) + " " + nodes.indexOf(m.getNodes()[1]) + ", Multiplier " + m.getMultiplier() + ", Length " + m.getLength() + ", Cost "+ m.getCost());
        }
    }

}
