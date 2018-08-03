package com.groupo.bank.service;

import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/customer")
@Produces("application/json")
public class CustomerResource {

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    public Customer getFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerID(rs.getInt("customer_id"));
        customer.setEmail(rs.getString("email"));
        customer.setName(rs.getString("name"));
        return customer;
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response getCustomerById(@PathParam("id") int id, @Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();
        Validator v = new Validator();
        Connection db = getConnection();

        String apiKey = info.getQueryParameters().getFirst("api_key");

        if (v.isValidAPI(apiKey)) {
            PreparedStatement p = db.prepareStatement("SELECT status from account where customer_id = ?");
            p.setInt(1, id);
            ResultSet s = p.executeQuery();

            if (s.next()) {
                int status = s.getInt("status");
                if (status == 1) {
                    String verifyAPI = "SELECT * FROM customer WHERE customer_id = ?";
                    PreparedStatement st = db.prepareStatement(verifyAPI);
                    st.setInt(1, id);
                    ResultSet rs = st.executeQuery();
                    List events = new ArrayList<>();
                    if (rs.next()) {
                        Customer e = getFromResultSet(rs);
                        events.add(e);

                    }
                    db.close();
                    return Response.status(200).entity(gson.toJson(events)).build();
                } else {
                    return Response.status(200).entity(gson.toJson(new APIResponse("Error", "The account has been removed"))).build();
                }
            }

        }

        return null;

    }

    private boolean isSavingsAccount(String accountNumber) throws SQLException, NamingException {
        PreparedStatement st;
        Connection db = getConnection();
        st = db.prepareStatement("SELECT * FROM customer AS c JOIN account AS a ON c.customer_id = a.customer_id WHERE account_type = 2 AND account_number = ?");
        st.setString(1, accountNumber);
        ResultSet rs2 = st.executeQuery();
        boolean isValid = rs2.next();
        db.close();
        return isValid;
    }

    private boolean isCurrentAccount(String accountNumber) throws SQLException, NamingException {
        PreparedStatement st;
        Connection db = getConnection();
        st = db.prepareStatement("SELECT * FROM customer AS c JOIN account AS a ON c.customer_id = a.customer_id WHERE account_type = 1 AND account_number = ?");
        st.setString(1, accountNumber);
        ResultSet rs2 = st.executeQuery();
        boolean isValid = rs2.next();
        db.close();
        return isValid;
    }

    @POST
    @Path("/add-account")
    @Produces("application/json")
    public Response addCustomerAccountType(@Context UriInfo info) throws SQLException, NamingException {
        Gson gson = new Gson();
        Validator v = new Validator();
        String apiKey = info.getQueryParameters().getFirst("api_key");
        String account = info.getQueryParameters().getFirst("account");
        String accountType;

        if (!(v.isValidAPI(apiKey))) {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API."))).build();
        }

        try {
            accountType = info.getQueryParameters().getFirst("account_type");
            if (accountType.equalsIgnoreCase("Current")) {
                accountType = "1";
            } else {
                accountType = "2";
            }

        } catch (java.lang.NullPointerException e) {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "No account type specified."))).build();
        }

        if (v.isValidAccountNumber(account)) {

            boolean hasC = isCurrentAccount(account);
            boolean hasS = isSavingsAccount(account);

            if (hasC && hasS) {
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "User has already a current and savings account."))).build();
            }

            if (accountType.equals("1") && hasC) {
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "User has a current account."))).build();
            } else if (accountType.equals("2") && hasS) {
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "User has a savings account."))).build();
            } else {

                PreparedStatement st;
                Connection db = getConnection();
                st = db.prepareStatement("SELECT * FROM account WHERE account_number = ?");
                st.setString(1, account);
                ResultSet rs2 = st.executeQuery();

                while (rs2.next()) {
                    String aid = rs2.getString("customer_id");
                    System.out.println(aid);
                    String sort = UUID.randomUUID().toString().substring(0, 8);
                    String an = UUID.randomUUID().toString().substring(0, 8);
                    int balance = 0;

                    PreparedStatement st3;
                    st3 = db.prepareStatement("SELECT * FROM account WHERE account_type = ? AND customer_id = ?");
                    st3.setInt(1, Integer.parseInt(accountType));
                    st3.setInt(2, Integer.parseInt(aid));
                    ResultSet rs3 = st3.executeQuery();

                    if (rs3.next()) {
                        System.out.println(rs3.getInt("account_type"));
                        return Response.status(200).entity(gson.toJson(new APIResponse("200", "You cant add another savings account."))).build();
                    } else {
                        String insertNewAccount = "INSERT INTO account"
                                + "(customer_id, sort_code, account_number, current_balance, account_type) VALUES"
                                + "(?,?,?,?, ?)";

                        PreparedStatement stm = db.prepareStatement(insertNewAccount);
                        stm.setInt(1, Integer.parseInt(aid));
                        stm.setString(2, sort);
                        stm.setString(3, an);
                        stm.setDouble(4, balance);
                        stm.setString(5, accountType);
                        stm.executeUpdate();

                        return Response.status(200).entity(gson.toJson(new APIResponse("200", "Account added"))).build();
                    }

                }

            }

        }

        return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API key"))).build();

    }

    @GET
    @Path("/")
    @Produces("application/json")
    public Response getCustomerList(@Context UriInfo info) throws SQLException, NamingException {
        Gson gson = new Gson();
        Validator v = new Validator();

        String apiKey = info.getQueryParameters().getFirst("api_key");

        if (v.isValidAPI(apiKey)) {
            List events = new ArrayList<>();
            Connection db = getConnection();

            try {
                PreparedStatement st = db.prepareStatement("SELECT customer.customer_id, customer.email, customer.name, account.customer_id, account.status FROM customer INNER JOIN account ON customer.customer_id = account.customer_id WHERE status = 1");
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    Customer e = getFromResultSet(rs);
                    events.add(e);
                }
                return Response.status(200).entity(gson.toJson(events)).build();
            } finally {
                db.close();
            }
        } else {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Invalid API key"))).build();
        }

    }

    @POST
    @Path("/create")
    @Produces("application/json")
    public Response createCustomer(@Context UriInfo info) throws SQLException, NamingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        Gson gson = new Gson();
        Connection db = getConnection();

        String name = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("name"), "UTF-8");
        String email = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("email"), "UTF-8");
        String address = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("address"), "UTF-8");
        String password = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("password"), "UTF-8");
        String apiKey = java.net.URLDecoder.decode(info.getQueryParameters().getFirst("api_key"), "UTF-8");

        if (name.equals("") || email.equals("") || address.equals("") || password.equals("")) {
            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Form fields can not be empty."))).build();
        }

        Validator v = new Validator();

        if (v.isValidAPI(apiKey)) {

            String generatedPassword;

            PasswordEncryptionService pes = new PasswordEncryptionService();

            byte[] salt = pes.generateSalt();
            byte[] securePassword = pes.getEncryptedPassword(password, salt);

            generatedPassword = new String(securePassword);
            String saltString = new String(salt);

            String accountType;
            try {
                accountType = info.getQueryParameters().getFirst("account_type");
                if (accountType.equalsIgnoreCase("Current")) {
                    accountType = "1";
                } else {
                    accountType = "2";
                }

            } catch (java.lang.NullPointerException e) {
                return Response.status(200).entity(gson.toJson(new APIResponse("200", "No account type specified."))).build();
            }

            String sort = UUID.randomUUID().toString().substring(0, 8);
            String account = UUID.randomUUID().toString().substring(0, 8);

            int balance = 0;

            try {
                String insertCustomer = "INSERT INTO customer"
                        + "(name, email, address, password, salt) VALUES"
                        + "(?,?,?,?,?)";

                String createAccount = "INSERT INTO account"
                        + "(sort_code, account_number, current_balance, account_type, customer_id) VALUES"
                        + "(?,?,?,?,?)";

                PreparedStatement st = db.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS);
                st.setString(1, name);
                st.setString(2, email);
                st.setString(3, address);
                st.setString(4, generatedPassword);
                st.setString(5, saltString);
                st.executeUpdate();

                // get the last insert ID
                int lastInsertId = 0;
                ResultSet rs = st.getGeneratedKeys();

                if (rs.next()) {
                    lastInsertId = rs.getInt(1);
                }

                PreparedStatement stm = db.prepareStatement(createAccount);
                stm.setString(1, sort);
                stm.setString(2, account);
                stm.setInt(3, balance);
                stm.setString(4, accountType);
                stm.setInt(5, lastInsertId);
                stm.executeUpdate();

                return Response.status(200).entity(gson.toJson(new APIResponse("200", "Customer created successfully."))).build();
            } finally {
                db.close();
            }
        } else {
            return Response.status(200).entity(gson.toJson(new APIResponse("500", "Invalid API key."))).build();
        }

    }

    @GET
    @Path("delete/{id}")
    @Produces("application/json")
    public Response deleteCustomerById(@PathParam("id") int id, @Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();
        Validator v = new Validator();
        Connection db = getConnection();

        String apiKey = info.getQueryParameters().getFirst("api_key");

        if (v.isValidAPI(apiKey)) {
            PreparedStatement p = db.prepareStatement("SELECT status from account where customer_id = ?");
            p.setInt(1, id);
            ResultSet rs = p.executeQuery();

            if (rs.next()) {
                int status = rs.getInt("status");
                if (status == 1) {

                    String deleteCustomer = "UPDATE account SET status = 0 WHERE customer_id = ?";
                    PreparedStatement st = db.prepareStatement(deleteCustomer);
                    st.setInt(1, id);
                    st.executeUpdate();
                    db.close();
                    return Response.status(200).entity(gson.toJson(new APIResponse("200", "Account has been removed"))).build();

                } else {
                    return Response.status(200).entity(gson.toJson(new APIResponse("500", "This account has already been removed."))).build();
                }
            }

        } else {
            return Response.status(200).entity(gson.toJson(new APIResponse("500", "Invalid API Key."))).build();
        }
        return null;
    }
}
