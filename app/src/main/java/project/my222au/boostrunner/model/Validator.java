package project.my222au.boostrunner.model;


public class Validator {


      public static boolean checkWeight(Double parseWeight) {
        if (parseWeight < 349 && parseWeight >= 15) {
            return true;
        }
        return false;

    }



    public static boolean checkHeight(Double parseHeihgt) {
        if (parseHeihgt < 220 && parseHeihgt > 120) {
            return true;
        }

        return false;

    }





}
