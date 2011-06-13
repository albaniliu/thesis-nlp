/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package feature;

/**
 * Contain all POS enum which are defined by vntagger
 * @author banhbaochay
 */
public enum POS {

    X("Unknown", 0),
    NP("Np", 1),
    NC("Nc", 2),
    NU("Nu", 3),
    N("N", 4),
    V("V", 5),
    A("A", 6),
    P("P", 7),
    R("R", 8),
    L("L", 9),
    M("M", 10),
    E("E", 11),
    C("C", 12),
    CC("CC", 13),
    I("I", 14),
    T("T", 15),
    Y("Y", 16),
    Z("Z", 17);

    /**
     * Set a string and a number for enum
     * @param name example: String of NP enum is Np
     * @param number is index of enum. Ex: NP is 1, NC is 2...
     */
    private POS(String name, int number) {
        this.pos = name;
        this.number = number;
    }

    /**
     * If pos is one of POS's pos, then return a POS.
     * @param pos pos which wants to get
     * @return Return X POS if no any POS has pos as one. Return a POS if others.
     */
    public static POS getPOS(String pos) {
        POS result = null;
        for (POS p : values()) {
            if (p.getName().equals(pos)) {
                result = p;
                break;
            }
            continue;
        }

        return (result != null) ? result : X;
    }

    /**
     * Get string of an enum. Can use POS.NP.getName() instead of POS.NP.toString()
     * @return a string of an enum
     */
    public String getName() {
        return pos;
    }

    /**
     * Get number index of an enum
     * @return an integer from 1 to 17
     */
    public int getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return pos;
    }

    /**
     * Example: NP enum has 2 value: string Np and number 1
     */
    private String pos;
    private int number;
}
