package com.project.wifikeymaster.HelperClasses;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PasswordGenerator {
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String PUNCTUATION = "!$%@#";
    private boolean useLower = false;
    private boolean useUpper = false;
    private boolean useDigits = false;
    private boolean usePunctuation = false;

    public PasswordGenerator(boolean useLower, boolean useUpper, boolean useDigits, boolean usePunctuation){
        this.useLower = useLower;
        this.useUpper = useUpper;
        this.useDigits = useDigits;
        this.usePunctuation = usePunctuation;
    }

    public String generate(int length) {
        if (length <= 0) {
            return "";
        }


        StringBuilder password = new StringBuilder(length);
        Random random = new Random(System.nanoTime());

        // Collect the categories to use.
        List<String> charCategories = new ArrayList<>(4);
        if (!(useLower || useUpper || useDigits || usePunctuation)){
            charCategories.add(" ");
        }
        if (useLower) {
            charCategories.add(LOWER);
        }
        if (useUpper) {
            charCategories.add(UPPER);
        }
        if (useDigits) {
            charCategories.add(DIGITS);
        }
        if (usePunctuation) {
            charCategories.add(PUNCTUATION);
        }

        // Build the password.
        for (int i = 0; i < length; i++) {
            String charCategory = charCategories.get(random.nextInt(charCategories.size()));
            int position = random.nextInt(charCategory.length());
            password.append(charCategory.charAt(position));
        }
        return new String(password);
    }
}