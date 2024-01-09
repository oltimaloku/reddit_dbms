package ui;

import database.DatabaseConnectionHandler;
import util.Constants;
import util.SqlParser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class UI {

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardsPanel = new JPanel(cardLayout);
    private JFrame frame;
    private DatabaseConnectionHandler databaseConnectionHandler;
    private JTable table;
    private JTextField textField;

    private JTextField userNameField;

    private JTextField firstNameField;

    private JTextField bioField;

    private JTextField birthDayField;

    private JTextField birthMonthField;

    private JTextField birthYearField;

    public UI(DatabaseConnectionHandler dbHandler) {
        this.databaseConnectionHandler = dbHandler;
    }

    public void createAndShowGUI() {
        // FRAME
        frame = new JFrame(Constants.APP_NAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 1000);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createMenuItem("Home", "homeCard"));
        menuBar.add(createMenuItem("Projection", "projectionCard"));
        menuBar.add(createMenuItem("Search Users", "selectionCard"));
        menuBar.add(createMenuItem("Admin Manager", "deleteCard"));
        menuBar.add(createMenuItem("Finder", "findCard"));
        menuBar.add(createMenuItem("Astrology Search", "joinCard"));
        menuBar.add(createMenuItem("Create User", "insertCard"));
        menuBar.add(createMenuItem("Update User", "updateCard"));
        frame.setJMenuBar(menuBar);

        cardsPanel.add(createPanel("Home Card"), "homeCard");
        cardsPanel.add(createPanel("Projection Card"), "projectionCard");
        cardsPanel.add(createPanel("Selection Card"), "selectionCard");
        cardsPanel.add(createPanel("Delete Card"), "deleteCard");
        cardsPanel.add(createPanel("Find Card"), "findCard");
        cardsPanel.add(createPanel("Join Card"), "joinCard");
        cardsPanel.add(createPanel("Insert Card"), "insertCard");
        cardsPanel.add(createPanel("Update Card"), "updateCard");

        frame.getContentPane().add(cardsPanel, BorderLayout.CENTER);

        // SCROLLABLE TABLE
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(scrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private JMenu createMenuItem(String title, String cardName) {
        JMenu menu = new JMenu(title);
        JMenuItem menuItem = new JMenuItem("View " + title);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardsPanel, cardName);
                if (cardName.equals("deleteCard")) {
                    selectAll("Admins");
                } else if (cardName.equals("insertCard")) {
                    selectAll("Users");
                } else if (cardName.equals("updateCard")) {
                    selectAll("Users");
                }
            }
        });
        menu.add(menuItem);
        return menu;
    }

    private JPanel createPanel(String cardName) {
        JPanel panel = new JPanel();

        switch (cardName) {
            case "Home Card":
                panel.setLayout(new FlowLayout());
//                JLabel label = new JLabel("Enter SQL: ");
//                panel.add(label);
//
//                textField = new JTextField(20);
//                panel.add(textField);
//
//                JButton executeSqlButton = new JButton("Execute SQL");
//                executeSqlButton.addActionListener(e -> executeSqlCommand());
//                panel.add(executeSqlButton);
//
//                JButton resetDbButton = new JButton("Reset Database");
//                resetDbButton.addActionListener(e -> resetDb());
//                panel.add(resetDbButton);

                JLabel label = new JLabel("Welcome to SocialMedia DB Manager!");
                panel.add(label);
                break;

            case "Projection Card":
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                HashMap<String, String[]> attributes = SqlParser.parseAttributeNames();
                String[] tableNames = new String[attributes.keySet().size()];
                int index = 0;
                for (String key : attributes.keySet()) {
                    tableNames[index++] = key;
                }

                // Label
                JLabel projectionInstruction = new JLabel("Select the relation and its attributes you would like to project from");
                panel.add(projectionInstruction);

                // Dropdown menu for tables
                JComboBox<String> comboBox = new JComboBox<>(tableNames);
                JPanel checkBoxPanel = new JPanel();
                checkBoxPanel.setLayout(new FlowLayout());
                ArrayList<JCheckBox> checkBoxes = new ArrayList<>(); // List to store checkboxes

                comboBox.addActionListener(e -> {
                    String selectedItem = (String) comboBox.getSelectedItem();

                    // Clear existing checkboxes and list
                    checkBoxPanel.removeAll();
                    checkBoxes.clear();

                    // Create new checkboxes for each attribute
                    String[] selectedItemAttributes = attributes.get(selectedItem);
                    if (selectedItemAttributes != null) {
                        for (String attr : selectedItemAttributes) {
                            JCheckBox checkBox = new JCheckBox(attr);
                            checkBoxPanel.add(checkBox);
                            checkBoxes.add(checkBox); // Add to list
                        }
                    }

                    checkBoxPanel.revalidate();
                    checkBoxPanel.repaint();
                });

                // Submit button
                JButton projectionSubmit = new JButton("Submit");
                projectionSubmit.addActionListener(e -> {
                    String selectedRelation = (String) comboBox.getSelectedItem();
                    ArrayList<String> selectedAttributes = new ArrayList<>();
                    for (JCheckBox checkBox : checkBoxes) {
                        if (checkBox.isSelected()) {
                            selectedAttributes.add(checkBox.getText());
                        }
                    }
                    System.out.println(selectedAttributes);
                    testProjection(selectedRelation, selectedAttributes.toArray(new String[0]));
                });

                panel.add(comboBox);
                panel.add(checkBoxPanel);
                panel.add(projectionSubmit);
                break;

            case "Selection Card":
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                JLabel selectionLabel = new JLabel("Select users where");
                panel.add(selectionLabel);

                // Panel to contain condition panels
                JPanel conditionsContainer = new JPanel();
                conditionsContainer.setLayout(new BoxLayout(conditionsContainer, BoxLayout.Y_AXIS));
                panel.add(conditionsContainer);

                List<JLabel> andOrLabels = new ArrayList<>(); // List to store the AND/OR labels

                // CONDITION
                ActionListener addConditionAction = e -> {
                    JPanel newConditionPanel = new JPanel();
                    JComboBox attributeDropdown = new JComboBox(SqlParser.parseAttributeNames().get("Users"));
                    newConditionPanel.add(attributeDropdown);
                    newConditionPanel.add(new JLabel("="));
                    newConditionPanel.add(new JTextField(20));

                    JLabel andOrLabel = new JLabel("AND");
                    newConditionPanel.add(andOrLabel);
                    andOrLabels.add(andOrLabel);

                    conditionsContainer.add(newConditionPanel);
                    panel.revalidate();
                    panel.repaint();
                };

                // And/Or Dropdown
                JComboBox<String> andOrDropdown = new JComboBox<>(new String[]{"AND", "OR"});
                andOrDropdown.setPreferredSize(new Dimension(100, 30));
                JPanel dropdownPanel = new JPanel();
                dropdownPanel.add(andOrDropdown);
                andOrDropdown.addActionListener(e -> {
                    String selected = (String) andOrDropdown.getSelectedItem();
                    for (JLabel label1 : andOrLabels) {
                        label1.setText(selected);
                    }
                    panel.revalidate();
                    panel.repaint();
                });

                // ADD + Button adds another condition
                JButton addConditionButton = new JButton("Add Condition +");
                addConditionButton.addActionListener(addConditionAction);

                // Add the dropdown and button at the bottom of the main panel
                panel.add(dropdownPanel);
                panel.add(addConditionButton);

                JButton submitButton = new JButton("Submit Selection");
                submitButton.setForeground(Color.blue);
                submitButton.addActionListener(e -> {
                    List<String[]> conditionsList = new ArrayList<>();
                    Component[] conditionPanels = conditionsContainer.getComponents();

                    for (Component component : conditionPanels) {
                        if (component instanceof JPanel) {
                            // Retrieve all components inside the condition panel (dropdown, textfield)
                            Component[] components = ((JPanel) component).getComponents();

                            String attributeName = null;
                            String conditionValue = null;

                            for (Component c : components) {
                                // Checks if component is a dropdown, if so it sets the attribute name
                                if (c instanceof JComboBox<?>) {
                                    attributeName = (String)((JComboBox<?>) c).getSelectedItem();
                                }
                                // If component is textfield, set condition value
                                else if (c instanceof JTextField) {
                                    conditionValue = ((JTextField) c).getText();
                                }
                            }

                            // If an attribute name and a condition value are set,
                            // add as a pair to the conditions list
                            if (attributeName != null && conditionValue != null && !conditionValue.isEmpty()) {
                                conditionsList.add(new String[]{attributeName, conditionValue});
                            }
                        }
                    }
                    boolean isAnd = andOrDropdown.getSelectedItem() == "AND";
                    selectUsersWhere(conditionsList, isAnd);

                });
                panel.add(submitButton);
                break;
            case "Delete Card":
                // TODO: Refresh panel after deleting admin
                panel.add(new JLabel("Enter the id of the admin you would like to delete: "));
                JTextField deleteAdminId = new JTextField(5);
                panel.add(deleteAdminId);
                JButton deleteButton = new JButton("Delete selected Admin");
                deleteButton.addActionListener(e -> {
                    testDelete(deleteAdminId.getText());
                    selectAll("Admins");
                });
                panel.add(deleteButton);

                break;
            case "Find Card":
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                // AGGREGATION GROUP BY
                JPanel aggGroupByPanel = new JPanel();
                aggGroupByPanel.add(new JLabel("Find the number of users per birth year"));
                JButton userPerBirthYearButton = new JButton("Find");
                userPerBirthYearButton.addActionListener(e -> testGroupBy());
                aggGroupByPanel.add(userPerBirthYearButton);
                panel.add(aggGroupByPanel);

                // AGGREGATION HAVING
                JPanel aggHavingPanel = new JPanel();
                aggHavingPanel.add(new JLabel("Find the popular event locations >"));
                JTextField numberOfEvents = new JTextField("1",5);
                aggHavingPanel.add(numberOfEvents);
                JButton popularEventLocationsButton = new JButton("Find");
                popularEventLocationsButton.addActionListener(e -> testHaving(numberOfEvents.getText()));
                aggHavingPanel.add(popularEventLocationsButton);
                panel.add(aggHavingPanel);

                // NESTED AGGREGATION GROUP BY
                JPanel nestedAggGroupByPanel = new JPanel();
                nestedAggGroupByPanel.add(new JLabel("Find the number of users per starsign"));
                JButton usersPerStartSignButton = new JButton("Find");
                usersPerStartSignButton.addActionListener(e -> testNestedGroupBy());
                nestedAggGroupByPanel.add(usersPerStartSignButton);
                panel.add(nestedAggGroupByPanel);

                // DIVISION
                JPanel divisionPanel = new JPanel();
                divisionPanel.add(new JLabel("Find the users who find attended all events"));
                JButton usersAllEventsButton = new JButton("Find");
                usersAllEventsButton.addActionListener(e -> testDivision());
                divisionPanel.add(usersAllEventsButton);
                panel.add(divisionPanel);

            case "Join Card":
                panel.setLayout(new FlowLayout());
                panel.add(new JLabel("Find users with starsign"));
                String[] starSigns = {
                        "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
                        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
                };
                JComboBox starSignSelector = new JComboBox(starSigns);
                panel.add(starSignSelector);

                JButton starSignSubmitButton = new JButton("Submit");
                starSignSubmitButton.addActionListener(e -> testJoin((String) starSignSelector.getSelectedItem()));
                panel.add(starSignSubmitButton);
                break;
            case "Insert Card":
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(new JLabel("Create a new user!"));
                JPanel attributeTextFieldPanel = new JPanel();

                attributeTextFieldPanel.add(new JLabel("username: "));
                JTextField usernameField = new JTextField(10);
                attributeTextFieldPanel.add(usernameField);

                attributeTextFieldPanel.add(new JLabel("biography: "));
                JTextField biographyField = new JTextField(10);
                attributeTextFieldPanel.add(biographyField);

                attributeTextFieldPanel.add(new JLabel("firstName: "));
                JTextField firstNameField = new JTextField(10);
                attributeTextFieldPanel.add(firstNameField);

                attributeTextFieldPanel.add(new JLabel("birthDay: "));
                JTextField birthDayField = new JTextField(10);
                attributeTextFieldPanel.add(birthDayField);

                attributeTextFieldPanel.add(new JLabel("birthMonth: "));
                JTextField birthMonthField = new JTextField(10);
                attributeTextFieldPanel.add(birthMonthField);

                attributeTextFieldPanel.add(new JLabel("birthYear: "));
                JTextField birthYearField = new JTextField(10);
                attributeTextFieldPanel.add(birthYearField);

                JPanel submitPanel = new JPanel();
                JButton submitUserButton = new JButton("Create User");
                submitUserButton.addActionListener(e-> {
                    String[] userAttributes = new String[6];
                    userAttributes[0] = usernameField.getText();
                    userAttributes[1] = biographyField.getText();
                    userAttributes[2] = firstNameField.getText();
                    userAttributes[3] = birthDayField.getText();
                    userAttributes[4] = birthMonthField.getText();
                    userAttributes[5] = birthYearField.getText();
                    insertUser(userAttributes);

                });
                submitPanel.add(submitUserButton);

                panel.add(attributeTextFieldPanel);
                panel.add(submitPanel);
                break;

            case "Update Card":
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                panel.add(new JLabel("Enter the username of the user you would like to update: "));
                JTextField username = new JTextField(10);
                panel.add(username);

                // Panel for attributes to update
                JPanel attributesToUpdate = new JPanel();
                attributesToUpdate.setLayout(new BoxLayout(attributesToUpdate, BoxLayout.Y_AXIS));

                // Biography
                attributesToUpdate.add(new JLabel("Biography: "));
                JTextField biographyTextField = new JTextField(10);
                attributesToUpdate.add(biographyTextField);

                // First Name
                attributesToUpdate.add(new JLabel("First Name: "));
                JTextField firstNameTextField = new JTextField(10);
                attributesToUpdate.add(firstNameTextField);

                // Birth Day
                attributesToUpdate.add(new JLabel("Birth Day (Integer): "));
                JTextField birthDayTextField = new JTextField(10);
                attributesToUpdate.add(birthDayTextField);

                // Birth Month
                attributesToUpdate.add(new JLabel("Birth Month: "));
                JTextField birthMonthTextField = new JTextField(10);
                attributesToUpdate.add(birthMonthTextField);

                // Birth Year
                attributesToUpdate.add(new JLabel("Birth Year (Integer): "));
                JTextField birthYearTextField = new JTextField(10);
                attributesToUpdate.add(birthYearTextField);

                JButton submitUpdateButton = new JButton("Submit Update");

                submitUpdateButton.addActionListener(e -> {
                    updateUser(biographyTextField.getText(), firstNameTextField.getText(), birthDayTextField.getText(), birthMonthTextField.getText(), birthYearTextField.getText(), username.getText());
                    selectAll("Users");
                });
                // Add the attributes panel to the main panel
                panel.add(attributesToUpdate);
                panel.add(submitUpdateButton);
                break;
            default :
                // Optionally handle an unknown card name
            break;
        }

        return panel;
    }

    public void updateUser(String biography, String firstName, String birthDay, String birthMonth, String birthYear, String username) {
        String starSign = SqlParser.getStarSign(birthMonth);
        try {
            databaseConnectionHandler.insertTuple("StarSigns", new String[]{birthDay, birthMonth, starSign});
            databaseConnectionHandler.updateTuple("Users", new String[]{username}, new String[]{username, biography, firstName, birthDay, birthMonth, birthYear});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + e.getMessage());
        }
    }

    private void insertUser(String[] args) {
        String month = args[4].substring(0, 1).toUpperCase() + args[4].substring(1);
        String starSign = SqlParser.getStarSign(month);
        try {
            databaseConnectionHandler.insertTuple("StarSigns", new String[]{args[3], month, starSign});
        }
        catch(SQLException e) {
            System.out.println("Not adding starsign to table");
        } try {
            databaseConnectionHandler.insertTuple("Users", args);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + e.getMessage());
        }
        selectUsersWhere(new ArrayList<String[]>(), false);
    }

    private void testJoin(String starSign) {
        try {
            ResultSet result = databaseConnectionHandler.joinTuple("Users", "StarSigns", new String[]{"birthDay", "birthMonth"}, starSign);
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    private void testDelete(String primaryKey) {
        try {
            databaseConnectionHandler.deleteTuple("Admins", new String[]{primaryKey});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, e);
        }

    }

    private void selectUsersWhere(List<String[]> conditions, boolean isAnd) {
        List<String>[] conditionsMapped = new ArrayList[6];
        for (int i = 0; i < conditionsMapped.length; i++) {
            conditionsMapped[i] = new ArrayList<>();
        }

        for (String[] condition : conditions) {
            if (condition.length < 2) continue; // Skip if the condition is not correctly formed

            switch (condition[0]) {
                case "userName":
                    conditionsMapped[0].add(condition[1]);
                    break;
                case "biography":
                    conditionsMapped[1].add(condition[1]);
                    break;
                case "firstName":
                    conditionsMapped[2].add(condition[1]);
                    break;
                case "birthDay":
                    conditionsMapped[3].add(condition[1]);
                    break;
                case "birthMonth":
                    conditionsMapped[4].add(condition[1]);
                    break;
                case "birthYear":
                    conditionsMapped[5].add(condition[1]);
                    break;
                default:
                    // Handle or log unrecognized attribute
                    break;
            }
        }

        // Determine the length of the longest list in conditionsMapped
        int maxLength = 0;
        for (List<String> conditionList : conditionsMapped) {
            maxLength = Math.max(maxLength, conditionList.size());
        }

        // Initialize the String[][] with the dimensions
        String[][] conditionsArray = new String[conditionsMapped.length][];

        // Populate the String[][] with values from conditionsMapped
        for (int i = 0; i < conditionsMapped.length; i++) {
            List<String> conditionList = conditionsMapped[i];
            if (conditionList.isEmpty()) {
                // If no conditions for this attribute, initialize an empty array
                conditionsArray[i] = new String[0];
            } else {
                conditionsArray[i] = conditionList.toArray(new String[0]);
            }
        }

        try {
            ResultSet result = databaseConnectionHandler.selectTuple("Users", conditionsArray, isAnd);
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    private void testHaving(String argument) {
        try {
            ResultSet result = databaseConnectionHandler.havingTuple("Events",new String[]{"location", "COUNT(*) AS numberOfEvents"}, "location", "COUNT(*)", Integer.parseInt(argument));
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    private void testGroupBy() {
        try {
            ResultSet result = databaseConnectionHandler.groupByTuple("Users", new String[]{"birthYear", "count(*) AS num"}, "birthYear");
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    private void testNestedGroupBy() {
        try {
            String nestedJoin = databaseConnectionHandler.nestedJoinTuple("Users", "StarSigns", new String[]{"starSign"}, new String[]{"birthDay", "birthMonth"}, new String[]{"t1", "t2"});
            ResultSet result = databaseConnectionHandler.groupByTuple(nestedJoin, new String[]{"starSign", "count(*) AS num"}, "starSign");
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    private void testDivision() {
        try {
            String nestedSelect = databaseConnectionHandler.nestedJoinTuple("Events", "", new String[]{"eventName", "dateTime"}, new String[]{}, new String[]{"t1", "t2"});
            String nestedJoin = databaseConnectionHandler.nestedJoinTuple("Attends", "", new String[]{"eventName", "dateTime"}, new String[]{"userName"}, new String[]{"Attends", "Users"});
            ResultSet result = databaseConnectionHandler.divisionTuple("Users",new String[]{"userName"}, nestedSelect, nestedJoin);
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    private void selectAll(String tableName) {
        try {

            ResultSet result = databaseConnectionHandler.selectTuple(tableName, new String[][]{{"", "", ""}}, false);
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }


    private void testProjection(String tableName, String[] attributes) {
        try {
            ResultSet result = databaseConnectionHandler.projectionTuple(tableName, attributes);
            if (result != null) {
                displayResultSetInTable(result);
            } else {
                // Opens popup in GUI
                JOptionPane.showMessageDialog(frame, "result is null");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    // Reset database to default state with example tuples
    public void resetDb() {
        try {
            databaseConnectionHandler.databaseSetup();
            JOptionPane.showMessageDialog(frame, "Successfully reset database to default state!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + ex.getMessage());
        }
    }

    // Populates table with result set
    private void displayResultSetInTable(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        int columnCount = metaData.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        // .next iterates over each row
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                row.add(rs.getObject(columnIndex));
            }
            data.add(row);
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        table.setModel(model);
        rs.close();
    }
}
