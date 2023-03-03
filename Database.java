package carsharing;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static Database database=null;
    static final String JDBC_DRIVER = "org.h2.Driver";
    private String DB_URL = "jdbc:h2:.\\src\\carsharing\\db\\";
    private final Connection connection;

    public static Database createDB(String dbName) {
        if (database == null) {
            database = new Database(dbName);
        }
        return database;
    }
    public Database(String dbName) {
        DB_URL = DB_URL + dbName;
        connection = getConnection();
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName(JDBC_DRIVER);
            connection=DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to get connection with db, check the url" + e.getMessage());
        }
        return connection;
    }
    public void closeDB() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void createTable(){
        try(Statement statement=connection.createStatement()) {
            String sqlCompany = """
                    CREATE TABLE IF NOT EXISTS COMPANY (
                    ID INT PRIMARY KEY AUTO_INCREMENT,
                    NAME VARCHAR(24) UNIQUE NOT NULL
                    );""";
            String sqlCar= """
                    CREATE TABLE IF NOT EXISTS CAR(
                    ID INTEGER PRIMARY KEY AUTO_INCREMENT,
                    NAME VARCHAR(24) UNIQUE NOT NULL,
                    COMPANY_ID INT NOT NULL,
                    CONSTRAINT fk_COMPANY_ID FOREIGN KEY (COMPANY_ID)
                    REFERENCES COMPANY(ID)
                    );""";
            String sqlCustomer= """
                    CREATE TABLE IF NOT EXISTS CUSTOMER(
                    ID INT PRIMARY KEY AUTO_INCREMENT,
                    NAME VARCHAR(24) UNIQUE NOT NULL,
                    RENTED_CAR_ID INT DEFAULT NULL,
                    CONSTRAINT fk_RENTED_CAR_ID FOREIGN KEY (RENTED_CAR_ID)
                    REFERENCES CAR(ID)
                    );""";
            statement.executeUpdate(sqlCompany);
            statement.executeUpdate(sqlCar);
            statement.executeUpdate(sqlCustomer);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void addCompany(String name){
        String insertSQL = "INSERT INTO COMPANY(NAME) VALUES(?);";
        try(PreparedStatement statement=connection.prepareStatement(insertSQL)) {
            statement.setString(1,name);
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println("The company was created!");
        System.out.println();
    }
    public void addCar(String name,int id){
        String insertSQL="INSERT INTO CAR(NAME,COMPANY_ID) VALUES(?,?);";
        try (PreparedStatement statement=connection.prepareStatement(insertSQL)){
            statement.setString(1,name);
            statement.setString(2, String.valueOf(id));
            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println("The car was added!");
        System.out.println();
    }
    public void addCustomer(String name){
        String insertSQL = "INSERT INTO CUSTOMER(NAME) VALUES(?);";
        try (PreparedStatement statement=connection.prepareStatement(insertSQL)){
            statement.setString(1,name);
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println("The customer was added!");
        System.out.println();
    }
    public List<String> companyList(){
        List<String> nameList=new ArrayList<>();
        try(Statement statement=connection.createStatement()) {
            ResultSet resultSet=statement.executeQuery("SELECT * FROM COMPANY;");
            while (resultSet.next()){
                nameList.add(String.format("%d. %s",resultSet.getInt(1),resultSet.getString(2)));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return nameList;
    }
    public List<String> carList(String id){
        List<String> carList=new ArrayList<>();
        boolean isRent =false;
        int indexRentCar = 0;
        try (Statement statement=connection.createStatement()){
            ResultSet rsCar=statement.executeQuery(String.format("SELECT * FROM CAR WHERE COMPANY_ID=%s;",id));
            while (rsCar.next()){
                carList.add(String.format("%d. %s", carList.size()+1,rsCar.getString(2)));
            }
            ResultSet rsCustomer=statement.executeQuery("SELECT * FROM CUSTOMER;");
            while (rsCustomer.next()){
                if(rsCustomer.getString(3)!=null){
                    isRent=true;
                    indexRentCar=rsCustomer.getInt(3);
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        if(isRent){
            carList.remove(indexRentCar-1);
        }
        return carList;
    }
    public List<String> customerList(){
        List<String> customerList=new ArrayList<>();
        try (Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery("SELECT * FROM CUSTOMER;");
            while (resultSet.next()){
                customerList.add(String.format("%d. %s",resultSet.getInt(1),resultSet.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }
    public String companyName(String id){
        String sql=String.format("SELECT NAME FROM COMPANY WHERE ID = %s;",id);
        try(Statement statement=connection.createStatement()) {
            ResultSet resultSet=statement.executeQuery(sql);
            while (resultSet.first()){
                return resultSet.getString("NAME");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public String carNameOfRent(String idCompany,String idCar,String idCustomer){
        String car = null;
        String change=String.format("""
                UPDATE CUSTOMER
                SET RENTED_CAR_ID=%s
                WHERE ID=%s;""",idCar,idCustomer);
        String sql=String.format("SELECT * FROM CAR WHERE COMPANY_ID=%s AND ID=%s;",idCompany,idCar);
        try (Statement statement=connection.createStatement()){
            statement.executeUpdate(change);
            ResultSet resultSet=statement.executeQuery(sql);
            while (resultSet.next()){
                car=resultSet.getString("NAME");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return car;
    }
    public void returnCar(String idCustomer){
        String sql=String.format("""
                UPDATE CUSTOMER
                SET RENTED_CAR_ID=NULL
                WHERE ID=%s""",idCustomer);
        try (Statement statement=connection.createStatement()){
            statement.executeUpdate(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public boolean isRent(String idCustomer){
        String sql=String.format("SELECT * FROM CUSTOMER WHERE ID=%s",idCustomer);
        try (Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery(sql);
            while (resultSet.next()){
                if(resultSet.getString(3)!=null){
                    return true;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}