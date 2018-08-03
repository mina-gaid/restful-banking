package com.groupo.bank.service;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/transfer")
@Produces("application/json")
public class TransferResource {

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    private boolean addTransaction(String description, Double balance, int id) throws SQLException, NamingException {
        String t = "INSERT INTO transaction (description, post_balance, customer_id) VALUES (?,?,?);";
        Connection db = getConnection();
        PreparedStatement s = db.prepareStatement(t, Statement.RETURN_GENERATED_KEYS);
        s.setString(1, description);
        s.setDouble(2, balance);
        s.setInt(3, id);
        s.executeUpdate();
        ResultSet rs = s.getGeneratedKeys();
        return rs.next();
    }

    @POST
    @Path("/create")
    @Produces("application/json")
    public Response createTransfer(@Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();

        String apiKey = info.getQueryParameters().getFirst("api_key");
        String from = info.getQueryParameters().getFirst("from");
        String to = info.getQueryParameters().getFirst("to");
        double amount = Double.parseDouble(info.getQueryParameters().getFirst("amount"));
        Validator v = new Validator();
        Connection db = getConnection();
        if (v.isValidAPI(apiKey) && v.isValidAccountNumber(from) && v.isValidAccountNumber(to)) {
            if (v.isValidAPI(apiKey)) {
                PreparedStatement p = db.prepareStatement("SELECT status from account where account_number = ?");
                p.setString(1, from);
                ResultSet rs = p.executeQuery();
                if (rs.next()) {
                    int status = rs.getInt("status");
                    if (status == 1) {
                        if (v.hasSufficentFunds(from, amount)) {
                            String updateBalance = "UPDATE account SET current_balance = current_balance + ? WHERE account_number = ?";
                            PreparedStatement st3 = db.prepareStatement(updateBalance);
                            st3.setDouble(1, amount);
                            st3.setString(2, to);
                            st3.executeUpdate();

                            String updateSenderBalance = "UPDATE account SET current_balance = current_balance - ? WHERE account_number = ?";
                            PreparedStatement st4 = db.prepareStatement(updateSenderBalance);
                            st4.setDouble(1, amount);
                            st4.setString(2, from);
                            st4.executeUpdate();

                            // update transaction
                            PreparedStatement ps = db.prepareStatement("SELECT * FROM account WHERE account_number = ?");
                            ps.setString(1, from);
                            ResultSet s = ps.executeQuery();
                            if (s.next()) {
                                int id = s.getInt("customer_id");
                                double balance = s.getDouble("current_balance");

                                TransactionResource t = new TransactionResource();
                                t.addTransaction("Transfer", balance, id);
                            }

                            return Response.status(200).entity(gson.toJson(new APIResponse("200", "Transfer successful."))).build();
                        } else {

                            return Response.status(200).entity(gson.toJson(new APIResponse("500", "The sender has insufficient funds to make this transfer."))).build();
                        }
                    } else {
                        return Response.status(200).entity(gson.toJson(new APIResponse("405", "Cant send money from a removed account."))).build();

                    }
                }

            } else {
                return Response.status(200).entity(gson.toJson(new APIResponse("500", "Invalid API."))).build();
            }

        }
        return null;
    }
}
