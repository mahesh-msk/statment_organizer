

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class TallyODBC {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO codimporte application logic here
        try {
              Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

              Connection con = DriverManager.getConnection("jdbc:odbc:TallyODBC64_9000", "", "");
              Statement stmt = (Statement) con.createStatement();
              ResultSet rs = stmt.executeQuery("SELECT * FROM Ledger");
  
//              ResultSet rs = stmt.executeQuery("SELECT * FROM sys.Tables");
              int numberOfColumns = rs.getRow();
              System.out.println(numberOfColumns);
//              int rowCount = 1;
//              while (rs.next()) { 
//                  //for(int i = 0; i < 7; i++){
//                       int op = Integer.parseInt(rs.getString("StockItem.`$OpeningBalance`").substring(0, (rs.getString("StockItem.`$OpeningBalance`").length() - 3)).trim());
//                       int in = Integer.parseInt(rs.getString("StockItem.`$_InwardQuantity`").substring(0, (rs.getString("StockItem.`$_InwardQuantity`").length() - 3)).trim());
//                       int out = Integer.parseInt(rs.getString("StockItem.`$_OutwardQuantity`").substring(0, (rs.getString("StockItem.`$_OutwardQuantity`").length() - 3)).trim());
//                       int cl = Integer.parseInt(rs.getString("StockItem.`$_ClosingBalance`").substring(0, (rs.getString("StockItem.`$_ClosingBalance`").length() - 3)).trim());
//                       System.out.println(rs.getString("StockItem.`$Parent`") + " \t " + rs.getString("StockItem.`$Name`") + " \t " + rs.getString("StockItem.`$BaseUnits`") + " \t " + op + " \t " + in + " \t " + out + " \t " + cl);
//                  //}             
//                rowCount++;
//              }
              //System.out.println(rowCount);
              stmt.close();
              con.close();
          } catch (Exception e) {
          System.out.println(e);
        }
    }
}