package com.groupo.bank.service;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/transaction")
@Produces("application/json")
public class TransactionResource {

    public Transaction getFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setTransaction_id(rs.getInt("customer_id"));
        transaction.setDescription(rs.getString("description"));
        transaction.setPost_balance(rs.getDouble("post_balance"));

        return transaction;
    }

    protected Connection getConnection() throws SQLException, NamingException {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/DSTix");
        return ds.getConnection();
    }

    public boolean addTransaction(String description, Double balance, int id) throws SQLException, NamingException {
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

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Response getTransactionById(@PathParam("id") int id, @Context UriInfo info) throws SQLException, NamingException {

        Gson gson = new Gson();
        Validator v = new Validator();
        Connection db = getConnection();

        String apiKey = info.getQueryParameters().getFirst("api_key");

        if (v.isValidAPI(apiKey)) {
            String getTransactions = ""
                    + "SELECT transaction.customer_id, transaction.description, transaction.post_balance, account.status "
                    + "FROM transaction "
                    + "INNER JOIN account "
                    + "ON transaction.customer_id = account.customer_id "
                    + "WHERE status = 1 "
                    + "AND transaction.customer_id = ?";
            PreparedStatement st = db.prepareStatement(getTransactions);
            st.setInt(1, id);
            ResultSet rs = st.executeQuery();
            List events = new ArrayList<>();
            while (rs.next()) {
                Transaction e = getFromResultSet(rs);
                events.add(e);

            }
            db.close();

            if(!events.isEmpty()){
                return Response.status(200).entity(gson.toJson(events)).build();
            } else {
                return Response.status(200).entity(gson.toJson(new APIResponse("405", "No account matching."))).build();
            }


        }

        return Response.status(200).entity(gson.toJson("Invalid API key.")).build();

    }


}
