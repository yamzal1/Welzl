package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import supportGUI.Circle;
import supportGUI.Line;
import java.util.Stack;


import java.util.Collections;

public class DefaultTeam {


    public Line calculDiametre(ArrayList<Point> points) {
        //Algo naif diamètre ensemble de points
        if (points.size() < 2) return null;
        Point p = points.get(0);
        Point q = points.get(1);
        for (Point s : points)
            for (Point t : points)
                if (s.distance(t) > p.distance(q)) {
                    p = s;
                    q = t;
                }
        return new Line(p, q);
    }

    public Circle calculCercleMin(ArrayList<Point> points) {
        System.out.println("ritter(points) : " + ritter(points));
        // return ritter(points);
        // return algoNaif(points);
         return welzl(points);
    }

    //ALGO NAIF CERCLE MIN
    private Circle algoNaif(ArrayList<Point> inputPoints) {
        //Algo naif cercle min
        ArrayList<Point> points = (ArrayList<Point>) inputPoints.clone();
        if (points.size() < 1) return null;
        double cX, cY, cRadius, cRadiusSquared;
        for (Point p : points) {
            for (Point q : points) {
                cX = .5 * (p.x + q.x);
                cY = .5 * (p.y + q.y);
                cRadiusSquared = 0.25 * ((p.x - q.x) * (p.x - q.x) + (p.y - q.y) * (p.y - q.y));
                boolean allHit = true;
                for (Point s : points)
                    if ((s.x - cX) * (s.x - cX) + (s.y - cY) * (s.y - cY) > cRadiusSquared) {
                        allHit = false;
                        break;
                    }
                if (allHit) return new Circle(new Point((int) cX, (int) cY), (int) Math.sqrt(cRadiusSquared));
            }
        }
        double resX = 0;
        double resY = 0;
        double resRadiusSquared = Double.MAX_VALUE;
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                for (int k = j + 1; k < points.size(); k++) {
                    Point p = points.get(i);
                    Point q = points.get(j);
                    Point r = points.get(k);
                    //si les trois sont colineaires on passe
                    if ((q.x - p.x) * (r.y - p.y) - (q.y - p.y) * (r.x - p.x) == 0) continue;
                    //si p et q sont sur la meme ligne, ou p et r sont sur la meme ligne, on les echange
                    if ((p.y == q.y) || (p.y == r.y)) {
                        if (p.y == q.y) {
                            p = points.get(k); //ici on est certain que p n'est sur la meme ligne de ni q ni r
                            r = points.get(i); //parce que les trois points sont non-colineaires
                        } else {
                            p = points.get(j); //ici on est certain que p n'est sur la meme ligne de ni q ni r
                            q = points.get(i); //parce que les trois points sont non-colineaires
                        }
                    }
                    //on cherche les coordonnees du cercle circonscrit du triangle pqr
                    //soit m=(p+q)/2 et n=(p+r)/2
                    double mX = .5 * (p.x + q.x);
                    double mY = .5 * (p.y + q.y);
                    double nX = .5 * (p.x + r.x);
                    double nY = .5 * (p.y + r.y);
                    //soit y=alpha1*x+beta1 l'equation de la droite passant par m et perpendiculaire a la droite (pq)
                    //soit y=alpha2*x+beta2 l'equation de la droite passant par n et perpendiculaire a la droite (pr)
                    double alpha1 = (q.x - p.x) / (double) (p.y - q.y);
                    double beta1 = mY - alpha1 * mX;
                    double alpha2 = (r.x - p.x) / (double) (p.y - r.y);
                    double beta2 = nY - alpha2 * nX;
                    //le centre c du cercle est alors le point d'intersection des deux droites ci-dessus
                    cX = (beta2 - beta1) / (double) (alpha1 - alpha2);
                    cY = alpha1 * cX + beta1;
                    cRadiusSquared = (p.x - cX) * (p.x - cX) + (p.y - cY) * (p.y - cY);
                    if (cRadiusSquared >= resRadiusSquared) continue;
                    boolean allHit = true;
                    for (Point s : points)
                        if ((s.x - cX) * (s.x - cX) + (s.y - cY) * (s.y - cY) > cRadiusSquared) {
                            allHit = false;
                            break;
                        }
                    if (allHit) {
                        System.out.println("Found r=" + Math.sqrt(cRadiusSquared));
                        resX = cX;
                        resY = cY;
                        resRadiusSquared = cRadiusSquared;
                    }
                }
            }
        }
        return new Circle(new Point((int) resX, (int) resY), (int) Math.sqrt(resRadiusSquared));
    }

    //ALGO RITTER CERCLE MIN
    private Circle ritter(ArrayList<Point> points) {
        //Algo Ritter
        if (points.size() < 1) return null;
        ArrayList<Point> rest = (ArrayList<Point>) points.clone();
        Point dummy = rest.get(0);
        Point p = dummy;
        for (Point s : rest) if (dummy.distance(s) > dummy.distance(p)) p = s;
        Point q = p;
        for (Point s : rest) if (p.distance(s) > p.distance(q)) q = s;
        double cX = .5 * (p.x + q.x);
        double cY = .5 * (p.y + q.y);
        double cRadius = .5 * p.distance(q);
        rest.remove(p);
        rest.remove(q);
        while (!rest.isEmpty()) {
            Point s = rest.remove(0);
            double distanceFromCToS = Math.sqrt((s.x - cX) * (s.x - cX) + (s.y - cY) * (s.y - cY));
            if (distanceFromCToS <= cRadius) continue;
            double cPrimeRadius = .5 * (cRadius + distanceFromCToS);
            double alpha = cPrimeRadius / (double) (distanceFromCToS);
            double beta = (distanceFromCToS - cPrimeRadius) / (double) (distanceFromCToS);
            double cPrimeX = alpha * cX + beta * s.x;
            double cPrimeY = alpha * cY + beta * s.y;
            cRadius = cPrimeRadius;
            cX = cPrimeX;
            cY = cPrimeY;
        }
        return new Circle(new Point((int) cX, (int) cY), (int) cRadius);
    }


    private double dist(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    private boolean isInside(Circle c, Point p) {
        if (c == null) {
            return false;
        }
        return dist(c.getCenter(), p) <= c.getRadius();
    }

    private Point getCircleCenter(int bx, int by, int cx, int cy) {
        int B = bx * bx + by * by;
        int C = cx * cx + cy * cy;
        int D = bx * cy - by * cx;
        if (D == 0) {
            return null;
        }
        return new Point((cy * B - by * C) / (2 * D), (bx * C - cx * B) / (2 * D));
    }

    private Circle circleFrom(Point A, Point B, Point C) {
        Point I = getCircleCenter(B.x - A.x, B.y - A.y, C.x - A.x, C.y - A.y);
        if (I == null) {
            return null;
        }
        I.x += A.x;
        I.y += A.y;
        return new Circle(I, (int) dist(I, A));
    }

    private Circle circleFrom(Point A, Point B) {
        Point C = new Point((int) ((A.x + B.x) / 2.0), (int) ((A.y + B.y) / 2.0));
        return new Circle(C, (int) (dist(A, B) / 2.0));
    }

    private boolean isValidCircle(Circle c, ArrayList<Point> P) {
        for (Point p : P) {
            if (!isInside(c, p)) {
                return false;
            }
        }
        return true;
    }

    private Circle minCircleTrivial(ArrayList<Point> P) {
        if (P.isEmpty()) {
            return new Circle(new Point(0, 0), 0);
        } else if (P.size() == 1) {
            return new Circle(P.get(0), 0);
        } else if (P.size() == 2) {
            return circleFrom(P.get(0), P.get(1));
        }

        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 3; j++) {
                Circle c = circleFrom(P.get(i), P.get(j));
                if (isValidCircle(c, P)) {
                    return c;
                }
            }
        }
        return circleFrom(P.get(0), P.get(1), P.get(2));
    }

    private Circle welzlHelper(ArrayList<Point> P, ArrayList<Point> R, int n) {
        if (n == 0 || R.size() == 3) {
            return minCircleTrivial(R);
        }

        int idx = new Random().nextInt(n);
        Point p = P.get(idx);

        Collections.swap(P, idx, n - 1); //Mettre le point selectionné à la fin de points est plus rapide que de le retirer

        Circle d = welzlHelper(P, new ArrayList<>(R), n - 1);

        if (isInside(d, p)) {
            return d;
        }

        R.add(p);

        return welzlHelper(P, new ArrayList<>(R), n - 1);
    }

    public Circle welzl(ArrayList<Point> P) {
        ArrayList<Point> P_copy = new ArrayList<>(P);

        Collections.shuffle(P_copy);
        return welzlHelper(P_copy, new ArrayList<Point>(), P_copy.size());
    }

    private ArrayList<Point> filtrageAklToussaint(ArrayList<Point> points) {
        if (points.size() < 4) return points;

        Point ouest = points.get(0);
        Point sud = points.get(0);
        Point est = points.get(0);
        Point nord = points.get(0);
        for (Point p : points) {
            if (p.x < ouest.x) ouest = p;
            if (p.y > sud.y) sud = p;
            if (p.x > est.x) est = p;
            if (p.y < nord.y) nord = p;
        }
        ArrayList<Point> result = (ArrayList<Point>) points.clone();
        for (int i = 0; i < result.size(); i++) {
            if (triangleContientPoint(ouest, sud, est, result.get(i)) || triangleContientPoint(ouest, est, nord, result.get(i))) {
                result.remove(i);
                i--;
            }
        }
        return result;
    }

    private boolean triangleContientPoint(Point a, Point b, Point c, Point x) {
        double l1 = ((b.y - c.y) * (x.x - c.x) + (c.x - b.x) * (x.y - c.y)) / (double) ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y));
        double l2 = ((c.y - a.y) * (x.x - c.x) + (a.x - c.x) * (x.y - c.y)) / (double) ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y));
        double l3 = 1 - l1 - l2;
        return (0 < l1 && l1 < 1 && 0 < l2 && l2 < 1 && 0 < l3 && l3 < 1);
    }


}
