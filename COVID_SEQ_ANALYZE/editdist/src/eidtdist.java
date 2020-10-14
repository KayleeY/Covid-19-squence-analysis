public class eidtdist {
    static int MATCH = 1;
    static int MISMATCH = -1;
    static int INDEL = -1;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Two arguments are expected.");
            System.exit(0);
        }
        int score = 0;

        String a = args[0];
        String b = args[1];

        String [] align = Alignment(a, b);
        for (int i = 0; i < align[0].length(); i++) {
            if (align[0].charAt(i) == align[1].charAt(i)) {
                score++;
            } else {
                score--;
            }
        }
        System.out.println(score);
        System.out.println(align[0]);
        System.out.println(align[1]);

    }

    // Find optimal location j in T that produces the highest alignment score
    public static int FindJ(String S, String T){
        int Slength = S.length();
        int mid = Slength/2;

        StringBuilder reverseT = new StringBuilder();
        reverseT.append(T);
        String Tr = reverseT.reverse().toString();

        StringBuilder reverse2ndHalfS = new StringBuilder();
        reverse2ndHalfS.append(S.substring(mid, Slength));
        String Sr = reverse2ndHalfS.reverse().toString();
        //System.out.println(S.substring(0, mid) +" : "+ T);
        int []firsthalf = linearSpaceNW(S.substring(0, mid), T);
        //System.out.println(Sr +" : "+ Tr);
        int []secondhalf = linearSpaceNW(Sr, Tr);

        //reverse secondhalf
        for (int i = 0; i < secondhalf.length/2; i++) {
            int temp = secondhalf[i];
            secondhalf[i] = secondhalf[secondhalf.length - i - 1];
            secondhalf[secondhalf.length - i - 1] = temp;
        }

        int ret = 0;
        int maxscore = 0;
        //System.out.println("s "+ S + " t "+ T);
        //System.out.println("Tr "+ Tr + " Sr "+ Sr);
        for (int j = 0; j < T.length(); j++) {
            int score = firsthalf[j] + secondhalf[j];
            if (j == 0) {
                maxscore = score;
            }
            if (score > maxscore) {
                ret = j;
                maxscore = score;
            }
        }
        //System.out.println("j = " + ret);
        return ret;
    }

    // DC algorithm to produce alignment
    public static String[] Alignment(String S, String T) {
        int Slength = S.length();
        int Tlength = T.length();
        if (Slength <= 2 || Tlength <= 2) {
            return NWAlign(S, T);
        }
        int mid = Slength/2;
        int j = FindJ(S,T);

        String[] ab = Alignment(S.substring(0, mid),T.substring(0, j));
        String[] cd = Alignment(S.substring(mid, Slength), T.substring(j, Tlength));
        String[] ret = {ab[0].concat(cd[0]), ab[1].concat(cd[1])};
        return ret;
    }

    // Compare two chars depends the score (MATCH/INDEL/MISMATCH)
    public static int f(char a, char b) {
        if (a == b) {
            return MATCH;
        } else if (a == '-' || b == '-') {
            return INDEL;
        } else {
            return MISMATCH;
        }
    }

    // Find the maximum of 3 numbers
    public static int max(int a, int b, int c) {
        if (a >= b && a >= c) {
            return a;
        } else if (b >= a && b >= c) {
            return b;
        } else {
            return c;
        }
    }

    // compute the last line of DP array in linear space.
    public static int[] linearSpaceNW(String a, String b) {

        int m = a.length();
        int n = b.length();
        //System.out.println("n = " + n);
        int [] odd = new int [n+1];
        int [] even = new int [n+1];

        for (int i = 0; i <= n; i++) {
            odd[i] = i * INDEL;
        }

        for (int i = 0; i < m; i++) {
            char current = a.charAt(i);
            for (int j = 0; j <= n; j++) {
                // change even array
                if (i % 2 == 0) {
                    if (j == 0) {
                        even[j] = (i + 1) * INDEL;
                    } else {
                        //System.out.println("even j = " + j);
                        even[j] = max(odd[j - 1] + f(current, b.charAt(j - 1)),
                                odd[j] + f(current, '-'),
                                even[j - 1] + f(b.charAt(j - 1), '-'));
                    }
                } else {// change odd array
                    if (j == 0) {
                        odd[j] = (i + 1) * INDEL;
                    } else {
                        //System.out.println("odd j = " + j);
                        odd[j] = max(even[j - 1] + f(current, b.charAt(j - 1)),
                                even[j] + f(current, '-'),
                                odd[j - 1] + f(b.charAt(j - 1), '-'));
                    }
                }
            }
        }
        if ((m-1) % 2 == 0) {
            //score = even[n];
            return even;
        } else {
            //score = odd[n];
            return odd;
        }
    }

    // Compute the actual optimal alignment of 2 strings
    public static String[] NWAlign(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] DP = new int[m+1][n+1];

        for (int i = 0; i <= m; i++)
            DP[i][0] = i * INDEL;

        for (int j = 0; j <= n; j++)
            DP[0][j] = j * INDEL;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                //System.out.println("print i = " + i + "print j = " + j);
                DP[i][j] = max(DP[i-1][j-1] + f(a.charAt(i-1), b.charAt(j-1)),
                        DP[i-1][j] + f(a.charAt(i-1), '-'),
                        DP[i][j-1] + f('-', b.charAt(j-1)));
            }
        }
        StringBuilder OutputA = new StringBuilder();
        StringBuilder OutputB = new StringBuilder();

        // backtracking
        for (int i = m, j = n; i > 0 || j > 0; ) {
            if (i > 0 && j > 0 && DP[i][j] == DP[i-1][j-1] + f(a.charAt(i-1), b.charAt(j-1))) {
                OutputA.append(a.charAt(i-1));
                OutputB.append(b.charAt(j-1));
                i--;
                j--;
            } else if (i > 0 && DP[i][j] == DP[i-1][j] + f(a.charAt(i-1), '-')) {
                OutputA.append(a.charAt(i-1));
                OutputB.append("-");
                i--;
            } else if (j > 0 && DP[i][j] == DP[i][j-1] + f('-', b.charAt(j-1))) {
                OutputA.append("-");
                OutputB.append(b.charAt(j-1));
                j--;
            } else {
                System.out.println("Error occurred during backtracking.");
                System.exit(0);
            }
        }

        return new String[]{OutputA.reverse().toString(), OutputB.reverse().toString()};
    }

}
