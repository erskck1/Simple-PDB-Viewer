package visualization.model.secondary;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import model.AminoAcid;


/**
 * Represents a single alpha helix in the protein structure.
 */

public class Helix {

    private AminoAcid start, end;

    private Point3D startP, endP;

    private Cylinder shape;

    private int serial, initSeqNum, endSeqNum;

    private String startChainID, endChainID;


    /**
     * Instantiates a new helix.
     *
     * @param start the start residue
     */
    public Helix(AminoAcid start, AminoAcid end) {
        this.start = start;
        this.end = end;

        startP = start.getAtomBy("CA").getPosition();
        endP = end.getAtomBy("CA").getPosition();

        shape = makeCylinder(startP, endP);
    }

    private Cylinder makeCylinder(Point3D start, Point3D end) {
        Point3D diff = new Point3D(end.getX() - start.getX(), end.getY()
                - start.getY(), end.getZ() - start.getZ());

        diff = diff.normalize();

        Point3D midpoint = start.midpoint(end);

        final Point3D YAXIS = new Point3D(0, 1, 0);

        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.WHITE);
        material.setSpecularColor(Color.WHITE);

        double length = start.distance(end);
        Cylinder cyl = new Cylinder(1f, length, 10);
        cyl.setMaterial(material);

        Point3D crossVec = YAXIS.crossProduct(diff);

        double ac = (double) Math.acos(YAXIS.dotProduct(diff));

        cyl.getTransforms()
                .add(new Translate(midpoint.getX(), midpoint.getY(), midpoint
                        .getZ()));
        cyl.getTransforms().add(new Rotate(Math.toDegrees(ac), crossVec));

        cyl.setVisible(true);
        return cyl;
    }

    /**
     * setters and getters
     */
    public Cylinder getNode() {
        return shape;
    }

    public void setNode(Cylinder shape) {
        this.shape = shape;
    }

}
