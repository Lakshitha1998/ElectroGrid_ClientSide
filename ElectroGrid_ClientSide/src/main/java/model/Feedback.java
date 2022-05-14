package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Feedback {
	// A common method to connect to the DB
	private Connection connect() {
		Connection con = null;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Provide the correct details: DBServer/DBName, username, password
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/electrogrid_clientside", "root", "");

			// For testing
			System.out.print("Successfully connected");

		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}

	public String readFeedback() {
		String output = "";

		try {
			Connection con = connect();
			if (con == null) {
				return "Error while connecting to the database for reading.";
			}

			// Prepare the html table to be displayed
			output = "<table border='1'><tr><th>Name</th>" + "<th>Contact Number</th><th>Email</th>"
					+ "<th>Comment</th>" + "<th>Update</th><th>Remove</th></tr>";

			String query = "select * from feedbacks";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			// iterate through the rows in the result set
			while (rs.next()) {

				String FeedbackID = Integer.toString(rs.getInt("FeedbackID"));
				String Name = rs.getString("Name");
				String Contact_Num = rs.getString("Contact_Num");
				String Email = rs.getString("Email");
				String Comment = rs.getString("Comment");

				// Add into the html table

				output += "<tr><td><input id='hidFeedbackIDUpdate' name='hidFeedbackIDUpdate' type='hidden' value='"
						+ FeedbackID + "'>" + Name + "</td>";

				output += "<td>" + Contact_Num + "</td>";
				output += "<td>" + Email + "</td>";
				output += "<td>" + Comment + "</td>";

				// buttons
				output += "<td><input name='btnUpdate' type='button' value='Update' class='btnUpdate btn btn-secondary'></td>"
						+ "<td><input name='btnRemove' type='button' value='Remove' class='btnRemove btn btn-danger' data-FeedbackID='"
						+ FeedbackID + "'>" + "</td></tr>";

			}

			con.close();

			// Complete the html table
			output += "</table>";
		} catch (Exception e) {
			output = "Error while reading the Feedback Details.";
			System.err.println(e.getMessage());
		}

		return output;
	}

	// Insert appointment
	public String insertFeedback(String Name, String Contact_Num, String Email, String Comment) {
		String output = "";

		try {
			Connection con = connect();

			if (con == null) {
				return "Error while connecting to the database";
			}

			// create a prepared statement
			String query = " insert into feedbacks (`FeedbackID`,`Name`,`Contact_Num`,`Email`,`Comment`)"
					+ " values (?, ?, ?, ?, ?)";

			PreparedStatement preparedStmt = con.prepareStatement(query);

			// binding values
			preparedStmt.setInt(1, 0);
			preparedStmt.setString(2, Name);
			preparedStmt.setString(3, Contact_Num);
			preparedStmt.setString(4, Email);
			preparedStmt.setString(5, Comment);

			// execute the statement
			preparedStmt.execute();
			con.close();

			// Create JSON Object to show successful msg.
			String newFeedback = readFeedback();
			output = "{\"status\":\"success\", \"data\": \"" + newFeedback + "\"}";
		} catch (Exception e) {
			// Create JSON Object to show Error msg.
			output = "{\"status\":\"error\", \"data\": \"Error while Inserting Feedback.\"}";
			System.err.println(e.getMessage());
		}

		return output;
	}

	// Update Feedback Details
	public String updateFeedback(String FeedbackID, String Name, String Contact_Num, String Email,
			String Comment) {
		String output = "";

		try {
			Connection con = connect();

			if (con == null) {
				return "Error while connecting to the database for updating.";
			}

			// create a prepared statement
			String query = "UPDATE feedbacks SET Name=?,Contact_Num=?,Email=?,Comment=? WHERE FeedbackID=?";

			PreparedStatement preparedStmt = con.prepareStatement(query);

			// binding values
			preparedStmt.setString(1, Name);
			preparedStmt.setString(2, Contact_Num);
			preparedStmt.setString(3, Email);
			preparedStmt.setString(4, Comment);
			preparedStmt.setInt(5, Integer.parseInt(FeedbackID));

			// execute the statement
			preparedStmt.execute();
			con.close();

			// create JSON object to show successful msg
			String newFeedback = readFeedback();
			output = "{\"status\":\"success\", \"data\": \"" + newFeedback + "\"}";
		} catch (Exception e) {
			output = "{\"status\":\"error\", \"data\": \"Error while Updating Feedback Details.\"}";
			System.err.println(e.getMessage());
		}

		return output;
	}

	public String deleteFeedback(String FeedbackID) {
		String output = "";

		try {
			Connection con = connect();

			if (con == null) {
				return "Error while connecting to the database for deleting.";
			}

			// create a prepared statement
			String query = "DELETE FROM feedbacks WHERE FeedbackID=?";

			PreparedStatement preparedStmt = con.prepareStatement(query);

			// binding values
			preparedStmt.setInt(1, Integer.parseInt(FeedbackID));
			// execute the statement
			preparedStmt.execute();
			con.close();

			// create JSON Object
			String newFeedback = readFeedback();
			output = "{\"status\":\"success\", \"data\": \"" + newFeedback + "\"}";
		} catch (Exception e) {
			// Create JSON object
			output = "{\"status\":\"error\", \"data\": \"Error while Deleting Feedback.\"}";
			System.err.println(e.getMessage());

		}

		return output;
	}

}
