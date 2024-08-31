package ca.jrvs.apps.jdbc.practice;

import ca.jrvs.apps.jdbc.util.DatabaseConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCExecutor {
    public static void main(String[] args) {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager("localhost", "hplussport",
                "postgres", "password");

        try {
            Connection connection = dcm.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);

            // get
//            Customer customer = customerDAO.findById(1000);
//            System.out.println(customer.getFirstName() + " " + customer.getLastName());

            // create
//            Customer customer = new Customer();
//            customer.setFirstName("Philip");
//            customer.setLastName("Barns");
//            customer.setEmail("testing@email.com");
//            customer.setAddress("12 Main St");
//            customer.setCity("Mississauga");
//            customer.setState("ON");
//            customer.setPhone("(647) 555-6545");
//            customer.setZipCode("22121");
//
//            customerDAO.create(customer);

            //update

//            Customer customer = customerDAO.findById(10000);
//            System.out.println(customer.getFirstName() + " " + customer.getLastName() + " " +
//                    customer.getEmail());
//            customer.setEmail("gwashington@wh.gov");
//            customer = customerDAO.update(customer);
//            System.out.println(customer.getFirstName() + " " + customer.getLastName() + " " +
//                    customer.getEmail());

            // delete
            Customer customer = new Customer();
            customer.setFirstName("John");
            customer.setLastName("Adams");
            customer.setEmail("jadams.wh.gov");
            customer.setAddress("1234 Main St");
            customer.setCity("Arlington");
            customer.setState("VA");
            customer.setPhone("(555) 555-9845");
            customer.setZipCode("01234");

            Customer dbCustomer = customerDAO.create(customer);
            System.out.println(dbCustomer);
            dbCustomer = customerDAO.findById(dbCustomer.getId());
            System.out.println(dbCustomer);
            dbCustomer.setEmail("john.adams@wh.gov");
            dbCustomer = customerDAO.update(dbCustomer);
            System.out.println(dbCustomer);
            customerDAO.delete(dbCustomer.getId());

        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
