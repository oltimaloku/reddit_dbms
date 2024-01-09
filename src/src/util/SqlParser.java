package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SqlParser {
    public SqlParser() {}

    public static ArrayList<String> parseSql() {
        ArrayList<String> sqlStatements = new ArrayList<>();
        String filePath = "src/src/sql/scripts/socialmedia.sql";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder currentStatement = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Append the current line to the current statement
                currentStatement.append(line.trim());

                // If the line ends with a semicolon, add the statement to the list
                if (line.trim().endsWith(";")) {
                    currentStatement.deleteCharAt(currentStatement.length() - 1);
                    sqlStatements.add(currentStatement.toString());
                    //System.out.println("SUCCESSFULLY PARSED: " + currentStatement.toString());
                    // Reset the StringBuilder for the next statement
                    currentStatement = new StringBuilder();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sqlStatements;
    }

    public static HashMap<String, String[]> parseAttributeTypes() {
        HashMap<String, String[]> retMap = new HashMap<>();
        retMap.put("Awards", new String[]{"String", "String", "float"});
        retMap.put("Comments", new String[]{"String", "String", "String", "String", "String"});
        retMap.put("HelpTickets", new String[]{"String", "String", "String", "String", "String"});
        retMap.put("Videos", new String[]{"String", "String", "int"});
        retMap.put("Images", new String[]{"String", "String"});
        retMap.put("TextPosts", new String[]{"String", "String"});
        retMap.put("Posts", new String[]{"String", "String", "String", "String", "String"});
        retMap.put("MemberOf", new String[]{"String", "String"});
        retMap.put("Attends", new String[]{"String", "String", "String"});
        retMap.put("Follows", new String[]{"String", "String"});
        retMap.put("Events", new String[]{"String", "String", "String", "String"});
        retMap.put("Users", new String[]{"String", "String", "String", "int", "String", "int"});
        retMap.put("Admins", new String[]{"String", "String", "String"});
        retMap.put("StorageSizes", new String[]{"int", "String"});
        retMap.put("Communities", new String[]{"String", "String", "String"});
        retMap.put("StarSigns", new String[]{"int", "String", "String"});
        retMap.put("Genres", new String[]{"String", "int", "String"});
        return retMap;
    }

    public static HashMap<String, String[]> parsePrimaryKeyTypes() {
        HashMap<String, String[]> retMap = new HashMap<>();
        retMap.put("Awards", new String[]{"String", "String"});
        retMap.put("Comments", new String[]{"String"});
        retMap.put("HelpTickets", new String[]{"String"});
        retMap.put("Videos", new String[]{"String"});
        retMap.put("Images", new String[]{"String"});
        retMap.put("TextPosts", new String[]{"String"});
        retMap.put("Posts", new String[]{"String"});
        retMap.put("MemberOf", new String[]{"String", "String"});
        retMap.put("Attends", new String[]{"String", "String", "String"});
        retMap.put("Follows", new String[]{"String", "String"});
        retMap.put("Events", new String[]{"String", "String"});
        retMap.put("Users", new String[]{"String"});
        retMap.put("Admins", new String[]{"String"});
        retMap.put("StorageSizes", new String[]{"int"});
        retMap.put("Communities", new String[]{"String"});
        retMap.put("StarSigns", new String[]{"int", "String"});
        retMap.put("Genres", new String[]{"String"});
        return retMap;
    }

    public static HashMap<String, String[]> parseAttributeNames() {
        HashMap<String, String[]> retMap = new HashMap<>();
        retMap.put("Awards", new String[]{"type", "postId", "cost"});
        retMap.put("Comments", new String[]{"commentId", "userName", "postId", "text", "dateTime"});
        retMap.put("HelpTickets", new String[]{"ticket", "description", "type", "userName", "adminId"});
        retMap.put("Videos", new String[]{"postId", "caption", "duration"});
        retMap.put("Images", new String[]{"postId", "caption"});
        retMap.put("TextPosts", new String[]{"postId", "text"});
        retMap.put("MemberOf", new String[]{"userName", "communityName"});
        retMap.put("Attends", new String[]{"userName", "eventName", "dateTime"});
        retMap.put("Follows", new String[]{"userName", "followsUserName"});
        retMap.put("Events", new String[]{"eventName", "dateTime", "location", "communityName"});
        retMap.put("Users", new String[]{"userName", "biography", "firstName", "birthDay", "birthMonth", "birthYear"});
        retMap.put("Admins", new String[]{"adminId", "name", "experienceLevel"});
        retMap.put("StorageSizes", new String[]{"duration", "size_type"});
        retMap.put("Communities", new String[]{"name", "description", "genre"});
        retMap.put("StarSigns", new String[]{"birthDay", "birthMonth", "starSign"});
        retMap.put("Genres", new String[]{"genre", "capacity", "ageRange"});
        return retMap;
    }

    public static String getStarSign(String inputMonth) {
        String month = inputMonth.substring(0, 1).toUpperCase() + inputMonth.substring(1);
        switch(month) {
            case "January":
                return "Aquarius";
            case "February":
                return "Pisces";
            case "March":
                return "Aries";
            case "April":
                return "Taurus";
            case "May":
                return "Gemini";
            case "June":
                return "Cancer";
            case "July":
                return "Leo";
            case "August":
                return "Virgo";
            case "September":
                return "Libra";
            case "October":
                return "Scorpio";
            case "November":
                return "Sagittarius";
            case "December":
                return "Capricorn";
            default:
                return "Aries";
        }
    }

}
