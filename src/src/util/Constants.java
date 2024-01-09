package util;

import java.util.ArrayList;
import java.util.HashMap;

import util.SqlParser;

public class Constants {

    private Constants() {}
    private static final SqlParser parser = new SqlParser();

    public static final String APP_NAME = "Social Media";

    //THE ORDER OF THIS ARRAY MATTERS! Tables are dropped in this order, so we need to drop the
    // tables with no foreign keys first otherwise it will fail to drop the tables with
    // foreign keys as the parent table still exists.
    public static final String[] TABLE_NAMES =
            {
                    "Awards",
                    "Comments",
                    "HelpTickets",
                    "Videos",
                    "Images",
                    "TextPosts",
                    "Posts",
                    "MemberOf",
                    "Attends",
                    "Follows",
                    "Events",
                    "Users",
                    "Admins",
                    "StorageSizes",
                    "Communities",
                    "StarSigns",
                    "Genres"
            };
    public static final ArrayList<String> QUERIES = parser.parseSql();

    public static final HashMap<String, String[]> ATTRIBUTE_TYPES = parser.parseAttributeTypes();

}
