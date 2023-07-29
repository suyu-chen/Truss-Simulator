import static java.lang.Math.*;
import java.awt.*;
import java.util.ArrayList;

public class Node {
    private double x;
    private double y;
    private boolean support;
    private ArrayList<Node> adjacentNodes = new ArrayList<Node>();
    private ArrayList<Member> connectedMembers = new ArrayList<Member>();
    private double extForce = 0;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
        support = false;
    }

    public Node(double x, double y, boolean support) {
        this.x = x;
        this.y = y;
        this.support = support;
    }

    public boolean isAt(double x, double y, double tol) {
        return abs(this.x - x) < tol && abs(this.y - y) < tol;
    }

    public void addAdjacentNode(Node node) {
        adjacentNodes.add(node);
    }

    public void addConnectingMember(Member member){
        connectedMembers.add(member);
    }

    public double calcLoadForce() {
        if (!this.isDeck())
            return 0;
        double force = 0;
        for (Node node : adjacentNodes) {
            if (node.isDeck()) {
                force -= abs(node.getX() - this.x) * Bridge.loadForce / 2;
            }
        }
        return force;
    }

    public void setExtForce(double externalForce){
        this.extForce = externalForce;
    }

    public double getExtForce(){
        return extForce;
    }

    public boolean isSupport() {
        return support;
    }

    public boolean isDeck() {
        return this.y == 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean getSupport() {
        return this.support;
    }

    public void setSupport(boolean support) {
        this.support = support;
    }

    public ArrayList<Node> getAdjacentNodes() {
        return this.adjacentNodes;
    }

    public void setAdjacentNodes(ArrayList<Node> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public ArrayList<Member> getConnectedMembers() {
        return this.connectedMembers;
    }

    public void setConnectedMembers(ArrayList<Member> connectedMembers) {
        this.connectedMembers = connectedMembers;
    }

    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        int innerD = SimPanel.gridSize / 5;
        int outerD = SimPanel.gridSize / 4;

        g.setColor(Color.WHITE);
        g.fillOval(SimPanel.mapX(x) - outerD / 2,
                SimPanel.mapY(y) - outerD / 2, outerD, outerD);

        if (support)
            g.setColor(new Color(49, 143, 49));
        else
            g.setColor(Color.BLACK);

        g.fillOval(SimPanel.mapX(x) - innerD / 2,
                SimPanel.mapY(y) - innerD / 2, innerD, innerD);

    }
}
