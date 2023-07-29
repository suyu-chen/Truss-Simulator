import static java.lang.Math.*;
import java.awt.*;

public class Member {
    private Node[] nodes = new Node[2];
    private int multiplier;
    private double force;

    private static Color tooMuchTension = new Color(255, 255, 153);
    private static Color tension = new Color(255, 217, 0);
    private static Color noForce = Color.LIGHT_GRAY;
    private static Color compression = new Color(51, 153, 255);
    private static Color tooMuchCompression = new Color(0, 51, 204);
    private static Color tooShort = Color.RED;

    private DisplayedNumber forceDisplay;
    private static Color textBg = new Color(0,0,0,128);
    private static double precision = 10000;

    public Member(Node start, Node end) {
        nodes[0] = start;
        nodes[1] = end;
        multiplier = 1;
        force = 0;
        forceDisplay = new DisplayedNumber(round(force*precision)/precision, SimPanel.gridSize / 4, Color.WHITE, 0, 0, textBg, SimPanel.gridSize / 10);
    }

    public void calcMultiplier() {
        if (force == 0)
            multiplier = 1;
        else if (force > 0)
            multiplier = (int) ceil(force / Bridge.maxForce);
        else if (force < 0)
            multiplier = (int) ceil(force / Bridge.minForce);

        if (multiplier > Bridge.maxMultiplier)
            multiplier = Bridge.maxMultiplier;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setForce(double force) {
        this.force = force;
    }
    
    public double getForce(){
        return force;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public double getLength() {
        return hypot(abs(nodes[0].getX() - nodes[1].getX()), abs(nodes[0].getY() - nodes[1].getY()));
    }

    public double getCost() {
        return Bridge.memberCost * getLength() * multiplier;
    }

    public boolean isDeck() {
        return nodes[0].isDeck() && nodes[1].isDeck();
    }

    public boolean isValid(){
        double length = this.getLength();
        return !(length < Bridge.minBeam || this.isDeck() && length > Bridge.maxFloorBeamSpacing);
    }

    public boolean isOverloaded(){
        return force < Bridge.minForce * multiplier || force > Bridge.maxForce * multiplier;
    }

    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        if (!isValid())
            g.setColor(tooShort);
        else if (force < Bridge.minForce * multiplier)
            g.setColor(tooMuchTension);
        else if (force > Bridge.maxForce * multiplier)
            g.setColor(tooMuchCompression);
        else if (abs(force) < Bridge.TOL)
            g.setColor(noForce);
        else if (force < 0)
            g.setColor(tension);
        else if (force > 0)
            g.setColor(compression);

        g.setStroke(
                new BasicStroke(SimPanel.gridSize * multiplier / 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawLine(SimPanel.mapX(nodes[0].getX()), SimPanel.mapY(nodes[0].getY()),
                SimPanel.mapX(nodes[1].getX()), SimPanel.mapY(nodes[1].getY()));

        forceDisplay.updatePosition(SimPanel.mapX((nodes[0].getX() + nodes[1].getX()) / 2),
                SimPanel.mapY((nodes[0].getY() + nodes[1].getY()) / 2));
        forceDisplay.changeNumber(round(force*precision)/precision);
        forceDisplay.draw(g);
    }

    public void drawBare(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setColor(Color.WHITE);
        g.setStroke(
                new BasicStroke(SimPanel.gridSize * multiplier / 10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g.drawLine(SimPanel.mapX(nodes[0].getX()), SimPanel.mapY(nodes[0].getY()),
                SimPanel.mapX(nodes[1].getX()), SimPanel.mapY(nodes[1].getY()));
    }
}
