package dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private Connection connection;
    private static DBConnection instance;

    private DBConnection(){
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream("db.properties"));
            String dburl = properties.getProperty("dburl");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            connection = DriverManager.getConnection(dburl, user, password);
        }
        catch (FileNotFoundException e){
            System.out.println("Problem z wczytaniem pliku z danymi.");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("Problem z wczytaniem pliku z danymi.");
            e.printStackTrace();
        }
        catch (SQLException e){
            System.out.println("Problem z logowaniem.");
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance(){
        if(instance == null){
            instance = new DBConnection();
        }
        return instance;
    }

    public ResultSet select(String query){
        Statement statement;
        ResultSet rst = null;
        try {
            statement = connection.createStatement();
            rst = statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rst;
    }

    public void createTableCategories() {
        try {
            Connection con = getInstance().connection;
            PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS kategorie(id int NOT NULL AUTO_INCREMENT," +
                    " nazwa_kategorii varchar(255), PRIMARY KEY(id))"); // id samo się zwiększa, kluczem jest id
            create.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
        finally {
            System.out.println("Function createTableCategories complete");
        }
    }

    public void createTableProducts() {
        try {
            Connection con = getInstance().connection;
            PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS produkty(id int NOT NULL AUTO_INCREMENT," +
                    " foreign_key int(255), nazwa_produktu varchar(255),  masa float, cena float, PRIMARY KEY(id))");
            create.executeUpdate();
        } catch (Exception e){
            System.out.println("createTableProduct " + e);
        }
        finally {
            System.out.println("Function createTableProducts complete");
        }
    }

    public void postCategory(String name) {
        try{
            Connection con = getInstance().connection;
            PreparedStatement post = con.prepareStatement("INSERT INTO kategorie(nazwa_kategorii) VALUES('"+name+"')");
            post.executeUpdate();
        }catch (Exception e) { System.out.println(e); }
        finally {
            System.out.println("Dodano kategorię.");
        }
    }

    public void postProduct(int foreign_key, String name, float mass, float price) {
        try{
            Connection con = getInstance().connection;
            PreparedStatement post = con.prepareStatement("INSERT INTO produkty(foreign_key, nazwa_produktu, masa, cena)" +
                    "VALUES('"+foreign_key+"', '"+name+"', '"+mass+"', '"+price+"')");  // można też INSERT INTO produkty -> bez podawania
                                                                                        // nazw kolumn, wtedy wypełni wszystkie
            post.executeUpdate();
        } catch (Exception e) { System.out.println("postProduct " + e); }
        finally {
            System.out.println("Dodano produkt");
        }
    }

    // usunięcie produktu
    public void deleteProduct(String name) {
        try {
            Connection con = getInstance().connection;
            PreparedStatement delete = con.prepareStatement("DELETE FROM produkty WHERE nazwa_produktu = '" + name + "'");
            delete.executeUpdate();
        } catch (Exception e) { System.out.println("deleteProduct " + e); }
        finally {
            System.out.println("Usunięto produkt");
        }
    }

    // metoda zwracająca id wybranej kategorii
    public int categoryId(String name){
        int category = 0;
        try {
            Connection con = getInstance().connection;
            PreparedStatement whichCategory = con.prepareStatement("SELECT id FROM kategorie WHERE nazwa_kategorii = '" + name + "'");
            ResultSet rs = whichCategory.executeQuery();
            while (rs.next()) {
                category = rs.getInt("id");
            }
        } catch (Exception e) { System.out.println(e); }
        System.out.println(category);
        return category;
    }

    // usunięcie kategorii
    public void deleteCategory(String name){
        try {
            deleteFromCategory(name);
            Connection con = getInstance().connection;
            PreparedStatement delete = con.prepareStatement("DELETE FROM kategorie WHERE nazwa_kategorii = '" + name + "'");
            delete.executeUpdate();
        } catch (Exception e) { System.out.println("deleteCategory " + e); }
    }

    // usunięcie wszystich produktów z kategorii
    public void deleteFromCategory(String name){
        int category = categoryId(name);
        try {
            Connection con = getInstance().connection;
            PreparedStatement delete = con.prepareStatement("DELETE FROM produkty WHERE foreign_key = ?");
            delete.setInt(1, category);
            delete.executeUpdate();
        } catch (Exception e) { System.out.println("deleteFromCategory " + e); }
    }

    public void destroyConnection(boolean b, ResultSet rst){
        if(b){
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            if(rst != null){
                try {
                    rst.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }

    public void destroy(ResultSet rst) {destroyConnection(false, rst);}
}
