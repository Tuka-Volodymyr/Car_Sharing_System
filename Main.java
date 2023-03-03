package carsharing;
public class Main {
    public static void main(String[] args) {
        String dbName = "example";
        if ("-databaseFileName".equals(args[0])) {
            dbName = args[1];
        }
        Menu menu =new Menu(dbName);
        menu.mainMenu();
    }
}

