package com.example.qr_go.QR;

public class Avatar {
    /**
     * An array of strings representing different face shapes.
     */
    private static final String[] faceShapes = {
            "  ( o_O )   ",
            "  ( o.o )  ",
            "  ( >.< )  ",
            " @( o_o )@  ",
            "  ( ._. )   ",
            "  ( Q_Q )   ",
            "  ( O O )   ",
            "  ( -_- )   ",
            "  ( ^_^ )   ",
            "  ( >_< )   ",
            "  (* > *)  "
    };

    /**
     * An array of strings representing different body features.
     */
    private static final String[] bodyFeatures = {
            "  |   |  ",
            "  |===|  ",
            "  |___|  ",
            "  /   \\  ",
            "  [   ]  ",
            "  {   }  ",
            "  (   )  ",
            "  \\   /  ",
            "  | o |  ",
            "  / o \\  ",
            "  ) . (  "
    };
    /**
     * An array of strings representing different leg features.
     */
    private static final String[] legFeatures = {
            "/   \\",
            "|   |",
            "| | |",
            "\\___/",
            "|-|-|",
            "/ | \\",
            "|___|",
            "| . |",
            "\\   /",
            "|/|\\|",
            "|>|<|"
    };
    /**
     * An array of strings representing different hat features.
     */
    private static final String[] hatFeatures = {
            "   ___   ",
            "  /   \\  ",
            "  \\___/  ",
            "  /___\\  ",
            "  \\___/  ",
            "   |||   ",
            "  (___)  ",
            "  [___]  ",
            "  _/ \\_  ",
            "  |^^^|  ",
            "  |_|_|_|  ",
            "  ooooo  "
    };
    /**
     * An array of strings representing different shoe features.
     */
    private static final String[] shoeFeatures = {
            "<_|_>",
            ">_|_<",
            "(_|_)",
            "[_|_]",
            "{_|_}",
            "\\_|_/",
            "....."
    };

    /**
     * Generates an avatar based on a given hash.
     * @param hash a string representing the hash to use to generate the avatar
     * @return a string representing the generated avatar
     */
    public String generateAvatar(String hash) {
        String hex = hash.substring(0, 10);

        String faceShape = faceShapes[Integer.parseInt(hex.substring(0, 2), 16) % faceShapes.length];
        String hat = hatFeatures[Integer.parseInt(hex.substring(2, 4), 16) % hatFeatures.length];
        String body = bodyFeatures[Integer.parseInt(hex.substring(4, 6), 16) % bodyFeatures.length];
        String legs = legFeatures[Integer.parseInt(hex.substring(6, 8), 16) % legFeatures.length];
        String shoes = shoeFeatures[Integer.parseInt(hex.substring(8, 10), 16) % shoeFeatures.length];

        int maxLength = Math.max(Math.max(faceShape.length(), hat.length()), Math.max(body.length(), Math.max(legs.length(), shoes.length())));

        int hatPadding = (maxLength - hat.length()) / 2;
        int facePadding = (maxLength - faceShape.length()) / 2;
        int bodyPadding = (maxLength - body.length()) / 2;
        int legsPadding = (maxLength - legs.length()) / 2;
        int shoesPadding = (maxLength - shoes.length()) / 2;

        String avatar = "";
        avatar += padString(hat, hatPadding) + "\n";
        avatar += padString(faceShape, facePadding) + "\n";
        avatar += padString(body, bodyPadding) + "\n";
        avatar += padString(legs, legsPadding) + "\n";
        avatar += padString(shoes, shoesPadding);

        return avatar;
    }

    /**
     * Pads a given string with spaces to a given length.
     * @param str the string to pad
     * @param padding the number of spaces to add to the left and right of the string
     * @return a padded string
     */
    private String padString(String str, int padding) {
        return " ".repeat(Math.max(0, padding)) + str;
    }
}
