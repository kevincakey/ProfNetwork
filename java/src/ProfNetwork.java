//Kevin Gao 862138776
//Aditi Behera 862138359
/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class ProfNetwork {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of ProfNetwork
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public ProfNetwork (String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end ProfNetwork

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
	 if(outputHeader){
	    for(int i = 1; i <= numCol; i++){
		System.out.print(rsmd.getColumnName(i) + "\t");
	    }
	    System.out.println();
	    outputHeader = false;
	 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
          List<String> record = new ArrayList<String>();
         for (int i=1; i<=numCol; ++i)
            record.add(rs.getString (i));
         result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       if(rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            ProfNetwork.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      ProfNetwork esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the ProfNetwork object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new ProfNetwork (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Goto Friend List");
                System.out.println("2. Update Profile");
                System.out.println("3. Write a new message");
                System.out.println("4. Send Friend Request");
                System.out.println("5. Change Your Password");
                System.out.println("6. Search For People");
                System.out.println("7. Respond to Connections");
                System.out.println("8. View Your Messages");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: FriendList(esql,authorisedUser); break;
                   case 2: UpdateProfile(esql, authorisedUser); break;
                   case 3: NewMessage(esql, authorisedUser); break;
                   case 4: SendRequest(esql, authorisedUser); break;
                   case 5: changePassword(esql,authorisedUser); break;
                   case 6: searchPeople(esql, authorisedUser); break;
                   case 7: connectionResponse(esql, authorisedUser); break;
                   case 8: viewMessages(esql, authorisedUser); break;
                   case 9: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user with privided login, passowrd and phoneNum
    * An empty block and contact list would be generated and associated with a user
    **/
   public static void CreateUser(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();
         System.out.print("\tEnter user email: ");
         String email = in.readLine();

	 //Creating empty contact\block lists for a user
	 String query = String.format("INSERT INTO USR (userId, password, email) VALUES ('%s','%s','%s')", login, password, email);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end

   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(ProfNetwork esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USR WHERE userId = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here
public static void FriendList(ProfNetwork esql, String authorisedUser){
   try{
      System.out.println();
      String query1 = String.format("SELECT connectionId FROM CONNECTION_USR WHERE userId = '%s' AND status = 'Accept'", authorisedUser);
      esql.executeQueryAndPrintResult(query1);
      String query2 = String.format("SELECT userId FROM CONNECTION_USR WHERE connectionId = '%s' AND status = 'Accept'", authorisedUser);
      esql.executeQueryAndPrintResult(query2);
      System.out.println();
      //System.out.print("\t 1 - Enter 1 to see someone's friend list -q to quit-: ");
      /*
      String input = " ";
      int connectionLevelCounter = 0;
      System.out.println("---------");
      System.out.println("\tFriends List Options: ");
      System.out.println("\t 1 - Enter 1 to find friends: ");
      System.out.println("\t 2 - Enter 2 to send a message to this user: ");
      if(connectionLevelCounter <= 3) System.out.println("\t 3 - Enter 3 to send a connection request to this user: ");
      System.out.println("\t q - Enter q to quit-: ");
      input = in.readLine();

      while(input != "q"){

         if(input == "1"){ //have to implement profile view 
            System.out.println("\t y - to search for a friend"); 
            System.out.println("\t n - Enter n to quit-: ");
            String response = in.readLine();
            if(response == "y"){
               System.out.println("\tEnter userid of friend to view their profile: ");
               String find = in.readLine();
               String query3 = String.format("SELECT connectionId FROM CONNECTION_USR WHERE userId = '%s' AND status = 'Accept'", find);
               esql.executeQueryAndPrintResult(query3);
               String query4 = String.format("SELECT userId FROM CONNECTION_USR WHERE connectionId = '%s' AND status = 'Accept'", find);
               esql.executeQueryAndPrintResult(query4);
               viewProfile(esql, find);
               //connectionLevelCounter++;
            }
         }
         else if(input == "2"){
            NewMessage(esql, authorisedUser);
         }
         else if(input == "3" && connectionLevelCounter <= 3){
            SendRequest(esql, authorisedUser);
         }
         System.out.println("---------");
         System.out.println("\tFriends List Options: ");
         System.out.println("\t 1 - Enter 1 to find friends: ");
         System.out.println("\t 2 - Enter 2 to send a message to this user: ");
         if(connectionLevelCounter <= 3) System.out.println("\t 3 - Enter 3 to send a connection request to this user: ");
         System.out.println("\t q - Enter q to quit-: ");
         input = in.readLine();
      }*/
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}
public static void UpdateProfile(ProfNetwork esql, String authorisedUser){
   try{
      System.out.print("\tEnter Full Name: ");
      String fullName = in.readLine();
      System.out.print("\tEnter date of birth YYYY-MM-DD: ");
      String dateOfBirth = in.readLine();

      String query = String.format("UPDATE USR SET name ='" + fullName + "', dateOfBirth='" + dateOfBirth + "' WHERE userId ='" + authorisedUser + "';");//authorisedUser; //possible syntax?
      esql.executeUpdate(query);
/*
      System.out.print("\tEnter Insititution Name: ");
      String institutionName = in.readLine();
      System.out.print("\tEnter major: ");
      String major = in.readLine();
      System.out.print("\tEnter degree: ");
      String degree = in.readLine();
      System.out.print("\tEnter startdate: ");
      String startdate = in.readLine();
      System.out.print("\tEnter enddate: ");
      String enddate = in.readLine();
      //possible error if there is a new entry and we arent supposed to use update.
      String query1 = String.format("UPDATE EDUCATIONAL_DETAILS SET instituitionName = '%s', major = '%s', degree= '%s', startdate='%s', enddate='%s' WHERE userId = '%s'", institutionName, major, degree, startdate, enddate, authorisedUser);
      esql.executeUpdate(query1);

      System.out.print("\tEnter company Name: ");
      String company = in.readLine();
      System.out.print("\tEnter role: ");
      String role = in.readLine();
      System.out.print("\tEnter location: ");
      String location = in.readLine();
      System.out.print("\tEnter startDate: ");
      String startDate = in.readLine();
      System.out.print("\tEnter endDate: ");
      String endDate = in.readLine();
      //possible error if there is a new entry and we arent supposed to use update.
      String query2 = String.format("UPDATE WORK_EXPR(userId, company, role, location, startDate, endDate) VALUES ('%s','%s','%s','%s','%s','%s')", authorisedUser, company, role, location, startDate, endDate);
      esql.executeUpdate(query2);
*/
      System.out.println ("User profile updated!");
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}
public static void NewMessage(ProfNetwork esql, String authorisedUser){ //trigger sequence for making message id
   try{
      int messid = 27812;
      System.out.print("\t Enter the userID to send this message to: ");
      String receiver = in.readLine();
      String query = String.format("SELECT * FROM USR WHERE userId = '%s'", receiver);
      int recipientExists = esql.executeQuery(query);
      if (recipientExists > 0) {
         Timestamp timestamp = new Timestamp(System.currentTimeMillis());
         System.out.print("\tEnter message: ", receiver);
         String message = in.readLine();
         message = message.replace("'", "''");

         query = String.format("INSERT INTO MESSAGE (msgId, senderId, receiverId, contents, sendTime, deleteStatus, status) VALUES ('%s','%s','%s','%s', '%s', '%d', '%s')", messid, authorisedUser, receiver, message, timestamp.toString(), 0, "Sent");
         esql.executeUpdate(query);
         messid++;
         System.out.println();
      }
      else {
         System.out.println("Error: User could not be found.");
      }
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}
public static void SendRequest(ProfNetwork esql, String authorisedUser){ //might want to include a nested selection check for the 3rd level connection req
   try{
      System.out.print("\t Enter the userID to send this connection request to: ");
      String connectingTo = in.readLine();
      String query = String.format("INSERT INTO CONNECTION_USR (userId, connectionId, status) VALUES ('%s','%s','Request')", authorisedUser, connectingTo);
      esql.executeUpdate(query);
      System.out.println("Request sent");
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}

public static void changePassword(ProfNetwork esql, String authorisedUser){
   try{
      System.out.print("\t Enter new password: ");
      String newPassword = in.readLine();
      //String query = String.format("UPDATE USR(Password) VALUES ('%s')", newPassword); //update syntax might be wrong
      //System.out.println("Authorised User: " + authorisedUser);
      String query = String.format("UPDATE USR SET password = '" + newPassword + "' WHERE userId = '" + authorisedUser + "';");
      esql.executeUpdate(query);
      System.out.println();
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}

public static void searchPeople(ProfNetwork esql, String authorisedUser){
   try{
      System.out.print("\tPlease enter name of the user you are searching for: ");
      String userName = in.readLine();
      String query = String.format("SELECT name FROM USR WHERE name LIKE '%%%s%%'", userName);

      esql.executeQueryAndPrintResult(query);
      System.out.println();
   }catch(Exception e){
      System.err.println(e.getMessage());	
   }
}

public static void viewProfile(ProfNetwork esql, String user){
   try{
      String query = String.format("SELECT userId, email, name, dateOfBirth FROM USR WHERE UserId = '%s'", user);
      esql.executeQueryAndPrintResult(query);
      System.out.println();
      query = String.format("SELECT * FROM EDUCATIONAL_DETAILS WHERE userid = '%s'", user);
      esql.executeQueryAndPrintResult(query);
      System.out.println();
      query = String.format("SELECT * FROM WORK_EXPR WHERE userId = '%s'", user);
      esql.executeQueryAndPrintResult(query);
      System.out.println();
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}

public static void connectionResponse(ProfNetwork esql, String authorisedUser){ //consider making this a loop
   try{
      String query = String.format("SELECT * FROM CONNECTION_USR WHERE status = 'Request' AND connectionId = '%s'", authorisedUser);
      int connectionReq = esql.executeQuery(query);
      if (connectionReq > 0) {
         esql.executeQueryAndPrintResult(query);
         System.out.print("\t Enter userid of connection request to respond to: ");
         String respondingTo = in.readLine();
         System.out.print("\t Enter response -Reject or Accept-: ");
         String response = in.readLine();

         String query1 = String.format("UPDATE CONNECTION_USR SET status = '" + response + "' WHERE connectionId = '" + authorisedUser + "' AND userId = '" + respondingTo + "';"); //update sytnax might be wrong have a connectionid = repondingTo
         int requests = esql.executeQuery(query1);
      }
      else{
         System.out.println("No connection requests found");
      }
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}

public static void viewMessages(ProfNetwork esql, String authorisedUser ){ 
   try{
      System.out.println();
      String query = String.format("SELECT * FROM MESSAGE WHERE ((senderId = '%s' AND (deleteStatus = 0 OR deleteStatus = 2)) OR (receiverId = '%s' AND (deleteStatus = 0 OR deleteStatus = 1))) AND (status NOT LIKE '%%Draft%%' AND status NOT LIKE '%%Failed%%');", authorisedUser, authorisedUser);
      List<List<String>> messages = esql.executeQueryAndReturnResult(query);

      int count = 0;
      System.out.println("Received messages:");
      for (int i = 0; i < messages.size(); i++) {
         if (authorisedUser.equals(messages.get(i).get(2).trim())) {
            System.out.printf("%d) FROM: %s\t TIME: %s\n %s\n", count+1, messages.get(i).get(1).trim(), messages.get(i).get(4), messages.get(i).get(6), messages.get(i).get(3));
            if (!messages.get(i).get(6).trim().equals("Read"))
               System.out.printf("\tSTATUS: Not Red\n");
            else
               System.out.printf("\tSTATUS: Read\n");

            messages.get(i).add(String.valueOf(count+1));
            count++;
         }
      }

      System.out.println("Sent messages:");
      for (int i = 0; i < messages.size(); i++) {
         if (authorisedUser.equals(messages.get(i).get(1).trim())) {
            System.out.printf("%d) TO: %s\t TIME: %s\t STATUS: %s\n %s\n", count+1, messages.get(i).get(2).trim(), messages.get(i).get(4), messages.get(i).get(6), messages.get(i).get(3));
            messages.get(i).add(String.valueOf(count+1));
            count++;
         }
      }

      System.out.println("Enter number of message to delete message: ");
      int messageCount = Integer.parseInt(in.readLine());

      for (int i = 0; i < messages.size(); i++) {
         if (messageCount == Integer.parseInt(messages.get(i).get(7))) {
            if (messages.get(i).get(2).trim().equals(authorisedUser)) {
               if (Integer.parseInt(messages.get(i).get(5).trim()) == 1) {
                  query = String.format("UPDATE MESSAGE SET deleteStatus = 3 WHERE msgId = %s;", messages.get(i).get(0));
                  messages.get(i).set(7, "-1");
                  esql.executeUpdate(query);
               }
               else {
                  query = String.format("UPDATE MESSAGE SET deleteStatus = 2 WHERE msgId = %s;", messages.get(i).get(0));
                  messages.get(i).set(7, "-1");
                  esql.executeUpdate(query);
               }
            }
            else if (messages.get(i).get(1).trim().equals(authorisedUser)) {
               if (Integer.parseInt(messages.get(i).get(5).trim()) == 2) {
                  query = String.format("UPDATE MESSAGE SET deleteStatus = 3 WHERE msgId = %s;", messages.get(i).get(0));
                  messages.get(i).set(7, "-1");
                  esql.executeUpdate(query);
               }
               else {
                  query = String.format("UPDATE MESSAGE SET deleteStatus = 1 WHERE msgId = %s;", messages.get(i).get(0));
                  messages.get(i).set(7, "-1");
                  esql.executeUpdate(query);
               }
            }
         }
      }
   }catch(Exception e){
      System.err.println (e.getMessage ());
   }
}

}//end ProfNetwork
