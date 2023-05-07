package algorithms;

import java.awt.Point;
import java.util.ArrayList;

import supportGUI.Circle;
import supportGUI.Line;

public class DefaultTeam {

  // calculDiametre: ArrayList<Point> --> Line
  //   renvoie une pair de points de la liste, de distance maximum.
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
    return ritter(points);
   // return algoNaif(points);
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

  private double crossProduct(Point p, Point q, Point s, Point t) {
    return ((q.x - p.x) * (t.y - s.y) - (q.y - p.y) * (t.x - s.x));
  }

  private ArrayList<Point> exercice2(ArrayList<Point> points) {
    //Tri pixel
    if (points.size() < 4) return points;
    int maxX = points.get(0).x;
    for (Point p : points) if (p.x > maxX) maxX = p.x;
    Point[] maxY = new Point[maxX + 1];
    Point[] minY = new Point[maxX + 1];
    for (Point p : points) {
      if (maxY[p.x] == null || p.y > maxY[p.x].y) maxY[p.x] = p;
      if (minY[p.x] == null || p.y < minY[p.x].y) minY[p.x] = p;
    }
    ArrayList<Point> result = new ArrayList<Point>();
    for (int i = 0; i < maxX + 1; i++) if (maxY[i] != null) result.add(maxY[i]);
    for (int i = maxX; i >= 0; i--)
      if (minY[i] != null && !result.get(result.size() - 1).equals(minY[i])) result.add(minY[i]);

    if (result.get(result.size() - 1).equals(result.get(0))) result.remove(result.size() - 1);

    return result;
  }
  private ArrayList<Point> exercice3(ArrayList<Point> points) {
    //Filtrage Akl-Toussaint
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

  private double angle(Point p, Point q, Point s, Point t) {
    if (p.equals(q) || s.equals(t)) return Double.MAX_VALUE;
    double cosTheta = dotProduct(p, q, s, t) / (double) (p.distance(q) * s.distance(t));
    return Math.acos(cosTheta);
  }

  private double dotProduct(Point p, Point q, Point s, Point t) {
    return ((q.x - p.x) * (t.x - s.x) + (q.y - p.y) * (t.y - s.y));
  }

}