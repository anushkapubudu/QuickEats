package lk.hndit.quickeats.util;

public class Common {

    public static String  getOrderStatus(int num){
        switch (num){
            case 0:
                return "Waiting";

            case 1:
                return "Preparing";

            case 2:
                return "Delivering";

            case 3:
                return "Complete";

            default:
                return "";
        }

    }
}
