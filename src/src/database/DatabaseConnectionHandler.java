package database;

import util.PrintablePreparedStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import util.Constants;
import util.SqlParser;

/**
 * This class handles all database related transactions
 * The structure and some implementations in this class are based on or inspired by the CPSC 304 Java Bank Project.
 */
public class DatabaseConnectionHandler {
	// Use this version of the ORACLE_URL if you are running the code off of the server
//	private static final String ORACLE_URL = "jdbc:oracle:thin:@dbhost.students.cs.ubc.ca:1522:stu";
	// Use this version of the ORACLE_URL if you are tunneling into the undergrad servers
	private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1522:stu";
	private static final String EXCEPTION_TAG = "[EXCEPTION]";
	private static final String WARNING_TAG = "[WARNING]";

	private Connection connection = null;
	public DatabaseConnectionHandler() {

		try {
			// Load the Oracle JDBC driver
			// Note that the path could change for new drivers
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public boolean login(String username, String password) {
		try {
			if (connection != null) {
				connection.close();
			}

			connection = DriverManager.getConnection(ORACLE_URL, username, password);
			connection.setAutoCommit(false);

			System.out.println("\nConnected to Oracle!");
			return true;
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			return false;
		}
	}

	private void rollbackConnection() {
		try  {
			connection.rollback();
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
		}
	}

	public ResultSet executeSQL(String sql) throws SQLException {
		PreparedStatement ps = connection.prepareStatement(sql);
		// ResultSet is an object representation of database instance
		ResultSet result = null;

		if (sql.trim().toLowerCase().startsWith("select")) {
			result = ps.executeQuery();
		} else {
			ps.executeUpdate();
			// TODO: Handle updates
		}

		return result;
	}

	public void databaseSetup() throws SQLException{
		// Retrieve a list of SQL queries from the Constants class
		ArrayList<String> queries = Constants.QUERIES;

		String query = "SELECT table_name FROM user_tables";

		try {
			PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);

			// Iterate through each query in the list and execute it
			for (String q : queries) {
				try {
					ps.execute(q);
				} catch (SQLException e) {
					// Print an error message if there's an issue executing a specific query
					System.out.println("Error executing " + q + e);
				}
			}
			connection.commit();
			ps.close();

		} catch (SQLException e) {
			System.out.println("Error executing " + query);
		}

	}

	/**
	 * Inserts a new tuple into the specified table with the given attribute values.
	 * @param tableName The name of the table into which the tuple will be inserted.
	 * @param attributes An array of values representing the attributes of the tuple to be inserted.
	 */
	public void insertTuple(String tableName, String[] attributes) throws SQLException {

		// Fetch attribute types for all tables from the SQL parser.
		HashMap<String, String[]> attributeTypes = SqlParser.parseAttributeTypes();

		// Retrieve the attribute types for the specific table.
		String[] types = attributeTypes.get(tableName);

		try {
			// Construct the basic INSERT query with placeholders for attributes.
			String query = "INSERT INTO " + tableName + " VALUES(";

			// Add placeholders for the attributes in the INSERT statement.
			for (int i = 0; i < attributes.length - 1; i++) {
				query += "?,";
			}
			query += "?)";

			// Create a PrintablePreparedStatement with the constructed query.
			PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);

			// Set values for each attribute in the prepared statement based on its type.
			for (int i = 0; i < attributes.length; i++) {
				if (types[i].equals("String")) {
					if (tableName.equalsIgnoreCase("Users") && i == 4) {
						String month = attributes[4].substring(0, 1).toUpperCase() + attributes[4].substring(1);
						ps.setString(i + 1, month);
					}
					else {
						ps.setString(i + 1, attributes[i]);
					}
				} else if (types[i].equals("int")) {
					int intValue = Integer.parseInt(attributes[i]);
					ps.setInt(i + 1, intValue);
				} else if (types[i].equals("float")) {
					float floatValue = Float.parseFloat(attributes[i]);
					ps.setFloat(i + 1, floatValue);
				} else {
					// Default to treating the attribute as a string if the type is not recognized.
					ps.setString(i + 1, attributes[i]);
				}
			}

			// Execute the INSERT query, commit the transaction, and close the statement.
			ps.executeUpdate();
			connection.commit();
			ps.close();

		} catch (SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to insert user!");
		}
	}

	/**
	 * Deletes a tuple from the specified table based on the given primary key values.
	 * @param tableName The name of the table from which the tuple will be deleted.
	 * @param primaryKeys An array of values representing the primary key(s) of the tuple to be deleted.
	 */
	public void deleteTuple(String tableName, String[] primaryKeys) throws SQLException {
		// Fetch primary key types and attribute names for all tables from the SQL parser.
		HashMap<String, String[]> primaryKeyTypes = SqlParser.parsePrimaryKeyTypes();
		HashMap<String, String[]> attributeNames = SqlParser.parseAttributeNames();

		// Retrieve the primary key types and attribute names for the specific table.
		String[] primaryKeyList = primaryKeyTypes.get(tableName);
		String[] attributeNameList = attributeNames.get(tableName);

		// Construct the basic DELETE query.
		String query = "DELETE FROM " + tableName + " WHERE " + attributeNameList[0] + " = ?";

		// Add conditions for additional primary key attributes in the DELETE statement (if PK has more than one attribute).
		for (int i = 1; i < primaryKeyList.length; i++) {
			query += " AND " + attributeNameList[i] + " = ?";
		}

		// Create a PrintablePreparedStatement with the constructed query.
		PrintablePreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query, false);

		// Set values for each primary key attribute in the prepared statement based on its type.
		for (int i = 0; i < primaryKeyList.length; i++) {
			if (primaryKeyList[i].equals("String")) {
				ps.setString(i + 1, primaryKeys[i]);
			} else if (primaryKeyList[i].equals("int")) {
				int intValue = Integer.parseInt(primaryKeys[i]);
				ps.setInt(i + 1, intValue);
			} else if (primaryKeyList[i].equals("float")) {
				float floatValue = Float.parseFloat(primaryKeys[i]);
				ps.setFloat(i + 1, floatValue);
			} else {
				// Default to treating the primary key attribute as a string if the type is not recognized.
				ps.setString(i + 1, primaryKeys[i]);
			}
		}

		// Execute the DELETE query, retrieve the row count affected, and commit the transaction.
		int rowCount = ps.executeUpdate();

		// Print a warning if no rows were affected by the DELETE operation (no entry with that PK).
		if (rowCount == 0) {
			connection.commit();
			ps.close();
			throw new SQLException("Entry in " + tableName + " does not exist!");
		}

		connection.commit();
		ps.close();
	}


	/**
	 * Executes a SQL SELECT query on a specified table with optional conditions.
	 * @param tableName The name of the table to select from.
	 * @param conditions An array of conditions to be applied in the WHERE clause.
	 * @param isAnd Specifies whether to use AND or OR conjunction between multiple conditions.
	 * @return A ResultSet containing the result of the SELECT query.
	 */
	public ResultSet selectTuple(String tableName, String[][] conditions, boolean isAnd) throws SQLException{
		// Retrieve attribute names for the specified table from the SQL parser.
		HashMap<String, String[]> attributeNames = SqlParser.parseAttributeNames();
		String[] attributeNameList = attributeNames.get(tableName);

		// Retrieve attribute types for the specified table from the SQL parser.
		HashMap<String, String[]> attributeTypes = SqlParser.parseAttributeTypes();
		String[] types = attributeTypes.get(tableName);

		ResultSet result = null;

		try {
			// Construct the basic SELECT query.
			String query = "SELECT * FROM " + tableName;
			int numOfConditions = 0;

			// Append conditions to the query based on the provided array.
			for (int i = 0; i < conditions.length; i++) {
				// Check if attribute at i is one we need to add to where clause
				if (conditions[i] != null && conditions[i].length > 0 && !conditions[i][0].isEmpty()) {
					// Append each of the repeated conditions to the query
					for (int j = 0; j < conditions[i].length; j++) {
						if (numOfConditions == 0) {
							query += " WHERE ";
						} else {
							if (isAnd) {
								query += " AND ";
							} else {
								query += " OR ";
							}
						}
						query += attributeNameList[i] + " = ?";
						numOfConditions++;
					}
				}
			}

			// Prepare a PreparedStatement with the constructed query.
			PreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query);

			int replaceIndex = 1;

			// Set values for the parameters in the prepared statement based on conditions and their types.
			for (int i = 0; i < conditions.length; i++) {
				if (conditions[i] != null && conditions[i].length > 0 && !conditions[i][0].isEmpty()) {
					for (int j = 0; j < conditions[i].length; j++) {
						if (types[i].equals("String")) {
							ps.setString(replaceIndex, conditions[i][j]);
						} else if (types[i].equals("int")) {
							int intValue = Integer.parseInt(conditions[i][j]);
							ps.setInt(replaceIndex, intValue);
						} else if (types[i].equals("float")) {
							float floatValue = Float.parseFloat(conditions[i][j]);
							ps.setFloat(replaceIndex, floatValue);
						} else {
							// Default to setting the parameter as a String if the type is not recognized.
							ps.setString(replaceIndex, conditions[i][j]);
						}
						replaceIndex++;
					}
				}
			}

			// Execute the query, retrieve the result set, and commit the transaction.
			ps.execute();
			result = ps.getResultSet();
			connection.commit();

		} catch(SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to find a tuple!");
		}

		// Return the result set obtained from the SELECT query.
		return result;
	}

	/**
	 * Updates a tuple in the specified table with new values based on the given primary keys.
	 * @param tableName The name of the table to update.
	 * @param primaryKeys An array of values representing the primary key(s) of the tuple to be updated.
	 * @param newValues An array of new values to be set for the attributes of the tuple.
	 */
	public void updateTuple(String tableName, String[] primaryKeys, String[] newValues) throws SQLException{
		// Retrieve attribute names for the specified table from the SQL parser.
		HashMap<String, String[]> attributeNames = SqlParser.parseAttributeNames();
		String[] attributeNameList = attributeNames.get(tableName);

		// Retrieve attribute types for the specified table from the SQL parser.
		HashMap<String, String[]> attributeTypes = SqlParser.parseAttributeTypes();
		String[] types = attributeTypes.get(tableName);

		// Retrieve primary key types for the specified table from the SQL parser.
		HashMap<String, String[]> primaryKeyTypes = SqlParser.parsePrimaryKeyTypes();
		String[] primaryKeyList = primaryKeyTypes.get(tableName);

		try {
			// Construct the basic UPDATE query.
			String query = "UPDATE " + tableName + " SET ";
			int newValueCount = 0;

			// Append new values to the SET clause in the query.
			for (int i = 0; i < newValues.length; i++) {
				if (!newValues[i].isEmpty()) {
					if (newValueCount != 0) {
						query += ",";
					}
					query += attributeNameList[i] + " = ?";
					newValueCount++;
				}
			}

			// Append the WHERE clause to the query based on primary keys.
			query += " WHERE " + attributeNameList[0] + " = ?";

			for (int i = 1; i < primaryKeyList.length; i++)
				query += " AND " + attributeNameList[i] + " = ?";

			// Create a PreparedStatement with the constructed query.
			PreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query);

			int replaceIndex = 1;

			// Set values for the parameters in the prepared statement based on new values and their types.
			for (int i = 0; i < newValues.length; i++) {
				if (!newValues[i].isEmpty()) {
					if (types[i].equals("String")) {
						ps.setString(replaceIndex, newValues[i]);
					} else if (types[i].equals("int")) {
						int intValue = Integer.parseInt(newValues[i]);
						ps.setInt(replaceIndex, intValue);
					} else if (types[i].equals("float")) {
						float floatValue = Float.parseFloat(newValues[i]);
						ps.setFloat(replaceIndex, floatValue);
					} else {
						// Default to setting the parameter as a String if the type is not recognized.
						ps.setString(replaceIndex, newValues[i]);
					}
					replaceIndex++;
				}
			}

			// Set values for the parameters in the prepared statement based on primary keys and their types.
			for (int i = 0; i < primaryKeyList.length; i++) {
				if (primaryKeyList[i].equals("String")) {
					ps.setString(replaceIndex, primaryKeys[i]);
				} else if (primaryKeyList[i].equals("int")) {
					int intValue = Integer.parseInt(primaryKeys[i]);
					ps.setInt(replaceIndex, intValue);
				} else if (primaryKeyList[i].equals("float")) {
					float floatValue = Float.parseFloat(primaryKeys[i]);
					ps.setFloat(replaceIndex, floatValue);
				} else {
					// Default to treating the primary key attribute as a string if the type is not recognized.
					ps.setString(replaceIndex, primaryKeys[i]);
				}
				replaceIndex++;
			}

			// Execute the UPDATE query, retrieve the row count affected, and commit the transaction.
			int rowCount = ps.executeUpdate();

			// Print a warning if no rows were affected by the update.
			if (rowCount == 0) {
				System.out.println(WARNING_TAG + "That row does not exist!");
			}

			connection.commit();
			ps.close();

		} catch (SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to update tuple!");
		}
	}

	/**
	 * Retrieves a projected tuple from the specified table based on the given attributes.

	 * @param tableName   The name of the table from which to retrieve the tuple.
	 * @param attributes  An array of attribute names for the projection.
	 * @return            A ResultSet containing the projected tuple, or null if an error occurs.
	 */
	public ResultSet projectionTuple(String tableName, String[] attributes) throws SQLException {
		ResultSet result = null;
		if (attributes.length == 0)
			return null;
		try {
			// Construct the SQL query by concatenating the selected attributes and the table name.
			String query = "SELECT " + attributes[0];

			for (int i = 1; i < attributes.length; i++) {
				query += ", " + attributes[i];
			}
			query += " FROM " + tableName;

			PreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query);

			// Execute the query, retrieve the ResultSet, and commit the transaction.
			System.out.println(ps);
			ps.execute();
			result = ps.getResultSet();
			connection.commit();

		} catch (SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to find those attributes!");
		}
		return result;
	}

	/**
	 * Performs an inner join operation between two tables based on the specified join attributes.
	 *
	 * @param tableName1      The name of the first table in the join operation.
	 * @param tableName2      The name of the second table in the join operation.
	 * @param joinAttributes  An array of attribute names used for joining the two tables.
	 * @return                A ResultSet containing the result of the inner join, or null if an error occurs.
	 */
	public ResultSet joinTuple(String tableName1, String tableName2, String[] joinAttributes, String starSignWhere) throws SQLException {
		ResultSet result = null;

		// Array to check if the joinAttributes have already been included in selection
		boolean[] selected = new boolean[joinAttributes.length];

		// Retrieve attribute names for the specified tables from the SQL parser.
		HashMap<String, String[]> attributeNames = SqlParser.parseAttributeNames();
		String[] attributeNameList1 = attributeNames.get(tableName1);
		String[] attributeNameList2 = attributeNames.get(tableName2);

		try {
			// Construct the SELECT part of the SQL query for the join operation by adding each table's attributes
			String query = "SELECT";

			//Add table 1's attributes
			for (int i = 0; i < attributeNameList1.length; i++) {
				if (Arrays.asList(joinAttributes).contains(attributeNameList1[i])) {
					for (int j = 0; j < joinAttributes.length; j++) {
						// Only add attributes that join tables if they haven't already been added
						if (joinAttributes[j].equals(attributeNameList1[i]) && !selected[j]) {
							selected[j] = true;
							query += " t1." + attributeNameList1[i] + " AS " + attributeNameList1[i] + ",";
						}
					}
				} else {
					query += " t1." + attributeNameList1[i] + ",";
				}
			}

			// Add table2's attributes
			for (int i = 0; i < attributeNameList2.length; i++) {
				if (Arrays.asList(joinAttributes).contains(attributeNameList2[i])) {
					for (int j = 0; j < joinAttributes.length; j++) {
						// Only add attributes that join tables if they haven't already been added
						if (joinAttributes[j].equals(attributeNameList2[i]) && !selected[j]) {
							selected[j] = true;
							query += " t2." + attributeNameList2[i] + " AS " + attributeNameList2[i] + ",";
						}
					}
				} else {
					query += " t2." + attributeNameList2[i] + ",";
				}
			}

			// Remove the last comma that was added, so it doesn't break syntax
			query = query.substring(0, query.length() - 1);

			// Construct the FROM and WHERE part of the SQL query for the join operation.
			query += " FROM " + tableName1 + " t1, " + tableName2 + " t2 WHERE t1." + joinAttributes[0] + " = t2." + joinAttributes[0];
			for (int i = 1; i < joinAttributes.length; i++) {
				query += " AND t1." + joinAttributes[i] + " = t2." + joinAttributes[i];
			}

			// If joining on StarSign, indicate which StarSign you want to view.
			if (tableName1.equalsIgnoreCase("StarSigns")) {
				query += " AND t1.StarSign = ?";
			} else if (tableName2.equalsIgnoreCase("StarSigns")) {
				query += " AND t2.StarSign = ?";
			}

			PreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query);
			ps.setString(1, starSignWhere);

			// Execute the query, retrieve the ResultSet, and commit the transaction.
			ps.execute();
			result = ps.getResultSet();
			connection.commit();

		} catch (SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to filter for those users!");
		}

		return result;
	}

	public ResultSet havingTuple(String tableName, String[] attributes, String group, String arg1, int arg2) throws SQLException {
		ResultSet result = null;

		try {
			// Construct the SQL query by concatenating the selected attributes and the table name.
			String query = "SELECT " + attributes[0];

			for (int i = 1; i < attributes.length; i++) {
				query += ", " + attributes[i];
			}
			query += " FROM " + tableName;

			query += " GROUP BY " + group;

			query += " HAVING " + arg1 + " > " + arg2;

			PreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query);

			// Execute the query, retrieve the ResultSet, and commit the transaction.
			ps.execute();
			result = ps.getResultSet();
			connection.commit();

		} catch (SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to filter for those users!");
		}
		return result;
	}

	/**
	 * Makes a sql query that groups-by a specified clause
	 *
	 * @param tableName 	base table name
	 * @param attributes 	attributes to be selected
	 * @param group 		the clause of the aggregation
	 * @return 				ResultSet
	 */
	public ResultSet groupByTuple(String tableName, String[] attributes, String group) throws SQLException {
		ResultSet result = null;

		try {
			// Construct the SQL query by concatenating the selected attributes and the table name.
			String query = "SELECT " + attributes[0];

			for (int i = 1; i < attributes.length; i++) {
				query += ", " + attributes[i];
			}
			query += " FROM " + tableName;

			query += " GROUP BY " + group;

			PreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query);

			// Execute the query, retrieve the R esultSet, and commit the transaction.
			ps.execute();
			result = ps.getResultSet();
			connection.commit();

		} catch (SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to filter for those users!");
		}
		return result;
	}

	public String nestedJoinTuple(String tableName1, String tableName2, String[] attributes, String[] joinAttributes, String[] tableNames) {
		//Add  attributes
		String query = "SELECT " + attributes[0];

		for (int i = 1; i < attributes.length; i++) {
			query += ", " + attributes[i];
		}
		if (tableName2.length() == 0) {
			query += " FROM " + tableName1;
		} else {
			query += " FROM " + tableName1 + " "+tableNames[0]+", " + tableName2 + " "+tableNames[1];
		}
		// Construct the FROM and WHERE part of the SQL query for the join operation.

		if (joinAttributes.length > 0) {
			query += " WHERE "+tableNames[0]+"." + joinAttributes[0] + " = "+tableNames[1]+"." + joinAttributes[0];
			for (int i = 1; i < joinAttributes.length; i++) {
				query += " AND "+tableNames[0]+"." + joinAttributes[i] + " = " + tableNames[1]+"."+joinAttributes[i];
			}
		}

		query = "(" + query + ")";

		return query;
	}

	public ResultSet divisionTuple(String tableName, String[] attributes, String table1, String table2) throws SQLException {
		ResultSet result = null;

		try {
			// Construct the SQL query by concatenating the selected attributes and the table name.
			String query = "SELECT " + attributes[0];

			for (int i = 1; i < attributes.length; i++) {
				query += ", " + attributes[i];
			}
			query += " FROM " + tableName;

			String division = "(" + table1 + " MINUS " + table2 + ")";

			query += " WHERE NOT EXISTS " + division;

			PreparedStatement ps = new PrintablePreparedStatement(connection.prepareStatement(query), query);

			// Execute the query, retrieve the ResultSet, and commit the transaction.
			ps.execute();
			result = ps.getResultSet();
			connection.commit();

		} catch (SQLException e) {
			// Handle SQL exception by printing an error message, rolling back the connection, and returning null.
			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
			rollbackConnection();
			throw new SQLException("Unable to filter for those users!");
		}
		return result;
	}
}
