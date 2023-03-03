package carsharing;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Menu {
    public static Scanner scanner=new Scanner(System.in);
    public Database database;
    public boolean run;
    public boolean isRent;
    public String nameRentCar;
    public String nameCompanyOfRentCar;
    public String idCustomerRenter;
    public Menu(String dbName){
        run=true;
        database=Database.createDB(dbName);
        database.createTable();
    }
    public void mainMenu(){
        while (run){
            System.out.println("""
                    1. Log in as a manager
                    2. Log in as a customer
                    3. Create a customer
                    0. Exit""");
            String choose=scanner.nextLine();
            switch (choose){
                case "0"->{
                    database.closeDB();
                    run=false;
                }
                case "1"->managerMenu();
                case "2"->customerMenu();
                case "3"->{
                    System.out.println("Enter the customer name:");
                    String name =scanner.nextLine();
                    database.addCustomer(name);
                }
            }
        }
    }
    public void managerMenu(){
        while (run){
            System.out.println("""
                    1. Company list
                    2. Create a company
                    0. Back""");
            switch (scanner.nextLine()){
                case "0"->mainMenu();
                case "1"->companyMenu();
                case "2"->{
                    System.out.println("Enter the company name:");
                    String name =scanner.nextLine();
                    database.addCompany(name);
                }
            }
        }
    }
    public void companyMenu(){
        String id=companyChooseList();
        if(id.equals("0")) {
            managerMenu();
        }else {
            while (run){
                System.out.printf("""
                        '%s' company
                        1. Car list
                        2. Create a car
                        0. Back""",database.companyName(id));
                System.out.println();
                switch (scanner.nextLine()){
                    case "0"->managerMenu();
                    case "1"->{
                        System.out.println("Car list:");
                        outputCarList(id);
                    }
                    case "2"->{
                        System.out.println("Enter the car name:");
                        String name=scanner.nextLine();
                        database.addCar(name,Integer.parseInt(id));
                    }
                }
            }
        }
    }
    public void customerMenu(){
        idCustomerRenter=customerChooseList();
        if(idCustomerRenter.equals("0")) {
            mainMenu();
        }else {
            while (run){
                isRent=database.isRent(idCustomerRenter);
                System.out.println("""
                        1. Rent a car
                        2. Return a rented car
                        3. My rented car
                        0. Back""");
                switch (scanner.nextLine()){
                    case "0"->mainMenu();
                    case "1"->rentCar();
                    case "2"->returnCar();
                    case "3"->infoAboutRentCar();
                }
            }
        }
    }
    public String customerChooseList(){
        List<String> customerList=database.customerList();
        if(customerList.isEmpty()){
            System.out.println("The customer list is empty!");
            return "0";
        }else {
            System.out.println("Customer list:");
            customerList.forEach(System.out::println);
            System.out.println("0. Back");
            return scanner.nextLine();
        }
    }
    public String companyChooseList(){
        List<String> nameList=database.companyList();
        if(nameList.isEmpty()){
            System.out.println("The company list is empty!");
            return "0";
        }else {
            System.out.println("Choose the company:");
            Collections.sort(nameList);
            nameList.forEach(System.out::println);
            System.out.println("0. Back");
            return scanner.nextLine();
        }
    }

    public void outputCarList(String id){
        List<String> carList=database.carList(id);
        if(carList.isEmpty()){
            System.out.println("The car list is empty!");
        }else {
            carList.forEach(System.out::println);
        }
    }
    public void rentCar(){
        if(isRent) {
            System.out.println("You've already rented a car!");
        }else {
            String idCompany=companyChooseList();
            if(idCompany.equals("0")){
                customerMenu();

            }else if(database.carList(idCompany).isEmpty()){
                System.out.printf("No available cars in the '%s' company",database.companyName(idCompany));
            }else{
                System.out.println("Choose a car");
                outputCarList(idCompany);
                String idCar= scanner.nextLine();
                isRent=true;
                nameRentCar=database.carNameOfRent(idCompany,idCar,idCustomerRenter);
                nameCompanyOfRentCar=database.companyName(idCompany);
                System.out.printf("You rented '%s'",nameRentCar);
                System.out.println();
                System.out.println();
            }
        }
    }
    public void returnCar(){
        if(!isRent) {
            System.out.println("You didn't rent a car!");
        }else {
            isRent=false;
            database.returnCar(idCustomerRenter);
            System.out.println("You've returned a rented car!");
        }
    }
    public void infoAboutRentCar(){
        if(!isRent){
            System.out.println("You didn't rent a car!");
        }else {
            System.out.printf("""
                    Your rented car:
                    %s
                    Company:
                    %s""",nameRentCar,nameCompanyOfRentCar);
        }
        System.out.println();
        System.out.println();
    }
}
