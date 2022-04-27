public class helper {
    public static void main(String[] args) throws Exception {
        byte[] ex1 = left_encode(234);
        printByte(ex1);

    }

    public static byte[] left_encode(final int x) throws Exception {
//        1. Let n be the smallest positive integer for which 2^(8n) > x.
        int n = 1;
        while (1<<(8*n) <= x){
            n++;
        }
        if (n > 256)
            throw new Exception("Left encode length greater than 256" + n);
        byte[] x_list = new byte[n+1];
//        2. Let x1, x2, …, xn be the base-256 encoding of x satisfying:
//        x = ∑ 2^8(n-i)xi, for i = 1 to n.
        for (int i = 1; i <= n; i++){
            x_list[i] = (byte) (x>>>(8*(n-i)));
        }
//        3. Let Oi = enc8(xi), for i = 1 to n.
//        4. Let O0 = enc8(n).
        x_list[0] = (byte) n;
//        5. Return O = O0 || O1 || … || On−1 || On.
        return x_list;
    }


    private static void printByte(byte[] source){
        for (int i = 0; i < source.length; i++){
            System.out.print(source[i]);
        }
        System.out.println();
    }
}
