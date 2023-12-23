package utils;

public class ValidationUtils {
    /**
     * @param userInputs details provided by the end user
     * @return true if the inputs are invalid, true otherwise
     */
    public static Boolean isUserInputInvalid(String... userInputs) {
        for (String inputStr : userInputs) {
            if (inputStr == null) {
                return true;
            }
        }
        return false;
    }

    public static String cleanInsertValue(String valueToBeInserted) {
        if (valueToBeInserted.endsWith(ConstantKeywords.comma)) {
            valueToBeInserted = valueToBeInserted.substring(0, valueToBeInserted.length() - 1);
        }
        return valueToBeInserted;
    }
}
